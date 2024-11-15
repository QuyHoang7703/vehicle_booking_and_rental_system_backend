package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.*;
import com.pbl6.VehicleBookingRental.user.dto.Meta;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBusTripScheduleDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripScheduleDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripScheduleDetailDTO;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BreakDayRepository;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusTripRepository;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusTripScheduleRepository;
import com.pbl6.VehicleBookingRental.user.service.BusService;
import com.pbl6.VehicleBookingRental.user.service.BusTripScheduleService;
import com.pbl6.VehicleBookingRental.user.service.BusinessPartnerService;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import jakarta.persistence.criteria.Join;
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

        BusTripSchedule savedBusTripSchedule = this.busTripScheduleRepository.save(busTripSchedule);

        List<BreakDay> breakDays = reqBusTripScheduleDTO.getBreakDays().stream()
                .map(breakDay -> {
                    return BreakDay.builder()
                            .startDay(breakDay.getStartDay())
                            .endDay(breakDay.getEndDay())
                            .busTripSchedule(savedBusTripSchedule)  // Gán BusTripSchedule cho BreakDay
                            .build();
                }).toList();

        savedBusTripSchedule.setBreakDays(breakDays);
        this.breakDayRepository.saveAll(breakDays);
        return savedBusTripSchedule;
    }

    @Override
    public ResBusTripScheduleDetailDTO convertToResBusTripScheduleDetailDTO(BusTripSchedule busTripSchedule) {
        ResBusTripDTO.BusTripInfo busTripInfo = ResBusTripDTO.BusTripInfo.builder()
                .id(busTripSchedule.getBusTrip().getId())
                .departureLocation(busTripSchedule.getBusTrip().getDepartureLocation())
                .arrivalLocation(busTripSchedule.getBusTrip().getArrivalLocation())
                .durationJourney(busTripSchedule.getBusTrip().getDurationJourney())
                .build();
        ResBusTripScheduleDetailDTO.BusInfo busInfo = ResBusTripScheduleDetailDTO.BusInfo.builder()
                .licensePlate(busTripSchedule.getBus().getLicensePlate())
                .busType(busTripSchedule.getBus().getBusType())
                .build();

        ResBusTripScheduleDetailDTO res = ResBusTripScheduleDetailDTO.builder()
                .idBusTripSchedule(busTripSchedule.getId())
                .busTripInfo(busTripInfo)
                .busInfo(busInfo)
                .departureTime(busTripSchedule.getDepartureTime())
                .arrivalTime(busTripSchedule.getDepartureTime().plus(busTripSchedule.getBusTrip().getDurationJourney()))
                .discountPercentage(busTripSchedule.getDiscountPercentage())
                .priceTicket(busTripSchedule.getPriceTicket())
                .startOperationDay(busTripSchedule.getStartOperationDay())
                .availableSeats(busTripSchedule.getAvailableSeats()) // Có thể thay thế sau này khi order
                .breakDays(busTripSchedule.getBreakDays())
                .isOperation(busTripSchedule.isOperation())
                .build();
        return res;
    }

    @Override
    public ResBusTripScheduleDetailDTO getBusTripScheduleById(int id) throws IdInvalidException {
        BusTripSchedule busTripSchedule = this.busTripScheduleRepository.findById(id)
                .orElseThrow(()-> new IdInvalidException("BusTrip not found"));

        return this.convertToResBusTripScheduleDetailDTO(busTripSchedule);
    }

    @Override
    public ResBusTripScheduleDTO convertToResBusTripScheduleDTO(BusTripSchedule busTripSchedule) {
        ResBusTripScheduleDTO res = ResBusTripScheduleDTO.builder()
                .idBusTripSchedule(busTripSchedule.getId())
                .businessName(busTripSchedule.getBusTrip().getBusPartner().getBusinessPartner().getBusinessName())
                .busTypeName(busTripSchedule.getBus().getBusType().getName())
                .departureLocation(busTripSchedule.getBusTrip().getDepartureLocation())
                .arrivalLocation(busTripSchedule.getBusTrip().getArrivalLocation())
                .departureTime(busTripSchedule.getDepartureTime())
                .durationJourney(busTripSchedule.getBusTrip().getDurationJourney())
                .discountPercentage(busTripSchedule.getDiscountPercentage())
                .priceTicket(busTripSchedule.getPriceTicket())
//                .arrivalTime(busTripSchedule.getDepartureTime().plus(busTripSchedule.getBusTrip().getDurationJourney()))
                .availableSeats(busTripSchedule.getAvailableSeats())
                .build();
        res.setArrivalTime(res.getArrivalTime());

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
                .map(this::convertToResBusTripScheduleDTO)
                .toList();
        res.setResult(resBusTripScheduleDTOS);

        return res;
    }

    // @Scheduled(cron = "0 0 0 * * ?")
//    @Transactional
//    @Scheduled(cron = "0 */1 * * * *")
//    public void updateBusTripScheduleStatus() {
//        LocalDate today = LocalDate.now();
//        log.info("Today is: " + today);
//        List<BusTripSchedule> schedules = busTripScheduleRepository.findAll();
//
//        for (BusTripSchedule schedule : schedules) {
//            List<BreakDay> breakDays = schedule.getBreakDays();
//            boolean isBreakDay = false;
//            for (BreakDay breakDay : breakDays) {
//                log.info("Start of reakDay is: " + breakDay.getStartDay());
//                boolean check = today.isBefore(breakDay.getStartDay());
//                log.info("Check: " + check);
//                if(!today.isBefore(breakDay.getStartDay()) && !today.isAfter(breakDay.getEndDay())) {
//                    isBreakDay = true;
//                    break;
//                }
//            }
//            if(isBreakDay && !schedule.isOperation()) {
//                schedule.setOperation(false);
//                log.info("Change status operation");
//            }
//        }
//
//        busTripScheduleRepository.saveAll(schedules);
//        log.info("Updated status operation for all schedules");
//    }


}
