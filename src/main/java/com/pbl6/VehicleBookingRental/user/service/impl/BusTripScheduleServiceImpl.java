package com.pbl6.VehicleBookingRental.user.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.pbl6.VehicleBookingRental.user.service.*;
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
    private final RedisService<String, String, BusTripSchedule> redisService;
    private final ObjectMapper objectMapper;
    private final String redisKeyPrefix = "busTripSchedule:";
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

        if(reqBusTripScheduleDTO.getStartOperationDay().isEqual(LocalDate.now()) || reqBusTripScheduleDTO.getStartOperationDay().isBefore(LocalDate.now())) {
            busTripSchedule.setOperation(true);
        }

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

        // Create busTripSchedule to save in Redis
//        String redisKey = "busTripSchedule:" + savedBusTripSchedule.getId();
//        redisService.setHashSet(redisKeyPrefix+ savedBusTripSchedule.getId(), "info", savedBusTripSchedule);
//        redisService.setHashSet("busTripSchedule", String.valueOf(savedBusTripSchedule.getId()), savedBusTripSchedule);
//        Object rawValue = redisService.getHashValue(redisKey, "info");
//        BusTripSchedule busTripSchedule1 = objectMapper.convertValue(rawValue, BusTripSchedule.class);
//        log.info("AVAILABLE OF REDIS " + String.valueOf(busTripSchedule1.getAvailableSeats()));
//        log.info("ID OF REDIS " + String.valueOf(busTripSchedule1.getId()));
//        log.info("departure time of redis" + String.valueOf(busTripSchedule1.getDepartureTime()));
//        log.info("start operation of reddis" + String.valueOf(busTripSchedule1.getBusTrip().getDepartureLocation()));
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
//        BusTripSchedule busTripScheduleRedis = objectMapper.convertValue(redisService.getHashValue(redisKeyPrefix+id, "info"), BusTripSchedule.class);
//        if(busTripScheduleRedis != null) {
//            log.info("Get busTripSchedule with id {} from redis ", id);
//            return this.convertToResBusTripScheduleDetailDTO(busTripScheduleRedis);
//        }
        BusTripSchedule busTripSchedule = this.busTripScheduleRepository.findById(id)
                .orElseThrow(()-> new IdInvalidException("BusTrip not found"));
        log.info("Get busTripSchedule with id {} from mysql ", id);
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
                .isOperation(busTripSchedule.isOperation())
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

//        // Tạo key Redis dựa trên các tham số filter và pageable
//        String redisKey = "busTripSchedules:" + businessPartner.getBusPartner().getId()
//                + ":filter=" + finalSpec.hashCode()
//                + ":page=" + pageable.getPageNumber()
//                + ":size=" + pageable.getPageSize();
//        log.info("Spec: " + finalSpec.toString().hashCode());
//        Map<String, BusTripSchedule> maps = redisService.getAllHashValues(redisKey);
//        if(!maps.isEmpty()){
//            log.info("Khác rỗng");
//        }

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

    @Override
    public ResultPaginationDTO getAllBusTripScheduleAvailableForUser(Specification<BusTripSchedule> spec, Pageable pageable) throws ApplicationException {
        Specification<BusTripSchedule> newSpec = (root, query, criteriaBuilder) -> {
            Predicate predicateOperationDay = criteriaBuilder.lessThanOrEqualTo(root.get("startOperationDay"), LocalDate.now());
            Predicate predicateStatusOperation = criteriaBuilder.equal(root.get("isOperation"), true);

            return criteriaBuilder.and(predicateOperationDay, predicateStatusOperation);
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
    @Transactional
    @Scheduled(cron = "0 */2 * * * *")
    public void updateBusTripScheduleStatus() {
        LocalDate today = LocalDate.now();
        log.info("Today is: " + today);
        List<BusTripSchedule> busTripSchedules = busTripScheduleRepository.findAll();
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
