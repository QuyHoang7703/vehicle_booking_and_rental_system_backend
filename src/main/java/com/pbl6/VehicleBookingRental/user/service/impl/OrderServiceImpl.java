package com.pbl6.VehicleBookingRental.user.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pbl6.VehicleBookingRental.user.config.VnPayConfig;
import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTripSchedule;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.OrderBusTrip;
import com.pbl6.VehicleBookingRental.user.dto.redis.OrderBusTripRedisDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.order.ResVnPayDTO;
import com.pbl6.VehicleBookingRental.user.repository.OrdersRepo;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusTripScheduleRepository;
import com.pbl6.VehicleBookingRental.user.repository.order.OrderBusTripRepository;
import com.pbl6.VehicleBookingRental.user.service.AccountService;
import com.pbl6.VehicleBookingRental.user.service.BusTripScheduleService;
import com.pbl6.VehicleBookingRental.user.service.OrderService;
import com.pbl6.VehicleBookingRental.user.service.RedisService;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import com.pbl6.VehicleBookingRental.user.util.VnPayUtil;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

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
        String keyOrderBusTrip = (String) redisService.getHashValue(transactionCode, "transactionCode");

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

        // Delete orderBusTrip, transactionCode in Redis
        redisService.deleteHashFile(transactionCode, "transactionCode");
        redisService.deleteHashFile(keyOrderBusTrip, "order-detail");

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
        log.info("TransactionCode: " + transactionCode);
        redisService.setHashSet(transactionCode, "transactionCode", keyOrder);
        redisService.setTimeToLive(transactionCode, 4);
        return res;
    }
}
