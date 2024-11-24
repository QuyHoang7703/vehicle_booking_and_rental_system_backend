package com.pbl6.VehicleBookingRental.user.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pbl6.VehicleBookingRental.user.config.VnPayConfig;
import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTripSchedule;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.OrderBusTrip;
import com.pbl6.VehicleBookingRental.user.domain.notification.Notification;
import com.pbl6.VehicleBookingRental.user.domain.notification.NotificationAccount;
import com.pbl6.VehicleBookingRental.user.dto.chat_dto.NotificationDTO;
import com.pbl6.VehicleBookingRental.user.dto.redis.OrderBusTripRedisDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.order.ResVnPayDTO;
import com.pbl6.VehicleBookingRental.user.repository.OrdersRepo;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRepository;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusTripScheduleRepository;
import com.pbl6.VehicleBookingRental.user.repository.chat.NotificationAccountRepo;
import com.pbl6.VehicleBookingRental.user.repository.chat.NotificationRepo;
import com.pbl6.VehicleBookingRental.user.repository.order.OrderBusTripRepository;
import com.pbl6.VehicleBookingRental.user.service.AccountService;
import com.pbl6.VehicleBookingRental.user.service.BusTripScheduleService;
import com.pbl6.VehicleBookingRental.user.service.OrderService;
import com.pbl6.VehicleBookingRental.user.service.RedisService;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import com.pbl6.VehicleBookingRental.user.util.VnPayUtil;
import com.pbl6.VehicleBookingRental.user.util.constant.NotificationTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
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
    private final OrderBusTripRepository orderBusTripRepository;
    private final NotificationRepo notificationRepo;
    private final AccountRepository accountRepository;
    private final NotificationAccountRepo notificationAccountRepo;
    private final NotificationServiceImpl notificationServiceImpl;

    @Override
    public ResVnPayDTO createPayment(HttpServletRequest request) throws ApplicationException, IdInvalidException {

        String key = request.getParameter("key");
        log.info("Key: " + key);

        // Get data from redis
        Object rawJson = redisService.getHashValue(key, "order-detail");
        // Convert json to orderBusTrip object
        OrderBusTripRedisDTO orderBusTripRedis = objectMapper.convertValue(rawJson, OrderBusTripRedisDTO.class);

        if(orderBusTripRedis==null){
            throw new ApplicationException("Order bus trip in redis not found");
        }

        ResVnPayDTO res = this.createUrlRequestToVnPay(request, orderBusTripRedis.getPriceTotal(), key);
        return res;
    }

    @Override
    public void handlePaymentSuccess(String transactionCode) throws IdInvalidException {
        String keyOrder = (String) redisService.getHashValue(transactionCode, "transactionCode");
        if(keyOrder.contains("BUS_TRIP")) {
            handleBusTripScheduleOrder(keyOrder, transactionCode);
        }

        // Delete orderBusTrip, transactionCode in Redis
        redisService.deleteHashFile(transactionCode, "transactionCode");
        redisService.deleteHashFile(keyOrder, "order-detail");
    }

    @Override
    public Orders findByTransactionCode(String transactionCode) throws ApplicationException {
        return this.ordersRepo.findByTransactionCode(transactionCode)
                .orElseThrow(() -> new ApplicationException("Order not found"));
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
        orderBusTrip.setAccount(currentAccount);

        BusTripSchedule busTripSchedule = this.busTripScheduleRepository.findById(orderBusTripRedisDTO.getBusTripScheduleId())
                .orElseThrow(() -> new IdInvalidException("Bus trip schedule not found"));
        orderBusTrip.setBusTripSchedule(busTripSchedule);

        orderBusTrip.setOrder(order);
        order.setOrderBusTrip(orderBusTrip);

        // Save all order and orderBusTrip, cascade = CascadeType.ALL => orderBusTrip is also saved
        this.ordersRepo.save(order);



        int busPartnerId = busTripSchedule.getBusTrip().getBusPartner().getId();
        int accountIdOfBusPartner = busTripSchedule.getBusTrip().getBusPartner().getBusinessPartner().getAccount().getId();
        createNotificationToPartner(accountIdOfBusPartner, busPartnerId, PartnerTypeEnum.BUS_PARTNER);
    }

    private void createNotificationToPartner(int accountIdOfPartner, int partnerId, PartnerTypeEnum partnerTypeEnum) {
        Notification notification = new Notification();
        notification.setCreate_at(new Date());
        notification.setMessage("Bạn có một đơn đặt xe mới ");
        notification.setTitle("Đơn thuê xe mới");
        notification.setType(NotificationTypeEnum.NEW_BOOKING);
        notificationRepo.save(notification);

        NotificationAccount notificationAccount = new NotificationAccount();
        notificationAccount.setNotification(notification);
        Optional<Account> partnerAccount = accountRepository.findById(accountIdOfPartner);
        notificationAccount.setAccount(partnerAccount.get());
        notificationAccount.setPartnerType(PartnerTypeEnum.BUS_PARTNER);
        notificationAccount.setSeen(false);
        notificationAccountRepo.save(notificationAccount);

        notificationServiceImpl.sendNotification
                (partnerId
                        ,String.valueOf(partnerTypeEnum)
                        , NotificationDTO.builder()
                                .id(notification.getId())
                                .type(notification.getType())
                                .title(notification.getTitle())
                                .message(notification.getMessage())
                                .create_at(notification.getCreate_at())
                                .isSeen(notificationAccount.isSeen())
                                .build());

    }
}
