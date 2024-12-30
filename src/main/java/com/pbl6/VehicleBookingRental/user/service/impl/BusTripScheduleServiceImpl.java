package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.Images;
import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.*;
import com.pbl6.VehicleBookingRental.user.dto.Meta;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBreakDayDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBusTripScheduleDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.*;
import com.pbl6.VehicleBookingRental.user.repository.OrdersRepo;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BreakDayRepository;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusTripRepository;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusTripScheduleRepository;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.DropOffLocationRepository;
import com.pbl6.VehicleBookingRental.user.repository.image.ImageRepository;
import com.pbl6.VehicleBookingRental.user.repository.order.OrderBusTripRepository;
import com.pbl6.VehicleBookingRental.user.service.*;
import com.pbl6.VehicleBookingRental.user.util.CurrencyFormatterUtil;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import com.pbl6.VehicleBookingRental.user.util.constant.ImageOfObjectEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.OrderStatusEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusTripScheduleServiceImpl implements BusTripScheduleService {
    private final BusTripScheduleRepository busTripScheduleRepository;
    private final BusTripRepository busTripRepository;
    private final BusService busService;
    private final BusinessPartnerService businessPartnerService;
    private final ImageRepository imageRepository;
    private final OrderBusTripRepository orderBusTripRepository;
    private final DropOffLocationRepository dropOffLocationRepository;
    private final AccountService accountService;
    private final OrdersRepo ordersRepo;
    private final BreakDayRepository breakDayRepository;

    @Override
    @Transactional
    public BusTripSchedule createBusTripSchedule(ReqBusTripScheduleDTO reqBusTripScheduleDTO) throws IdInvalidException, ApplicationException {
//        if(this.busTripScheduleRepository.existsByBusTrip_IdAndBus_Id(reqBusTripScheduleDTO.getBusTripId(), reqBusTripScheduleDTO.getBusId())) {
//            throw new ApplicationException("This bus trip schedule has already existed");
//        }
        BusTripSchedule busTripSchedule = new BusTripSchedule();

        BusTrip busTrip = this.busTripRepository.findById(reqBusTripScheduleDTO.getBusTripId())
                .orElseThrow(()-> new IdInvalidException("BusTrip not found"));

        busTripSchedule.setBusTrip(busTrip);
        DropOffLocation dropOffLocationForBusTripRequest = this.dropOffLocationRepository.findArrivalLocationOfBusTrip(busTrip.getId(), busTrip.getArrivalLocation())
                .orElseThrow(() -> new ApplicationException("DropOffLocation not found 1"));

        // Get the bus
        Bus bus = this.busService.findBusById(reqBusTripScheduleDTO.getBusId());
        // Lấy ra danh sách lịch trình của xe
        List<BusTripSchedule> busTripSchedules = bus.getBusTripSchedules();
        for(BusTripSchedule schedule : busTripSchedules) {
            // Tính toán thời gian đến của mỗi lịch trình của xe
            DropOffLocation dropOffLocation = this.dropOffLocationRepository.findByProvinceAndBusTripScheduleId(schedule.getBusTrip().getArrivalLocation(), schedule.getId())
                    .orElseThrow(()-> new ApplicationException("DropOffLocation not found 2"));

            LocalDateTime departureDateTime = LocalDateTime.of(LocalDate.now(), schedule.getDepartureTime());
            log.info("departureDateTime : " + departureDateTime);

            // Thời gian đến cộng thêm 1 tiếng để phòng chờ có việc phát sinh
            LocalDateTime arrivalDateTime = departureDateTime.plus(dropOffLocation.getJourneyDuration()).plusHours(0);
            log.info("arrivalDateTime : " + arrivalDateTime);

            LocalDateTime newDepartDateTime = LocalDateTime.of(LocalDate.now(), reqBusTripScheduleDTO.getDepartureTime());
            log.info("newDepartDateTime : " + newDepartDateTime);
            // Nếu thời gian khởi hành mới nằm trong khoảng thời gian mà bus đã chạy ở 1 lịch trình khác -> Xung đột lịch
            if ((newDepartDateTime.isAfter(departureDateTime) && newDepartDateTime.isBefore(arrivalDateTime))
                    || newDepartDateTime.isEqual(departureDateTime)
                    || newDepartDateTime.isEqual(arrivalDateTime)) {
                throw new ApplicationException("Conflict schedules of the bus - conflict departure time");
            }

            // Kiểm tra xem liệu giờ đến của chuyến mới có nằm trong giờ đi của xe của chuyến cũ ko
            LocalDateTime newArrivalDateTime = newDepartDateTime.plus(dropOffLocationForBusTripRequest.getJourneyDuration()).plusHours(0);
            log.info("newArrivalDateTime: " + newArrivalDateTime);
            LocalTime newArrivalTime = newArrivalDateTime.toLocalTime();
            log.info("newArrivalTime: " + newArrivalTime);
            if(newArrivalTime.isAfter(schedule.getDepartureTime()) && newArrivalTime.isBefore(arrivalDateTime.toLocalTime())) {
                throw new ApplicationException("Conflict schedules of the bus - conflict arrival time");
            }


        }

        busTripSchedule.setBus(bus);
        busTripSchedule.setAvailableSeats(bus.getBusType().getNumberOfSeat());

        busTripSchedule.setDepartureTime(reqBusTripScheduleDTO.getDepartureTime());
        busTripSchedule.setDiscountPercentage(reqBusTripScheduleDTO.getDiscountPercentage());
        busTripSchedule.setStartOperationDay(reqBusTripScheduleDTO.getStartOperationDay());

        if(reqBusTripScheduleDTO.getStartOperationDay().isEqual(LocalDate.now()) || reqBusTripScheduleDTO.getStartOperationDay().isBefore(LocalDate.now())) {
            busTripSchedule.setOperation(true);
        }

        // Check breakDays is null ?
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
        if(departureDate ==  null) {
            departureDate = LocalDate.now();
        }
        ResBusTripDTO.BusTripInfo busTripInfo = ResBusTripDTO.BusTripInfo.builder()
                .id(busTripSchedule.getBusTrip().getId())
                .departureLocation(busTripSchedule.getBusTrip().getDepartureLocation())
                .arrivalLocation(busTripSchedule.getBusTrip().getArrivalLocation())
                .build();
        ResBusTripScheduleForAdminDTO.BusInfo busInfo = ResBusTripScheduleForAdminDTO.BusInfo.builder()
                .licensePlate(busTripSchedule.getBus().getLicensePlate())
                .busType(busTripSchedule.getBus().getBusType())
                .build();

        ResBusTripScheduleDetailForAdminDTO res = ResBusTripScheduleDetailForAdminDTO.builder()
                .busTripScheduleId(busTripSchedule.getId())
                .busTripInfo(busTripInfo)
                .busInfo(busInfo)
                .departureTime(busTripSchedule.getDepartureTime())
                .discountPercentage(busTripSchedule.getDiscountPercentage())
                .startOperationDay(busTripSchedule.getStartOperationDay())
                .availableSeats(this.getAvailableNumberOfSeatsByDepartureDate(busTripSchedule, departureDate)) // Có thể thay thế sau này khi order
                .breakDays(busTripSchedule.getBreakDays())
                .isSuspended(busTripSchedule.isSuspended())
                .build();

        LocalDate finalDepartureDate = departureDate;
        boolean isOperation = busTripSchedule.getBreakDays().stream().anyMatch(breakDay ->
                (!finalDepartureDate.isBefore(breakDay.getStartDay()) && !finalDepartureDate.isAfter(breakDay.getEndDay())));

        res.setOperation(!isOperation);



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
    public ResultPaginationDTO getAllBusTripScheduleByBusTripId(Specification<BusTripSchedule> spec, Pageable pageable, int busTripId, LocalDate departureDate) throws ApplicationException, IdInvalidException {
        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);

        BusTrip busTrip = this.busTripRepository.findById(busTripId)
                .orElseThrow(()-> new IdInvalidException("BusTrip not found"));

        if(busTrip.getBusTripSchedules() == null || busTrip.getBusTripSchedules().isEmpty()) {
            return null;
        }
        if(!busTrip.getBusPartner().getBusinessPartner().equals(businessPartner)) {
            throw new ApplicationException("You aren't allowed to see bus trip schedule of this bus trip");
        }

        Specification<BusTripSchedule> newSpec = (root, query, criteriaBuilder) -> {
            Join<BusTripSchedule, BusTrip> joinBusTrip = root.join("busTrip");
            Predicate predicateByBusTripId = criteriaBuilder.equal(joinBusTrip.get("id"), busTripId);

            Join<BusTrip, BusPartner> joinBusPartner = joinBusTrip.join("busPartner");
            Predicate predicateByBusPartnerId = criteriaBuilder.equal(joinBusPartner.get("id"), businessPartner.getId());
            return criteriaBuilder.and(predicateByBusTripId, predicateByBusPartnerId);
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

        List<ResBusTripScheduleForAdminDTO> resBusTripScheduleDTOS = busTripSchedulePage.getContent().stream()
                .map(busTripSchedule -> {
                    try {
                            return convertToResBusTripScheduleForAdminDTO(busTripSchedule, departureDate);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        res.setResult(resBusTripScheduleDTOS);

        return res;
    }

    @Override
    public ResultPaginationDTO getAllBusTripScheduleAvailableForUser(Specification<BusTripSchedule> spec, Pageable pageable,String departureLocation, String arrivalProvince, LocalDate departureDate) throws ApplicationException {
        if(departureDate == null) {
            departureDate = LocalDate.now();
//            log.info("Departure: " + departureDate);
        }
        List<Long> validIds = busTripScheduleRepository.findBusTripScheduleIdsNotInBreakDays(departureDate);
        LocalDate finalDepartureDate1 = departureDate;
        Specification<BusTripSchedule> newSpec = (root, query, criteriaBuilder) -> {
            Predicate predicateOperationDay = criteriaBuilder.lessThanOrEqualTo(root.get("startOperationDay"), LocalDate.now());
            Predicate predicateIsOperation = criteriaBuilder.equal(root.get("isOperation"), true);
            Predicate predicateValidIds = root.get("id").in(validIds); // Chỉ lấy các ID hợp lệ từ truy vấn

            Predicate predicateSuspended = criteriaBuilder.equal(root.get("suspended"), false);// Lấy các bus trip schedule có suspended là false

            Join<BusTripSchedule, BusTrip> joinBusTrip = root.join("busTrip");
            Predicate pre1 = criteriaBuilder.equal(joinBusTrip.get("departureLocation"), departureLocation);
            Join<BusTrip, DropOffLocation> joinDropOffLocation = joinBusTrip.join("dropOffLocations");
            Predicate pre2 = criteriaBuilder.equal(joinDropOffLocation.get("province"), arrivalProvince);


            if(finalDepartureDate1.isAfter(LocalDate.now())){
                return criteriaBuilder.and(predicateOperationDay, predicateIsOperation, predicateSuspended, predicateValidIds, pre1, pre2);
            }else{
                // Giờ khởi hành lớn hơn giờ hiện tại là 1 tiếng
                LocalDate currentDate = LocalDate.now();

                Path<LocalTime> departureTime = root.get("departureTime");

                // Kết hợp thành LocalDateTime
                Expression<LocalDateTime> departureDateTime = criteriaBuilder.function(
                        "TIMESTAMP", LocalDateTime.class,
                        criteriaBuilder.literal(currentDate), departureTime
                );

                // So sánh với LocalDateTime hiện tại + 1 giờ
                LocalDateTime validTime = LocalDateTime.now().plusHours(0);
                Predicate predicateValidTime = criteriaBuilder.greaterThan(departureDateTime, validTime);
                return criteriaBuilder.and(predicateOperationDay, predicateIsOperation, predicateSuspended, predicateValidIds, predicateValidTime, pre1, pre2);
            }
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
//        List<ResBusTripScheduleDTO> resBusTripScheduleDTOS = busTripSchedulePage.getContent().stream()
//                .map(busTripSchedule -> {
//                    try {
//                        return convertToResBusTripScheduleDTO(busTripSchedule, finalDepartureDate);
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                })
//                .toList();
        List<ResBusTripScheduleDTO> resBusTripScheduleDTOS = busTripSchedulePage.getContent().stream()
                .flatMap(busTripSchedule -> {
                    try {
                        return convertToResBusTripScheduleDTO2(busTripSchedule, finalDepartureDate, arrivalProvince).stream();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        res.setResult(resBusTripScheduleDTOS);
        return res;
    }

    @Override
    public ResBusTripScheduleDTO getBusTripScheduleByIdForUser(int busTripScheduleId, LocalDate departureDate, String arrivalProvince) throws IdInvalidException, ApplicationException {
        BusTripSchedule busTripSchedule = this.busTripScheduleRepository.findById(busTripScheduleId)
                .orElseThrow(() -> new IdInvalidException("Bus trip schedule not found"));

        return this.convertToResBusTripScheduleDTO2(busTripSchedule, departureDate, arrivalProvince).get(0);
    }

    @Override
    public List<BreakDay> getBreakDaysForBusTripSchedule(int busTripScheduleId) throws IdInvalidException, ApplicationException {
        String email = SecurityUtil.getCurrentLogin().isPresent()?SecurityUtil.getCurrentLogin().get():null;
        Account account = this.accountService.handleGetAccountByUsername(email);
        BusTripSchedule busTripSchedule = this.busTripScheduleRepository.findById(busTripScheduleId)
                .orElseThrow(() -> new IdInvalidException("Bus trip schedule not found"));

        if(!busTripSchedule.getBusTrip().getBusPartner().getBusinessPartner().getAccount().equals(account)){
            throw new ApplicationException("You don't have permission to see the break days of this bus trip shedule");
        }
        return busTripSchedule.getBreakDays();
    }

    @Override
    @Transactional
    public void cancelBusTripSchedule(int busTripScheduleId, LocalDate cancelDate) throws IdInvalidException, ApplicationException {
        BusTripSchedule busTripSchedule = this.busTripScheduleRepository.findById(busTripScheduleId)
                .orElseThrow(() -> new IdInvalidException("Bus trip schedule not found"));

        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);

        if(!busTripSchedule.getBusTrip().getBusPartner().getBusinessPartner().equals(businessPartner)){
            throw new ApplicationException("You don't have permission to delete the bus trip schedule");
        }

        LocalDateTime cancelDepartureTime = cancelDate.atTime(busTripSchedule.getDepartureTime());
        LocalDateTime cutoffTime = cancelDepartureTime.minusMinutes(3);
        if(LocalDateTime.now().isAfter(cutoffTime)){
            throw new ApplicationException("The schedule can only be cancelled at least 3 minutes before the departure time.");
        }

        this.updateStatusOfOrderOfBusTripSchedule(busTripSchedule.getId(), businessPartner.getAccount().getId());


    }

    private void updateStatusOfOrderOfBusTripSchedule(int busTripScheduleId, int businessPartnerId) throws ApplicationException {
        List<OrderBusTrip> orderBusTrips = this.orderBusTripRepository.findOrderBusTripNotStart(busTripScheduleId);
        if (orderBusTrips.isEmpty()) {
            throw new ApplicationException("No orders associated with this bus trip schedule");
        }
        List<Orders> ordersToUpdate  = new ArrayList<>();
        // Update status of order into CANCELLED
        for(OrderBusTrip orderBusTrip : orderBusTrips){
            orderBusTrip.setStatus(OrderStatusEnum.CANCELLED);

            Orders order = orderBusTrip.getOrder();
            order.setCancelTime(Instant.now());
            order.setCancelUserId(businessPartnerId);

            ordersToUpdate.add(order);
//            this.ordersRepo.save(order);
        }
        this.ordersRepo.saveAll(ordersToUpdate);
    }

    @Override
    public boolean checkBusTripScheduleHasOrder(int busTripScheduleId) throws IdInvalidException, ApplicationException {
        BusTripSchedule busTripSchedule = this.busTripScheduleRepository.findById(busTripScheduleId)
                .orElseThrow(() -> new IdInvalidException("Bus trip schedule not found"));

        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);

        if(!busTripSchedule.getBusTrip().getBusPartner().getBusinessPartner().equals(businessPartner)){
            throw new ApplicationException("You don't have permission to delete the bus trip schedule");
        }

        List<OrderBusTrip> orderBusTrips = this.orderBusTripRepository.findOrderBusTripNotStart(busTripScheduleId);
        if(orderBusTrips!=null && !orderBusTrips.isEmpty()){
            return true;
        }

        return false;
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
            if(busTripSchedule.isSuspended()){
                continue;
            }
            if(this.updateOperationInDayOfBusTripSchedule(busTripSchedule, today) != null){
                updatedBusTripSchedules.add(busTripSchedule);
            }

        }
        busTripScheduleRepository.saveAll(updatedBusTripSchedules);
        log.info("Updated status operation for all schedules");
    }

    private BusTripSchedule updateOperationInDayOfBusTripSchedule(BusTripSchedule busTripSchedule, LocalDate today) {
        boolean isOperation = busTripSchedule.isOperation();
        // Check today is break day ?
        boolean isBreakDay = busTripSchedule.getBreakDays().stream()
                .anyMatch(breakDay -> !today.isBefore(breakDay.getStartDay()) && !today.isAfter(breakDay.getEndDay()));

        if(isOperation == isBreakDay) {
            busTripSchedule.setOperation(!isBreakDay);
            log.info("Change status operation: " + busTripSchedule.getId());
            return busTripSchedule;
        }
        return null;
    }

    private int getAvailableNumberOfSeatsByDepartureDate(BusTripSchedule busTripSchedule, LocalDate departureDate) {
        // Find orderBusTrip by departureDate and busTripScheduleId to calculate rest numberOfTicket
        int availableNumberOfSeats = busTripSchedule.getAvailableSeats();
        List<OrderBusTrip> orderBusTrips = this.orderBusTripRepository.findByDepartureDateAndBusTripScheduleId(departureDate, busTripSchedule.getId());
        if(orderBusTrips!=null && !orderBusTrips.isEmpty()) {
            for(OrderBusTrip orderBusTrip : orderBusTrips) {
                if(orderBusTrip.getStatus().equals(OrderStatusEnum.COMPLETED)){
                    availableNumberOfSeats -= orderBusTrip.getNumberOfTicket();
                }
            }
        }

        return availableNumberOfSeats;
    }


    public List<ResBusTripScheduleDTO> convertToResBusTripScheduleDTO2(BusTripSchedule busTripSchedule, LocalDate departureDate, String arrivalProvince) throws IdInvalidException, ApplicationException {
        List<ResBusTripScheduleDTO> resBusTripScheduleDTOS = new ArrayList<>();

//        List<DropOffLocation> dropOffLocations = busTripSchedule.getBusTrip().getDropOffLocations();

        DropOffLocation dropOffLocation = this.dropOffLocationRepository.findByProvinceAndBusTripScheduleId(arrivalProvince, busTripSchedule.getId())
                .orElseThrow(() -> new ApplicationException("Drop off location not found"));

        // Create bus info
        Bus bus = busTripSchedule.getBus();
        Images imageOfBus = this.imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.BUS), bus.getId()).get(0);
        ResBusTripScheduleForAdminDTO.BusInfo busInfo = ResBusTripScheduleForAdminDTO.BusInfo.builder()
                .licensePlate(bus.getLicensePlate())
                .imageRepresentative(imageOfBus.getPathImage())
                .busType(bus.getBusType())
                .build();

        // Create busTrip info
        ResBusTripDTO.BusTripInfo busTripInfo = ResBusTripDTO.BusTripInfo.builder()
                .id(busTripSchedule.getBusTrip().getId())
                .departureLocation(busTripSchedule.getBusTrip().getDepartureLocation())
                .arrivalLocation(dropOffLocation.getProvince())
//                    .durationJourney(busTripSchedule.getBusTrip().getDurationJourney())
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
                .journeyDuration(dropOffLocation.getJourneyDuration())
                .discountPercentage(busTripSchedule.getDiscountPercentage())
                .priceTicket(CurrencyFormatterUtil.formatToVND(dropOffLocation.getPriceTicket()))
                .arrivalTime(busTripSchedule.getDepartureTime().plus(dropOffLocation.getJourneyDuration()))
                .availableSeats(availableNumberOfSeats)
                .isOperation(busTripSchedule.isOperation())
                .ratingTotal(busTripSchedule.getRatingTotal())
                .journey(String.format("Vé thuộc chặng chuyến %s %s %s - %s",
                        busTripSchedule.getDepartureTime(),
                        DateTimeFormatter.ofPattern("dd-MM-yyyy").format(departureDate),
                        busTripInfo.getDepartureLocation(),
                        busTripSchedule.getBusTrip().getArrivalLocation()))
                .build();
        resBusTripScheduleDTOS.add(res);

        return resBusTripScheduleDTOS;
    }

    public ResBusTripScheduleForAdminDTO convertToResBusTripScheduleForAdminDTO(BusTripSchedule busTripSchedule, LocalDate departureDate) throws ApplicationException {
        String status = "Hoạt động";
        List<BreakDay> breakDays = busTripSchedule.getBreakDays();
        for(BreakDay breakDay : breakDays) {
            if(!departureDate.isBefore(breakDay.getStartDay()) && !departureDate.isAfter(breakDay.getEndDay())) {
                status = "Nghỉ đến ngày " + DateTimeFormatter.ofPattern("dd-MM-yyyy").format(breakDay.getEndDay());
                break;
            }
        }

        ResBusTripScheduleForAdminDTO.BusInfo busInfo = ResBusTripScheduleForAdminDTO.BusInfo.builder()
                .licensePlate(busTripSchedule.getBus().getLicensePlate())
                .busType(busTripSchedule.getBus().getBusType())
                .build();
        ResBusTripScheduleForAdminDTO res = ResBusTripScheduleForAdminDTO.builder()
                .busTripSchedule(busTripSchedule.getId())
                .busInfo(busInfo)
                .departureTime(busTripSchedule.getDepartureTime())
                .status(status)
                .ratingValue(busTripSchedule.getRatingTotal())
                .discountPercentage(busTripSchedule.getDiscountPercentage())
                .availableSeats(this.getAvailableNumberOfSeatsByDepartureDate(busTripSchedule, departureDate))
                .build();
        return res;
    }

    public ResultPaginationDTO getAllBusTripSchedules(Specification<BusTripSchedule> spec, Pageable pageable) throws ApplicationException {
        ResultPaginationDTO res = new ResultPaginationDTO();

        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);

        Specification<BusTripSchedule> findByBusPartnerSpec = (root, query, criteriaBuilder) ->{
            Join<BusTripSchedule, BusTrip> joinBusTrip = root.join("busTrip");
            Join<BusTrip, BusPartner> joinBusPartner = joinBusTrip.join("busPartner");
            return criteriaBuilder.equal(joinBusPartner.get("id"), businessPartner.getBusPartner().getId());
        };

        Specification<BusTripSchedule> newSpec = spec.and(findByBusPartnerSpec);
        Page<BusTripSchedule> busTripSchedulePage = this.busTripScheduleRepository.findAll(newSpec, pageable);

        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(busTripSchedulePage.getTotalPages());
        meta.setTotal(busTripSchedulePage.getTotalElements());
        res.setMeta(meta);

        List<ResBusTripScheduleForAdminDTO2> resBusTripScheduleForAdminDTO2s = busTripSchedulePage.getContent().stream()
                .map(busTripSchedule -> {
                    try {
                        return this.convertToResBusTripScheduleForAdminDTO2(busTripSchedule);
                    } catch (ApplicationException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        res.setResult(resBusTripScheduleForAdminDTO2s);
        return res;
    }

    @Override
    public void addBreakDay(ReqBreakDayDTO reqBreakDayDTO) throws IdInvalidException, ApplicationException {
        BusTripSchedule busTripSchedule = this.busTripScheduleRepository.findById(reqBreakDayDTO.getBusTripScheduleId())
                .orElseThrow(()-> new IdInvalidException("Bus trip schedule not found"));

        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);
        if(!busTripSchedule.getBusTrip().getBusPartner().getBusinessPartner().equals(businessPartner)) {
            throw new ApplicationException("You don't have permission to add break days for this bus trip schedule");
        }

        // Find order bus trip in break days
        if(reqBreakDayDTO.getBreakDays() != null && !reqBreakDayDTO.getBreakDays().isEmpty()) {
            List<OrderBusTrip> orderBusTrips = reqBreakDayDTO.getBreakDays().stream()
                    .flatMap(breakDay -> this.orderBusTripRepository.findOrderBusTripBetweenDates(
                                    businessPartner.getBusPartner().getId(),
                                    breakDay.getStartDay(),
                                    breakDay.getEndDay())
                            .stream())
                    .toList();
            log.info("orderBusTrip length in break days: " + orderBusTrips.size());
            // Update status of order
            List<BreakDay> notAvailableBreakDays = new ArrayList<>();
            if(!orderBusTrips.isEmpty()) {
                for(OrderBusTrip orderBusTrip : orderBusTrips) {
                    orderBusTrip.setStatus(OrderStatusEnum.CANCELLED);
                    Orders order = orderBusTrip.getOrder();
                    //Update order
                    order.setCancelTime(Instant.now());
                    order.setCancelUserId(businessPartner.getId());
                    this.ordersRepo.save(order);
                }
            }

            for(BreakDay breakDay : reqBreakDayDTO.getBreakDays()) {
                if(!this.breakDayRepository.existsByStartDayAndEndDayAndBusTripSchedule_Id(breakDay.getStartDay(), breakDay.getEndDay(), busTripSchedule.getId())) {
                    breakDay.setBusTripSchedule(busTripSchedule);
                    notAvailableBreakDays.add(breakDay);
                }
            }
            this.breakDayRepository.saveAll(notAvailableBreakDays);
        }


       // Update discount percentage for bus trip schedule
        if(reqBreakDayDTO.getDiscountPercentage() != 0.0) {
            busTripSchedule.setDiscountPercentage(reqBreakDayDTO.getDiscountPercentage());
            this.busTripScheduleRepository.save(busTripSchedule);
        }
    }

    @Override
    public void deleteBreakDay(int breakDayId) throws IdInvalidException {
//        BusinessPartner businessPartner = this.businessPartnerService
        BreakDay breakDay = this.breakDayRepository.findById(breakDayId)
                .orElseThrow(() -> new IdInvalidException("Break day not found"));

    }

    @Override
    public void updateStatusOfBusTripSchedule(int busTripScheduleId, boolean suspended) throws IdInvalidException, ApplicationException {
        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);
        BusTripSchedule busTripScheduleDb = this.busTripScheduleRepository.findById(busTripScheduleId)
                .orElseThrow(() -> new IdInvalidException("Bus trip schedule not found"));
        if(busTripScheduleDb.getBusTrip().getBusPartner() != businessPartner.getBusPartner()){
            throw new ApplicationException("You don't have permission to update status of this bus trip schedule");
        }
        LocalDate today = LocalDate.now();
        // In case bus trip schedule is suspend -> need check status 'operation' in this day before changing
        if(busTripScheduleDb.isSuspended()){
            this.updateOperationInDayOfBusTripSchedule(busTripScheduleDb, today);
        }
        // In case bus trip schedule is active -> need change status of orders of bus trip schedule before updating
        else {
            this.updateStatusOfOrderOfBusTripSchedule(busTripScheduleDb.getId(), businessPartner.getAccount().getId());
        }
        busTripScheduleDb.setSuspended(suspended);
        this.busTripScheduleRepository.save(busTripScheduleDb);
    }

    public ResBusTripScheduleForAdminDTO2 convertToResBusTripScheduleForAdminDTO2(BusTripSchedule busTripSchedule) throws ApplicationException {

        DropOffLocation dropOffLocation = this.dropOffLocationRepository.findByProvinceAndBusTripScheduleId(busTripSchedule.getBusTrip().getArrivalLocation(), busTripSchedule.getId())
                .orElseThrow(() -> new ApplicationException("Drop off location not found"));

        // Create bus info
        Bus bus = busTripSchedule.getBus();
        Images imageOfBus = this.imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.BUS), bus.getId()).get(0);
        ResBusTripScheduleForAdminDTO.BusInfo busInfo = ResBusTripScheduleForAdminDTO.BusInfo.builder()
                .licensePlate(bus.getLicensePlate())
                .imageRepresentative(imageOfBus.getPathImage())
                .busType(bus.getBusType())
                .build();

        // Create busTrip info
        ResBusTripDTO.BusTripInfo busTripInfo = ResBusTripDTO.BusTripInfo.builder()
                .id(busTripSchedule.getBusTrip().getId())
                .departureLocation(busTripSchedule.getBusTrip().getDepartureLocation())
                .arrivalLocation(dropOffLocation.getProvince())
                .build();

        ResBusTripScheduleForAdminDTO2 res = ResBusTripScheduleForAdminDTO2.builder()
                .busTripScheduleId(busTripSchedule.getId())
                .busInfo(busInfo)
                .busTripInfo(busTripInfo)
                .departureTime(busTripSchedule.getDepartureTime())
                .journeyDuration(dropOffLocation.getJourneyDuration())
                .priceTicket(CurrencyFormatterUtil.formatToVND(dropOffLocation.getPriceTicket()))
                .arrivalTime(busTripSchedule.getDepartureTime().plus(dropOffLocation.getJourneyDuration()))
                .isOperation(busTripSchedule.isOperation())
                .ratingTotal(busTripSchedule.getRatingTotal())
                .build();
        return res;
    }


}
