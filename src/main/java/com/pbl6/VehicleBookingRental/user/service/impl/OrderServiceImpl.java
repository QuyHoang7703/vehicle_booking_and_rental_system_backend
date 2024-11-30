package com.pbl6.VehicleBookingRental.user.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pbl6.VehicleBookingRental.user.config.VnPayConfig;
import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTripSchedule;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.OrderBusTrip;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalOrders;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalService;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.VehicleRegister;
import com.pbl6.VehicleBookingRental.user.domain.notification.Notification;
import com.pbl6.VehicleBookingRental.user.domain.notification.NotificationAccount;
import com.pbl6.VehicleBookingRental.user.dto.chat_dto.NotificationDTO;
import com.pbl6.VehicleBookingRental.user.dto.redis.OrderBusTripRedisDTO;
import com.pbl6.VehicleBookingRental.user.dto.redis.OrderVehicleRentalRedisDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.order.ResVnPayDTO;
import com.pbl6.VehicleBookingRental.user.repository.OrdersRepo;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRepository;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusTripScheduleRepository;
import com.pbl6.VehicleBookingRental.user.repository.chat.NotificationAccountRepo;
import com.pbl6.VehicleBookingRental.user.repository.chat.NotificationRepo;
import com.pbl6.VehicleBookingRental.user.repository.order.OrderBusTripRepository;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleRegisterRepo;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleRentalServiceRepo;
import com.pbl6.VehicleBookingRental.user.service.*;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import com.pbl6.VehicleBookingRental.user.util.VnPayUtil;
import com.pbl6.VehicleBookingRental.user.util.constant.*;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;


import static com.pbl6.VehicleBookingRental.user.util.constant.NotificationTypeEnum.NEW_BOOKING;
import static com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum.BUS_PARTNER;
import static com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum.CAR_RENTAL_PARTNER;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final ObjectMapper objectMapper;
    private final VnPayConfig vnPayConfig;
    private final RedisService<String, String, Object> redisService;
    private final OrdersRepo ordersRepo;
    private final AccountService accountService;
    private final BusTripScheduleRepository busTripScheduleRepository;
    private final VehicleRentalServiceRepo vehicleRentalServiceRepo;

    private final NotificationRepo notificationRepo;
    private final AccountRepository accountRepository;
    private final NotificationAccountRepo notificationAccountRepo;
    private final NotificationServiceImpl notificationServiceImpl;
    private final NotificationService notificationService;


    @Override
    public ResVnPayDTO createPayment(HttpServletRequest request) throws ApplicationException, IdInvalidException {

        String key = request.getParameter("key");
        log.info("Key: " + key);
        String[] parts = key.split("-");
        String typeOfOrder = parts[1];

        // Get data from redis
        Object rawJson = redisService.getHashValue(key, "order-detail");
        ResVnPayDTO res = new ResVnPayDTO();
        switch (typeOfOrder)
        {
            case "BUS_TRIP":{
                // Convert json to orderBusTrip object
                OrderBusTripRedisDTO orderBusTripRedis = objectMapper.convertValue(rawJson, OrderBusTripRedisDTO.class);

                if(orderBusTripRedis==null){
                    throw new ApplicationException("Order bus trip in redis not found");
                }
                res = this.createUrlRequestToVnPay(request, orderBusTripRedis.getPriceTotal(), key);
            };break;
            case "VEHICLE_RENTAL":{
                // Convert json to orderBusTrip object
                OrderVehicleRentalRedisDTO orderVehicleRental = objectMapper.convertValue(rawJson, OrderVehicleRentalRedisDTO.class);

                if(orderVehicleRental==null){
                    throw new ApplicationException("Order bus trip in redis not found");
                }
                res = this.createUrlRequestToVnPay(request, orderVehicleRental.getPriceTotal(), key);
            };break;
            default:
        }
        return res;
    }

    @Override
    public void handlePaymentSuccess(String transactionCode) throws IdInvalidException {
        String keyOrder = (String) redisService.getHashValue(transactionCode, "transactionCode");
        if(keyOrder.contains("BUS_TRIP")) {
            handleBusTripScheduleOrder(keyOrder, transactionCode);
        }
        if(keyOrder.contains("VEHICLE_RENTAL")) {
            handleVehicleRentalOrder(keyOrder);
        }
        // Delete orderBusTrip, transactionCode in Redis
        redisService.deleteHashFile(transactionCode, "transactionCode");
        redisService.deleteHashFile(keyOrder, "order-detail");
    }

    @Override
    public Orders findByTransactionCode(String transactionCode) throws ApplicationException {
        String email = SecurityUtil.getCurrentLogin().isPresent()?SecurityUtil.getCurrentLogin().get():null;
        if(email==null) {
            throw new ApplicationException("Access token not found or expired");
        }
        Account account = this.accountService.handleGetAccountByUsername(email);
        Orders orders = this.ordersRepo.findByTransactionCode(transactionCode)
                .orElseThrow(() -> new ApplicationException("Order not found"));
        if(!orders.getOrderBusTrip().getAccount().equals(account)) {
            throw new ApplicationException("You don't have permission to see this order");
        }
        return orders;
    }


    private ResVnPayDTO createUrlRequestToVnPay (HttpServletRequest request, Double amount, String keyOrder) throws ApplicationException, IdInvalidException {
        // Create request payment with params to call VnPay's API
        String bankCode = request.getParameter("bankCode");
        // Get obligatory params
        Map<String, String> vnpParamsMap = vnPayConfig.getVnPayConfig();
        // Create additional params about transaction
        vnpParamsMap.put("vnp_Amount", String.valueOf(Math.round(amount* 100)));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VnPayUtil.getIpAddress(request));
        // Build request url
        String queryUrl = VnPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VnPayUtil.getPaymentURL(vnpParamsMap, false);
        queryUrl += "&vnp_SecureHash=" + VnPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;

        ResVnPayDTO res = new ResVnPayDTO();
        res.setMessage("Success");
        res.setPaymentUrl(paymentUrl);

        String transactionCode = vnpParamsMap.get("vnp_TxnRef");
//        booking.setTransactionCode(transactionCode);

        redisService.setHashSet(transactionCode, "transactionCode", keyOrder);
        redisService.setTimeToLive(transactionCode, 4);

        return res;
    }

    private void handleBusTripScheduleOrder(String keyOrderBusTrip, String transactionCode) throws IdInvalidException {
        // Get data from redis
        Object rawJson = redisService.getHashValue(keyOrderBusTrip, "order-detail");
        // Convert json to orderBusTrip object
        OrderBusTripRedisDTO orderBusTripRedisDTO = objectMapper.convertValue(rawJson, OrderBusTripRedisDTO.class);

        // Create order to save in database
        Orders order = new Orders();
        order.setId(orderBusTripRedisDTO.getId());
        order.setOrder_type("BUS_TRIP_ORDER");
        order.setCustomerName(orderBusTripRedisDTO.getCustomerName());
        order.setCustomerPhoneNumber(orderBusTripRedisDTO.getCustomerPhoneNumber());
        order.setTransactionCode(transactionCode);

        Account currentAccount = accountService.fetchAccountById(orderBusTripRedisDTO.getAccount_Id());

        // Create orderBusTrip to save in database
        OrderBusTrip orderBusTrip = new OrderBusTrip();
        orderBusTrip.setId(orderBusTripRedisDTO.getId());
        orderBusTrip.setNumberOfTicket(orderBusTripRedisDTO.getNumberOfTicket());
        orderBusTrip.setPriceTotal(orderBusTripRedisDTO.getPriceTotal());
        orderBusTrip.setDepartureDate(orderBusTripRedisDTO.getDepartureDate());
        orderBusTrip.setStatus(OrderStatusEnum.COMPLETED);
        orderBusTrip.setAccount(currentAccount);

        BusTripSchedule busTripSchedule = this.busTripScheduleRepository.findById(orderBusTripRedisDTO.getBusTripScheduleId())
                .orElseThrow(() -> new IdInvalidException("Bus trip schedule not found"));
        orderBusTrip.setBusTripSchedule(busTripSchedule);

        orderBusTrip.setOrder(order);
        order.setOrderBusTrip(orderBusTrip);

        // Save all order and orderBusTrip, cascade = CascadeType.ALL => orderBusTrip is also saved
        this.ordersRepo.save(order);


        int accountIdOfBusPartner = busTripSchedule.getBusTrip().getBusPartner().getBusinessPartner().getAccount().getId();
        //create new notification
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setMessage("Bạn có một đơn đặt xe mới ");
        notificationDTO.setTitle("Đơn đặt xe mới");
        notificationDTO.setType(NotificationTypeEnum.NEW_BOOKING);
        notificationDTO.setCreate_at(new Date());
        notificationDTO.setSeen(false);
        createNotificationToPartner(accountIdOfBusPartner,  AccountEnum.BUS_PARTNER,notificationDTO);
    }

    private void createNotificationToPartner(int accountIdOfPartner,  AccountEnum partnerTypeEnum,NotificationDTO notificationDTO) {
        Notification notification = new Notification();
        notification.setCreate_at(notificationDTO.getCreate_at());
        notification.setType(notificationDTO.getType());
        notification.setTitle(notificationDTO.getTitle());
        notification.setMessage(notificationDTO.getMessage());
        notification.setSeen(notificationDTO.isSeen());
        notificationRepo.save(notification);

        NotificationAccount notificationAccount = new NotificationAccount();
        notificationAccount.setNotification(notification);
        Optional<Account> partnerAccount = accountRepository.findById(accountIdOfPartner);
        notificationAccount.setAccount(partnerAccount.get());
        notificationAccount.setPartnerType(partnerTypeEnum);
        notificationAccountRepo.save(notificationAccount);

        notificationServiceImpl.sendNotification
                (accountIdOfPartner
                        ,String.valueOf(partnerTypeEnum)
                        , NotificationDTO.builder()
                                .id(notification.getId())
                                .type(notification.getType())
                                .title(notification.getTitle())
                                .message(notification.getMessage())
                                .create_at(notification.getCreate_at())
                                .isSeen(notification.isSeen())
                                .build());

    }
    private void handleVehicleRentalOrder(String key) throws IdInvalidException{
        // Get data from redis
        Object rawJson = redisService.getHashValue(key, "order-detail");
        // Convert json to orderBusTrip object
        OrderVehicleRentalRedisDTO orderVehicleRentalRedisDTO = objectMapper.convertValue(rawJson, OrderVehicleRentalRedisDTO.class);

        // Create order to save in database
        Orders order = new Orders();
        order.setId(orderVehicleRentalRedisDTO.getId());
        order.setOrder_type("VEHICLE_RENTAL_ORDER");
        order.setCustomerName(orderVehicleRentalRedisDTO.getCustomerName());
        order.setCustomerPhoneNumber(orderVehicleRentalRedisDTO.getCustomerPhoneNumber());

        Account currentAccount = accountService.fetchAccountById(orderVehicleRentalRedisDTO.getAccount_Id());

        // Create orderBusTrip to save in database
        CarRentalOrders carRentalOrders = new CarRentalOrders();
        carRentalOrders.setStart_rental_time(orderVehicleRentalRedisDTO.getStart_rental_time());
        carRentalOrders.setEnd_rental_time(orderVehicleRentalRedisDTO.getEnd_rental_time());
        carRentalOrders.setPickup_location(orderVehicleRentalRedisDTO.getPickup_location());
        carRentalOrders.setCreated_at(orderVehicleRentalRedisDTO.getCreated_at());
        carRentalOrders.setVoucher_value(orderVehicleRentalRedisDTO.getVoucher_value());
        carRentalOrders.setVoucher_percentage(orderVehicleRentalRedisDTO.getVoucher_percentage());
        carRentalOrders.setAmount(orderVehicleRentalRedisDTO.getNumberOfVehicles());
        carRentalOrders.setCar_deposit(orderVehicleRentalRedisDTO.getCar_deposit());
        carRentalOrders.setReservation_fee(orderVehicleRentalRedisDTO.getReservation_fee());
        carRentalOrders.setPrice(orderVehicleRentalRedisDTO.getPrice());
        carRentalOrders.setId(orderVehicleRentalRedisDTO.getId());

        carRentalOrders.setTotal(orderVehicleRentalRedisDTO.getPriceTotal());

        carRentalOrders.setAccount(currentAccount);

        CarRentalService carRentalService = this.vehicleRentalServiceRepo.findById(orderVehicleRentalRedisDTO.getVehicle_rental_service_id())
                .orElseThrow(() -> new IdInvalidException("Bus trip schedule not found"));
        carRentalOrders.setCarRentalService(carRentalService);

        carRentalOrders.setOrder(order);
        order.setCarRentalOrders(carRentalOrders);

        // Save all order and orderBusTrip, cascade = CascadeType.ALL => orderBusTrip is also saved
        this.ordersRepo.save(order);


        int accountIdOfVehicleRentalPartner = carRentalOrders.getCarRentalService().getVehicleRegister().getCarRentalPartner()
                                    .getBusinessPartner().getAccount().getId();
        //create new notification
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setMessage("Bạn có một đơn đặt xe mới ");
        notificationDTO.setTitle("Đơn thuê xe mới");
        notificationDTO.setType(NotificationTypeEnum.NEW_BOOKING);
        notificationDTO.setCreate_at(new Date());
        notificationDTO.setSeen(false);
        createNotificationToPartner(accountIdOfVehicleRentalPartner,  AccountEnum.CAR_RENTAL_PARTNER,notificationDTO);
    }
}
