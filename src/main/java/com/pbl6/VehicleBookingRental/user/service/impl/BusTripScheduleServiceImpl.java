package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.*;
import com.pbl6.VehicleBookingRental.user.dto.Meta;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBusTripScheduleDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripScheduleDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripScheduleDetailForAdminDTO;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BreakDayRepository;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusTripRepository;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusTripScheduleRepository;
import com.pbl6.VehicleBookingRental.user.service.*;
import com.pbl6.VehicleBookingRental.user.util.CurrencyFormatterUtil;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusTripScheduleServiceImpl implements BusTripScheduleService {
    private final BusTripScheduleRepository busTripScheduleRepository;
    private final BusTripRepository busTripRepository;
    private final BusService busService;
    private final BreakDayRepository breakDayRepository;
    private final BusinessPartnerService businessPartnerService;
    @Override
    public BusTripSchedule createBusTripSchedule(ReqBusTripScheduleDTO reqBusTripScheduleDTO) throws IdInvalidException, ApplicationException {

        BusTripSchedule busTripSchedule = new BusTripSchedule();

        BusTrip busTrip = this.busTripRepository.findById(reqBusTripScheduleDTO.getBusTripId())
                .orElseThrow(()-> new IdInvalidException("BusTrip not found"));
        busTripSchedule.setBusTrip(busTrip);

        // Get the bus
        Bus bus = this.busService.findBusById(reqBusTripScheduleDTO.getBusId());


        // Kiểm tra trường hợp lúc thêm mà ko có break days

        // Check valid departure time of new bus trip schedule
        List<BusTripSchedule> busTripSchedulesOfBus = bus.getBusTripSchedules();
        for(BusTripSchedule busTripScheduleOfBus : busTripSchedulesOfBus) {
            Duration durationJourney = busTripScheduleOfBus.getBusTrip().getDurationJourney();

            LocalTime existingDepartureTime = busTripScheduleOfBus.getDepartureTime();

            // Check new bus trip schedule has same route (bus trip)
            Duration timeDifference = Duration.between(existingDepartureTime, reqBusTripScheduleDTO.getDepartureTime());
            if(reqBusTripScheduleDTO.getBusTripId() == busTripScheduleOfBus.getBusTrip().getId()){
                if (timeDifference.toHours() < 2 * durationJourney.toHours()) {
                    throw new ApplicationException("New schedule conflicts with existing schedule. Must be at least 2 durations apart.");
                }
                        //.plus(Duration.ofMinutes(30))
            }else{
                // In case different route
                LocalTime requiredNextDepartureTime = busTripScheduleOfBus.getDepartureTime().plus(durationJourney);

                if(!reqBusTripScheduleDTO.getDepartureTime().isAfter(requiredNextDepartureTime)) {
                    throw new ApplicationException("New schedule conflicts with existing schedule");
                }
            }


        }
        busTripSchedule.setBus(bus);
        busTripSchedule.setAvailableSeats(bus.getBusType().getNumberOfSeat());

        busTripSchedule.setDepartureTime(reqBusTripScheduleDTO.getDepartureTime());
        busTripSchedule.setDiscountPercentage(reqBusTripScheduleDTO.getDiscountPercentage());
        busTripSchedule.setPriceTicket(reqBusTripScheduleDTO.getPriceTicket());
        busTripSchedule.setStartOperationDay(reqBusTripScheduleDTO.getStartOperationDay());

        if(reqBusTripScheduleDTO.getStartOperationDay().isEqual(LocalDate.now()) || reqBusTripScheduleDTO.getStartOperationDay().isBefore(LocalDate.now())) {
            busTripSchedule.setOperation(true);
        }

        // Check breaDays is null ?
        if(reqBusTripScheduleDTO.getBreakDays()!=null && !reqBusTripScheduleDTO.getBreakDays().isEmpty()) {
            List<BreakDay> breakDays = reqBusTripScheduleDTO.getBreakDays().stream()
                    .map(breakDay -> {
                        return BreakDay.builder()
                                .startDay(breakDay.getStartDay())
                                .endDay(breakDay.getEndDay())
                                .busTripSchedule(busTripSchedule)  // Set BusTripSchedule for BreakDay
                                .build();
                    }).toList();

           busTripSchedule.setBreakDays(breakDays);
        }


        return this.busTripScheduleRepository.save(busTripSchedule);
    }

    @Override
    public ResBusTripScheduleDetailForAdminDTO convertToResBusTripScheduleDetailDTO(BusTripSchedule busTripSchedule) {
        ResBusTripDTO.BusTripInfo busTripInfo = ResBusTripDTO.BusTripInfo.builder()
                .id(busTripSchedule.getBusTrip().getId())
                .departureLocation(busTripSchedule.getBusTrip().getDepartureLocation())
                .arrivalLocation(busTripSchedule.getBusTrip().getArrivalLocation())
                .durationJourney(busTripSchedule.getBusTrip().getDurationJourney())
                .build();
        ResBusTripScheduleDetailForAdminDTO.BusInfo busInfo = ResBusTripScheduleDetailForAdminDTO.BusInfo.builder()
                .licensePlate(busTripSchedule.getBus().getLicensePlate())
                .busType(busTripSchedule.getBus().getBusType())
                .build();

        ResBusTripScheduleDetailForAdminDTO res = ResBusTripScheduleDetailForAdminDTO.builder()
                .idBusTripSchedule(busTripSchedule.getId())
                .busTripInfo(busTripInfo)
                .busInfo(busInfo)
                .departureTime(busTripSchedule.getDepartureTime())
                .arrivalTime(busTripSchedule.getDepartureTime().plus(busTripSchedule.getBusTrip().getDurationJourney()))
                .discountPercentage(busTripSchedule.getDiscountPercentage())
                .priceTicket(CurrencyFormatterUtil.formatToVND(busTripSchedule.getPriceTicket()))
                .startOperationDay(busTripSchedule.getStartOperationDay())
                .availableSeats(busTripSchedule.getAvailableSeats()) // Có thể thay thế sau này khi order
                .breakDays(busTripSchedule.getBreakDays())
                .isOperation(busTripSchedule.isOperation())
                .build();
        return res;
    }

    @Override
    public ResBusTripScheduleDetailForAdminDTO getBusTripScheduleById(int id) throws IdInvalidException {
        BusTripSchedule busTripSchedule = this.busTripScheduleRepository.findById(id)
                .orElseThrow(()-> new IdInvalidException("BusTrip not found"));
        log.info("Get busTripSchedule with id {} from mysql ", id);
        return this.convertToResBusTripScheduleDetailDTO(busTripSchedule);
    }

    @Override
    public ResBusTripScheduleDTO convertToResBusTripScheduleDTO(BusTripSchedule busTripSchedule) throws IdInvalidException {
        ResBusDTO resBusDTO = this.busService.convertToResBus(busTripSchedule.getBus());

        ResBusTripDTO.BusTripInfo busTripInfo = ResBusTripDTO.BusTripInfo.builder()
                .id(busTripSchedule.getBusTrip().getId())
                .departureLocation(busTripSchedule.getBusTrip().getDepartureLocation())
                .arrivalLocation(busTripSchedule.getBusTrip().getArrivalLocation())
                .durationJourney(busTripSchedule.getBusTrip().getDurationJourney())
                .build();
        ResBusTripScheduleDTO.BusinessPartnerInfo businessPartnerInfo = ResBusTripScheduleDTO.BusinessPartnerInfo.builder()
                .id(busTripSchedule.getBusTrip().getBusPartner().getBusinessPartner().getId())
                .name(busTripSchedule.getBusTrip().getBusPartner().getBusinessPartner().getBusinessName())
                .build();
        ResBusTripScheduleDTO res = ResBusTripScheduleDTO.builder()
                .busTripScheduleId(busTripSchedule.getId())
                .businessPartnerInfo(businessPartnerInfo)
                .busInfo(resBusDTO)
                .busTripInfo(busTripInfo)
                .departureTime(busTripSchedule.getDepartureTime())
                .discountPercentage(busTripSchedule.getDiscountPercentage())
                .priceTicket(CurrencyFormatterUtil.formatToVND(busTripSchedule.getPriceTicket()))
                .arrivalTime(busTripSchedule.getDepartureTime().plus(busTripInfo.getDurationJourney()))
                .availableSeats(busTripSchedule.getAvailableSeats())
                .isOperation(busTripSchedule.isOperation())
                .build();

//        res.setArrivalTime(busTripInfo.getD);

        return res;
    }


    @Override
    public ResultPaginationDTO getAllBusTripSchedules(Specification<BusTripSchedule> spec, Pageable pageable) throws ApplicationException {


        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);

        Specification<BusTripSchedule> newSpec = (root, query, criteriaBuilder) -> {
            Join<BusTripSchedule, BusTrip> joinBus = root.join("busTrip");
            Join<BusTrip, BusPartner> joinBusPartner = joinBus.join("busPartner");
            return criteriaBuilder.equal(joinBusPartner.get("id"), businessPartner.getBusPartner().getId());
        };

        Specification<BusTripSchedule> finalSpec = spec.and(newSpec);

        Page<BusTripSchedule> busTripSchedulePage = this.busTripScheduleRepository.findAll(finalSpec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();

        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(busTripSchedulePage.getTotalPages());
        meta.setTotal(busTripSchedulePage.getTotalElements());

        res.setMeta(meta);

        List<ResBusTripScheduleDTO> resBusTripScheduleDTOS = busTripSchedulePage.getContent().stream()
                .map(busTripSchedule -> {
                    try {
                        return convertToResBusTripScheduleDTO(busTripSchedule);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        res.setResult(resBusTripScheduleDTOS);

        return res;
    }

    @Override
    public ResultPaginationDTO getAllBusTripScheduleAvailableForUser(Specification<BusTripSchedule> spec, Pageable pageable, LocalDate departureDate) throws ApplicationException {
        if(departureDate == null) {
            departureDate = LocalDate.now();
//            log.info("Departure: " + departureDate);
        }
        List<Long> validIds = busTripScheduleRepository.findBusTripScheduleIdsNotInBreakDays(departureDate);
        Specification<BusTripSchedule> newSpec = (root, query, criteriaBuilder) -> {
            Predicate predicateOperationDay = criteriaBuilder.lessThanOrEqualTo(root.get("startOperationDay"), LocalDate.now());
            Predicate predicateStatusOperation = criteriaBuilder.equal(root.get("isOperation"), true);
            Predicate predicateValidIds = root.get("id").in(validIds); // Chỉ lấy các ID hợp lệ từ truy vấn

            return criteriaBuilder.and(predicateOperationDay, predicateStatusOperation, predicateValidIds);
        };
        Specification<BusTripSchedule> finalSpec = spec.and(newSpec);
        Page<BusTripSchedule> busTripSchedulePage = this.busTripScheduleRepository.findAll(finalSpec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();

        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(busTripSchedulePage.getTotalPages());
        meta.setTotal(busTripSchedulePage.getTotalElements());

        res.setMeta(meta);

        List<ResBusTripScheduleDTO> resBusTripScheduleDTOS = busTripSchedulePage.getContent().stream()
                .map(busTripSchedule -> {
                    try {
                        return convertToResBusTripScheduleDTO(busTripSchedule);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        res.setResult(resBusTripScheduleDTOS);
        return res;
    }

    @Override
    public ResBusTripScheduleDTO getBusTripScheduleByIdForUser(int busTripScheduleId) throws IdInvalidException {
        BusTripSchedule busTripSchedule = this.busTripScheduleRepository.findById(busTripScheduleId)
                .orElseThrow(() -> new IdInvalidException("Bus trip schedule not found"));

        return this.convertToResBusTripScheduleDTO(busTripSchedule);
    }

    // @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    @Scheduled(cron = "0 */2 * * * *")
    public void updateBusTripScheduleStatus() {
        LocalDate today = LocalDate.now();
        log.info("Today is: " + today);
        List<BusTripSchedule> busTripSchedules = busTripScheduleRepository.findSchedulesBeforeToday(today);
        List<BusTripSchedule> updatedBusTripSchedules = new ArrayList<>();
        for (BusTripSchedule busTripSchedule : busTripSchedules) {
            boolean isOperation = busTripSchedule.isOperation();
            // Check today is break day ?
            boolean isBreakDay = busTripSchedule.getBreakDays().stream()
                    .anyMatch(breakDay -> !today.isBefore(breakDay.getStartDay()) && !today.isAfter(breakDay.getEndDay()));

            if(isOperation == isBreakDay) {
                busTripSchedule.setOperation(!isBreakDay);
                updatedBusTripSchedules.add(busTripSchedule);
                log.info("Change status operation: " + busTripSchedule.getId());
            }
        }

        busTripScheduleRepository.saveAll(updatedBusTripSchedules);
        log.info("Updated status operation for all schedules");
    }


}
