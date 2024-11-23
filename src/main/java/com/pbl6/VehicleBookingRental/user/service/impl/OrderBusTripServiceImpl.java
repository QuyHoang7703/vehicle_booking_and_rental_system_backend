package com.pbl6.VehicleBookingRental.user.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTripSchedule;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.OrderBusTrip;
import com.pbl6.VehicleBookingRental.user.dto.redis.OrderBusTripRedisDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.order.ReqOrderBusTripDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripScheduleDetailDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.order.ResOrderBusTripDTO;
import com.pbl6.VehicleBookingRental.user.repository.OrdersRepo;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusTripScheduleRepository;
import com.pbl6.VehicleBookingRental.user.repository.order.OrderBusTripRepository;
import com.pbl6.VehicleBookingRental.user.service.*;
import com.pbl6.VehicleBookingRental.user.util.CurrencyFormatterUtil;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderBusTripServiceImpl implements OrderBusTripService {
    private final OrderBusTripRepository orderBusTripRepository;
    private final OrdersRepo ordersRepo;
    private final AccountService accountService;
    private final RedisService<String, String, OrderBusTripRedisDTO> redisService;
//    private final RedisService<String, String, Orders> redisServiceOrders;
    private final BusTripScheduleRepository busTripScheduleRepository;
    private final ObjectMapper objectMapper;

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
        redisService.setTimeToLive(redisKeyOrderBusTrip, 1);


        // Number of available tickets minus number of ticket
        busTripSchedule.setAvailableSeats(busTripSchedule.getAvailableSeats() - reqOrderBusTripDTO.getNumberOfTicket());
        this.busTripScheduleRepository.save(busTripSchedule);


        return orderBusTripRedis;
    }

    @Override
    public ResOrderBusTripDTO convertToResOrderBusTripDTO(OrderBusTripRedisDTO orderBusTripRedis) throws ApplicationException {
        String email = SecurityUtil.getCurrentLogin().isPresent() ? SecurityUtil.getCurrentLogin().get() : null;
        if(email==null){
            throw new ApplicationException("Email is invalid");
        }

        Account currentAccount = accountService.handleGetAccountByUsername(email);

        BusTripSchedule busTripSchedule = this.busTripScheduleRepository.findById(orderBusTripRedis.getBusTripScheduleId())
                .orElseThrow(() -> new ApplicationException("BusTripSchedule not found"));

        // Create customer info
        ResOrderBusTripDTO.CustomerInfo customerInfo = ResOrderBusTripDTO.CustomerInfo.builder()
                .email(email)
                .name(orderBusTripRedis.getCustomerName())
                .phoneNumber(orderBusTripRedis.getCustomerPhoneNumber())
                .build();

        // Create order info
        ResOrderBusTripDTO.OrderInfo orderInfo = ResOrderBusTripDTO.OrderInfo.builder()
                .orderId(orderBusTripRedis.getId())
                .numberOfTicket(orderBusTripRedis.getNumberOfTicket())
                .discountPercentage(busTripSchedule.getDiscountPercentage())
                .orderDate(orderBusTripRedis.getOrderDate())
                .build();

        double pricePerTicket = busTripSchedule.getPriceTicket();
        double priceTotal = pricePerTicket * orderBusTripRedis.getNumberOfTicket();
        double discountPercentage = busTripSchedule.getDiscountPercentage();
        if(discountPercentage != 0.0){
            priceTotal = priceTotal * (1 - discountPercentage/100);
        }

        orderInfo.setPricePerTicket(CurrencyFormatterUtil.formatToVND(pricePerTicket));
        orderInfo.setPriceTotal(CurrencyFormatterUtil.formatToVND(priceTotal));

        // Create tripInfo
        ResOrderBusTripDTO.TripInfo tripInfo = ResOrderBusTripDTO.TripInfo.builder()
                .departureLocation(busTripSchedule.getBusTrip().getDepartureLocation())
                .arrivalLocation(busTripSchedule.getBusTrip().getArrivalLocation())
                .build();

        // Calculate departureDateTime
        LocalTime localTime = busTripSchedule.getDepartureTime();
        LocalDate localDate = orderBusTripRedis.getDepartureDate();
        Instant departureDateTime = this.changeInstant(localDate, localTime);
        tripInfo.setDepartureDateTime(departureDateTime);

        Duration duration = busTripSchedule.getBusTrip().getDurationJourney();
        Instant arrivalDateTime = departureDateTime.plus(duration);
        tripInfo.setArrivalDateTime(arrivalDateTime);

        // Create busInfo
        ResBusTripScheduleDetailDTO.BusInfo busInfo = ResBusTripScheduleDetailDTO.BusInfo.builder()
                .licensePlate(busTripSchedule.getBus().getLicensePlate())
                .busType(busTripSchedule.getBus().getBusType())
                .build();

        ResOrderBusTripDTO res = ResOrderBusTripDTO.builder()
                .customerInfo(customerInfo)
                .orderInfo(orderInfo)
                .tripInfo(tripInfo)
                .busInfo(busInfo)
                .key(orderBusTripRedis.getKey())
                .build();

        return res;
    }


    private Instant changeInstant(LocalDate localDate, LocalTime localTime) {
        LocalDateTime localDateTime = localDate.atTime(localTime);
        ZoneId zoneId = ZoneId.systemDefault(); // Múi giờ hệ thống
        return localDateTime.atZone(zoneId).toInstant();
    }

}
