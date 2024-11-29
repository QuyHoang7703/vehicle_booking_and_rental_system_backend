package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.Images;
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
import com.pbl6.VehicleBookingRental.user.repository.image.ImageRepository;
import com.pbl6.VehicleBookingRental.user.repository.order.OrderBusTripRepository;
import com.pbl6.VehicleBookingRental.user.service.*;
import com.pbl6.VehicleBookingRental.user.util.CurrencyFormatterUtil;
import com.pbl6.VehicleBookingRental.user.util.constant.ImageOfObjectEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
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
import java.time.LocalDateTime;
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
    private final ImageRepository imageRepository;
    private final OrderBusTripRepository orderBusTripRepository;

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
    public ResBusTripScheduleDetailForAdminDTO convertToResBusTripScheduleDetailDTO(BusTripSchedule busTripSchedule, LocalDate departureDate) {
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
                .availableSeats(this.getAvailableNumberOfSeatsByDepartureDate(busTripSchedule, departureDate)) // Có thể thay thế sau này khi order
                .breakDays(busTripSchedule.getBreakDays())
                .isOperation(busTripSchedule.isOperation())
                .build();
        return res;
    }

    @Override
    public ResBusTripScheduleDetailForAdminDTO getBusTripScheduleById(int id, LocalDate departureDate) throws IdInvalidException {
        BusTripSchedule busTripSchedule = this.busTripScheduleRepository.findById(id)
                .orElseThrow(()-> new IdInvalidException("BusTrip not found"));
        log.info("Get busTripSchedule with id {} from mysql ", id);
        return this.convertToResBusTripScheduleDetailDTO(busTripSchedule, departureDate);
    }

    @Override
    public ResBusTripScheduleDTO convertToResBusTripScheduleDTO(BusTripSchedule busTripSchedule, LocalDate departureDate) throws IdInvalidException {
//        ResBusDTO resBusDTO = this.busService.convertToResBus(busTripSchedule.getBus());
        // Create bus info
        Bus bus = busTripSchedule.getBus();
        Images imageOfBus = this.imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.BUS), bus.getId()).get(0);
        ResBusTripScheduleDetailForAdminDTO.BusInfo busInfo = ResBusTripScheduleDetailForAdminDTO.BusInfo.builder()
                .licensePlate(bus.getLicensePlate())
                .imageRepresentative(imageOfBus.getPathImage())
                .busType(bus.getBusType())
                .build();

        // Create busTrip info
        ResBusTripDTO.BusTripInfo busTripInfo = ResBusTripDTO.BusTripInfo.builder()
                .id(busTripSchedule.getBusTrip().getId())
                .departureLocation(busTripSchedule.getBusTrip().getDepartureLocation())
                .arrivalLocation(busTripSchedule.getBusTrip().getArrivalLocation())
                .durationJourney(busTripSchedule.getBusTrip().getDurationJourney())
                .build();

        // Create busPartner info
        ResBusTripScheduleDTO.BusinessPartnerInfo businessPartnerInfo = ResBusTripScheduleDTO.BusinessPartnerInfo.builder()
                .id(busTripSchedule.getBusTrip().getBusPartner().getBusinessPartner().getId())
                .name(busTripSchedule.getBusTrip().getBusPartner().getBusinessPartner().getBusinessName())
                .accountId(busTripSchedule.getBusTrip().getBusPartner().getBusinessPartner().getAccount().getId())
                .build();

        int availableNumberOfSeats = this.getAvailableNumberOfSeatsByDepartureDate(busTripSchedule, departureDate);

        ResBusTripScheduleDTO res = ResBusTripScheduleDTO.builder()
                .busTripScheduleId(busTripSchedule.getId())
                .businessPartnerInfo(businessPartnerInfo)
                .busInfo(busInfo)
                .busTripInfo(busTripInfo)
                .departureTime(busTripSchedule.getDepartureTime())
                .discountPercentage(busTripSchedule.getDiscountPercentage())
                .priceTicket(CurrencyFormatterUtil.formatToVND(busTripSchedule.getPriceTicket()))
                .arrivalTime(busTripSchedule.getDepartureTime().plus(busTripInfo.getDurationJourney()))
                .availableSeats(availableNumberOfSeats)
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
                            return convertToResBusTripScheduleDTO(busTripSchedule, null);
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

            // Giờ khởi hành lớn hơn giờ hiện tại là 1 tiếng
            LocalDate currentDate = LocalDate.now();

            Path<LocalTime> departureTime = root.get("departureTime");

            // Kết hợp thành LocalDateTime
            Expression<LocalDateTime> departureDateTime = criteriaBuilder.function(
                    "TIMESTAMP", LocalDateTime.class,
                    criteriaBuilder.literal(currentDate), departureTime
            );

            // So sánh với LocalDateTime hiện tại + 1 giờ
            LocalDateTime validTime = LocalDateTime.now().plusHours(1);

//            LocalDate test = LocalDate.now();
//            LocalDateTime test2 = LocalDateTime.of(test, LocalTime.of(23, 0)).plusHours(1);
//            log.info("Test2: " + test2);

            Predicate predicateValidTime = criteriaBuilder.greaterThan(departureDateTime, validTime);

            return criteriaBuilder.and(predicateOperationDay, predicateStatusOperation, predicateValidIds, predicateValidTime);
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

        LocalDate finalDepartureDate = departureDate;
        List<ResBusTripScheduleDTO> resBusTripScheduleDTOS = busTripSchedulePage.getContent().stream()
                .map(busTripSchedule -> {
                    try {
                        return convertToResBusTripScheduleDTO(busTripSchedule, finalDepartureDate);
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

        return this.convertToResBusTripScheduleDTO(busTripSchedule, null);
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

    private int getAvailableNumberOfSeatsByDepartureDate(BusTripSchedule busTripSchedule, LocalDate departureDate) {
        // Find orderBusTrip by departureDate and busTripScheduleId to calculate rest numberOfTicket
        int availableNumberOfSeats = busTripSchedule.getAvailableSeats();
        List<OrderBusTrip> orderBusTrips = this.orderBusTripRepository.findByDepartureDateAndBusTripScheduleId(departureDate, busTripSchedule.getId());
        if(orderBusTrips!=null && !orderBusTrips.isEmpty()) {
            for(OrderBusTrip orderBusTrip : orderBusTrips) {
                availableNumberOfSeats -= orderBusTrip.getNumberOfTicket();
            }
        }

        return availableNumberOfSeats;
    }


}
