package com.pbl6.VehicleBookingRental.user.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.Images;
import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.Bus;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTripSchedule;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.OrderBusTrip;
import com.pbl6.VehicleBookingRental.user.dto.Meta;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.redis.OrderBusTripRedisDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.order.ReqOrderBusTripDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripScheduleDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripScheduleDetailForAdminDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.order.ResCustomerInfoForOrderBusTrip;
import com.pbl6.VehicleBookingRental.user.dto.response.order.ResOrderBusTripDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.order.ResOrderBusTripDetailDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.order.ResOrderKey;
import com.pbl6.VehicleBookingRental.user.repository.OrdersRepo;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusTripScheduleRepository;
import com.pbl6.VehicleBookingRental.user.repository.image.ImageRepository;
import com.pbl6.VehicleBookingRental.user.repository.order.OrderBusTripRepository;
import com.pbl6.VehicleBookingRental.user.service.*;
import com.pbl6.VehicleBookingRental.user.util.CurrencyFormatterUtil;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import com.pbl6.VehicleBookingRental.user.util.constant.ImageOfObjectEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.OrderStatusEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderBusTripServiceImpl implements OrderBusTripService {
    private final OrderBusTripRepository orderBusTripRepository;
    private final OrdersRepo ordersRepo;
    private final AccountService accountService;
    private final RedisService<String, String, OrderBusTripRedisDTO> redisService;
    private final BusTripScheduleRepository busTripScheduleRepository;
    private final ObjectMapper objectMapper;
    private final ImageRepository imageRepository;
    private final BusService busService;

    @Override
    public OrderBusTripRedisDTO createOrderBusTrip(ReqOrderBusTripDTO reqOrderBusTripDTO) throws ApplicationException{
        String email = SecurityUtil.getCurrentLogin().isPresent() ? SecurityUtil.getCurrentLogin().get() : null;
        if(email==null){
            throw new ApplicationException("Email is invalid");
        }
        Account currentAccount = accountService.handleGetAccountByUsername(email);

        String orderId = UUID.randomUUID().toString().replaceAll("-", "");

//        Random random = new Random();
//        int orderId = 100000 + random.nextInt(900000);

        BusTripSchedule busTripSchedule = this.busTripScheduleRepository.findById(reqOrderBusTripDTO.getBusTripScheduleId())
                .orElseThrow(() -> new ApplicationException("BusTripSchedule not found"));


        OrderBusTripRedisDTO orderBusTripRedis = new OrderBusTripRedisDTO();
        orderBusTripRedis.setId(orderId);
        orderBusTripRedis.setNumberOfTicket(reqOrderBusTripDTO.getNumberOfTicket());
        orderBusTripRedis.setDepartureDate(reqOrderBusTripDTO.getDepartureDate());
        orderBusTripRedis.setPriceTotal(reqOrderBusTripDTO.getNumberOfTicket()*busTripSchedule.getPriceTicket());
        orderBusTripRedis.setAccount_Id(currentAccount.getId());
        orderBusTripRedis.setBusTripScheduleId(reqOrderBusTripDTO.getBusTripScheduleId());
        orderBusTripRedis.setCustomerName(reqOrderBusTripDTO.getCustomerName());
        orderBusTripRedis.setCustomerPhoneNumber(reqOrderBusTripDTO.getCustomerPhoneNumber());
        orderBusTripRedis.setOrderDate(Instant.now());

//        String redisKeyOrder = "order:" + currentAccount.getEmail();
//        redisService.setHashSet(redisKeyOrder, orderId, orde);
        // Save orderBusTrip trong redis
        String redisKeyOrderBusTrip = "order:" + currentAccount.getEmail()
                + "-" + "BUS_TRIP"
                + "-" + orderId
                + "-" + busTripSchedule.getId()
                + "-" + reqOrderBusTripDTO.getNumberOfTicket();
        orderBusTripRedis.setKey(redisKeyOrderBusTrip);

        redisService.setHashSet(redisKeyOrderBusTrip, "order-detail", orderBusTripRedis);
        redisService.setTimeToLive(redisKeyOrderBusTrip, 3);

        // Number of available tickets minus number of ticket
//        busTripSchedule.setAvailableSeats(busTripSchedule.getAvailableSeats() - reqOrderBusTripDTO.getNumberOfTicket());
//        this.busTripScheduleRepository.save(busTripSchedule);

        return orderBusTripRedis;
    }

    @Override
    public ResOrderKey getKeyOfOrderBusTripRedisDTO(OrderBusTripRedisDTO orderBusTripRedisDTO) throws ApplicationException {
        return ResOrderKey.builder()
                .keyOrder(orderBusTripRedisDTO.getKey())
                .build();
    }

    @Override
    public ResOrderBusTripDetailDTO convertToResOrderBusTripDetailDTO(Orders order) throws ApplicationException {
        String email = SecurityUtil.getCurrentLogin().isPresent() ? SecurityUtil.getCurrentLogin().get() : null;
        if(email==null){
            throw new ApplicationException("Access token is invalid or expired");
        }

        BusTripSchedule busTripSchedule = order.getOrderBusTrip().getBusTripSchedule();

        // Create customer info
        ResOrderBusTripDetailDTO.CustomerInfo customerInfo = ResOrderBusTripDetailDTO.CustomerInfo.builder()
                .email(email)
                .name(order.getCustomerName())
                .phoneNumber(order.getCustomerPhoneNumber())
                .build();

        OrderBusTrip orderBusTrip = order.getOrderBusTrip();

        // Create businessPartner info
        ResBusTripScheduleDTO.BusinessPartnerInfo businessPartnerInfo = this.createBusinessPartnerInfo(orderBusTrip);

        // Create order info
        ResOrderBusTripDTO.OrderInfo orderInfo = this.createOrderInfo(orderBusTrip);

        // Create trip info
        ResOrderBusTripDTO.TripInfo tripInfo = this.createTripInfo(orderBusTrip);

        // Create busInfo
        ResBusTripScheduleDetailForAdminDTO.BusInfo busInfo = ResBusTripScheduleDetailForAdminDTO.BusInfo.builder()
                .licensePlate(busTripSchedule.getBus().getLicensePlate())
                .busType(busTripSchedule.getBus().getBusType())
                .build();

        ResOrderBusTripDetailDTO res = ResOrderBusTripDetailDTO.builder()
                .customerInfo(customerInfo)
                .businessPartnerInfo(businessPartnerInfo)
                .orderInfo(orderInfo)
                .tripInfo(tripInfo)
                .busInfo(busInfo)
                .cancelTime(order.getCancelTime())
                .build();


        return res;
    }

    @Override
    public ResOrderBusTripDTO convertToResOrderBusTripDTO(OrderBusTrip orderBusTrip) throws ApplicationException, IdInvalidException {
//        BusinessPartner businessPartner = orderBusTrip.getBusTripSchedule().getBus().getBusPartner().getBusinessPartner();
//        ResBusTripScheduleDTO.BusinessPartnerInfo businessPartnerInfo = ResBusTripScheduleDTO.BusinessPartnerInfo.builder()
//                .id(businessPartner.getId())
//                .name(businessPartner.getBusinessName())
//                .build();
        ResBusTripScheduleDTO.BusinessPartnerInfo businessPartnerInfo = this.createBusinessPartnerInfo(orderBusTrip);

        Bus bus = orderBusTrip.getBusTripSchedule().getBus();
        ResBusDTO busInfo = this.busService.convertToResBus(bus);

        ResOrderBusTripDTO.OrderInfo orderInfo = this.createOrderInfo(orderBusTrip);

        ResOrderBusTripDTO.TripInfo tripInfo= this.createTripInfo(orderBusTrip);

        ResOrderBusTripDTO res = ResOrderBusTripDTO.builder()
                .businessPartner(businessPartnerInfo)
                .busInfo(busInfo)
                .orderInfo(orderInfo)
                .tripInfo(tripInfo)
                .cancelTime(orderBusTrip.getOrder().getCancelTime())
                .build();
        return res;
    }

    @Override
    public ResultPaginationDTO getAllOrderBusTrip(Specification<OrderBusTrip> spec, Pageable pageable, Boolean isGone) throws ApplicationException {
        String email = SecurityUtil.getCurrentLogin().isPresent() ? SecurityUtil.getCurrentLogin().get() : null;
        if(email==null){
            throw new ApplicationException("Access token is invalid or expired");
        }
        Specification<OrderBusTrip> newSpec = (root, query, criteriaBuilder) -> {
            Join<OrderBusTrip, Account> accountJoin = root.join("account");
            Predicate orderOfAccount = criteriaBuilder.equal(accountJoin.get("email"), email);
            Join<OrderBusTrip, Orders> orderJoin = root.join("order");
            query.orderBy(criteriaBuilder.desc(orderJoin.get("create_at")));

            if(isGone==null) {
                return criteriaBuilder.and(orderOfAccount);
            }
            Predicate statusOfOrder = isGone ? criteriaBuilder.lessThan(root.get("departureDate"), LocalDate.now())
                    : criteriaBuilder.greaterThanOrEqualTo(root.get("departureDate"), LocalDate.now());

            return criteriaBuilder.and(orderOfAccount, statusOfOrder);
        };

        Specification<OrderBusTrip> finalSpec = spec.and(newSpec);

        Page<OrderBusTrip> page = this.orderBusTripRepository.findAll(finalSpec, pageable);

        ResultPaginationDTO res = new ResultPaginationDTO();

        Meta meta = new Meta();

        meta.setCurrentPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        List<ResOrderBusTripDTO> resOrderBusTripDTOS = page.getContent().stream()
                .map(orderBusTrip -> {
                    try {
                        return this.convertToResOrderBusTripDTO(orderBusTrip);
                    } catch (ApplicationException | IdInvalidException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        res.setMeta(meta);
        res.setResult(resOrderBusTripDTOS);

        return res;
    }

    @Override
    public void cancelOrderBusTrip(String orderBusTripId) throws IdInvalidException, ApplicationException {
        OrderBusTrip orderBusTrip = this.orderBusTripRepository.findById(orderBusTripId)
                .orElseThrow(() -> new IdInvalidException("Order Bus Trip not found"));

        String email = SecurityUtil.getCurrentLogin().isPresent() ? SecurityUtil.getCurrentLogin().get() : null;
        if(email==null){
            throw new ApplicationException("Access token is invalid or expired");
        }
        Account currentAccount = this.accountService.handleGetAccountByUsername(email);
        if(!orderBusTrip.getAccount().equals(currentAccount)){
            throw new ApplicationException("You don't have permission to cancel this order bus trip");
        }

        // Lấy ngày và giờ khởi hành của chuyến xe
        LocalDate departureDate = orderBusTrip.getDepartureDate();
        LocalTime departureTime = orderBusTrip.getBusTripSchedule().getDepartureTime();
        // Chuyển thành kiểu LocalDateTime để tạo ra Instant
        LocalDateTime departureDateTime = departureDate.atTime(departureTime);
        Instant departureInstant = departureDateTime.toInstant(ZoneOffset.UTC);

        log.info("Departure Instant: " + departureInstant);
        log.info("Cancel Date: " + Instant.now().atZone(ZoneId.of("Asia/Ho_Chi_Minh")));
        log.info("Departure Date: " + departureDate);
        log.info("Cancel LocalDate: " + LocalDate.now());

        // So sánh ngày khởi hành với ngày hủy đơn
        if(departureDate.equals(LocalDate.now())){
            Duration duration = Duration.between(departureInstant, Instant.now().atZone(ZoneId.of("Asia/Ho_Chi_Minh")));
            if(duration.toMinutes()>3) {
                throw new ApplicationException("You can't cancel this order because it has exceed the time limit");
            }
        }
        orderBusTrip.setStatus(OrderStatusEnum.CANCELLED);
        log.info("Canceled order bus trip");
        this.orderBusTripRepository.save(orderBusTrip);

        Orders order = orderBusTrip.getOrder();
        order.setCancelTime(Instant.now());
        this.ordersRepo.save(order);

    }

    @Override
    public ResultPaginationDTO getCustomersByOrderBusTrip(int busTripScheduleId, Specification<OrderBusTrip> spec, Pageable pageable) {
        Specification<OrderBusTrip> getByOrderBusTripSpec = (root, query, criteriaBuilder) -> {
            Join<OrderBusTrip, BusTripSchedule> joinBusTripSchedule = root.join("busTripSchedule");
            return criteriaBuilder.equal(joinBusTripSchedule.get("id"), busTripScheduleId);
        };
        Specification<OrderBusTrip> finalSpec = spec.and(getByOrderBusTripSpec);
        Page<OrderBusTrip> page = this.orderBusTripRepository.findAll(finalSpec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();

        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        res.setMeta(meta);

        List<ResCustomerInfoForOrderBusTrip> list = page.getContent().stream()
                .map(orderBusTrip -> this.convertToResCustomerInfoForOrderBusTrip(orderBusTrip))
                .toList();

        res.setResult(list);
        return res;
    }

    private ResCustomerInfoForOrderBusTrip convertToResCustomerInfoForOrderBusTrip(OrderBusTrip orderBusTrip){
        Account account = orderBusTrip.getAccount();

        ResCustomerInfoForOrderBusTrip res = ResCustomerInfoForOrderBusTrip.builder()
                .name(account.getName())
                .email(account.getEmail())
                .phoneNumber(account.getPhoneNumber())
                .orderTime(orderBusTrip.getOrder().getCreate_at())
                .numberOfTicker(orderBusTrip.getNumberOfTicket())
                .totalPrice(CurrencyFormatterUtil.formatToVND(orderBusTrip.getPriceTotal()))
                .cancelTime(orderBusTrip.getOrder().getCancelTime())
                .build();

        return res;
    }


    private Instant changeInstant(LocalDate localDate, LocalTime localTime) {
        LocalDateTime localDateTime = localDate.atTime(localTime);
        ZoneId zoneId = ZoneId.systemDefault(); // Múi giờ hệ thống
        return localDateTime.atZone(zoneId).toInstant();
    }

    private ResOrderBusTripDTO.OrderInfo createOrderInfo(OrderBusTrip orderBusTrip) {
        ResOrderBusTripDTO.OrderInfo orderInfo = ResOrderBusTripDTO.OrderInfo.builder()
                .orderId(orderBusTrip.getId())
                .transactionCode(orderBusTrip.getOrder().getTransactionCode())
                .numberOfTicket(orderBusTrip.getNumberOfTicket())
                .orderDate(orderBusTrip.getOrder().getCreate_at())
                .build();

        BusTripSchedule busTripSchedule = orderBusTrip.getBusTripSchedule();

        double pricePerTicket = busTripSchedule.getPriceTicket();
        double priceTotal = pricePerTicket * orderInfo.getNumberOfTicket();
        double discountPercentage = busTripSchedule.getDiscountPercentage();
        if(discountPercentage != 0.0){
            priceTotal = priceTotal * (1 - discountPercentage/100);
        }

        orderInfo.setPricePerTicket(CurrencyFormatterUtil.formatToVND(pricePerTicket));
        orderInfo.setPriceTotal(CurrencyFormatterUtil.formatToVND(priceTotal));
        orderInfo.setDiscountPercentage(busTripSchedule.getDiscountPercentage());

        return orderInfo;
    }

    private ResOrderBusTripDTO.TripInfo createTripInfo(OrderBusTrip orderBusTrip) {
        BusTripSchedule busTripSchedule = orderBusTrip.getBusTripSchedule();

        // Create trip info
        ResOrderBusTripDTO.TripInfo tripInfo = ResOrderBusTripDTO.TripInfo.builder()
                .id(orderBusTrip.getBusTripSchedule().getBusTrip().getId())
                .departureLocation(busTripSchedule.getBusTrip().getDepartureLocation())
                .arrivalLocation(busTripSchedule.getBusTrip().getArrivalLocation())
                .build();

        // Calculate departureDateTime
        LocalTime localTime = busTripSchedule.getDepartureTime();
        LocalDate localDate = orderBusTrip.getDepartureDate();
        Instant departureDateTime = this.changeInstant(localDate, localTime);
        tripInfo.setDepartureDateTime(departureDateTime);

        Duration duration = busTripSchedule.getBusTrip().getDurationJourney();
        tripInfo.setDurationJourney(duration);
        Instant arrivalDateTime = departureDateTime.plus(duration);
        tripInfo.setArrivalDateTime(arrivalDateTime);


        return tripInfo;
    }

    private ResBusTripScheduleDTO.BusinessPartnerInfo createBusinessPartnerInfo(OrderBusTrip orderBusTrip) {
        BusinessPartner businessPartner = orderBusTrip.getBusTripSchedule().getBus().getBusPartner().getBusinessPartner();
        ResBusTripScheduleDTO.BusinessPartnerInfo businessPartnerInfo = ResBusTripScheduleDTO.BusinessPartnerInfo.builder()
                .id(businessPartner.getId())
                .name(businessPartner.getBusinessName())
                .accountId(businessPartner.getAccount().getId())
                .build();
        return businessPartnerInfo;
    }
}
