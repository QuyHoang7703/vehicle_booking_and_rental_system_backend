package com.pbl6.VehicleBookingRental.user.service;


import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTripSchedule;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalOrders;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalService;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.VehicleRegister;
import com.pbl6.VehicleBookingRental.user.domain.notification.Notification;
import com.pbl6.VehicleBookingRental.user.domain.notification.NotificationAccount;
import com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO.VehicleRentalOrdersDTO;

import com.pbl6.VehicleBookingRental.user.dto.chat_dto.NotificationDTO;
import com.pbl6.VehicleBookingRental.user.dto.redis.OrderBusTripRedisDTO;
import com.pbl6.VehicleBookingRental.user.dto.redis.OrderVehicleRentalRedisDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.order.ResOrderKey;
import com.pbl6.VehicleBookingRental.user.dto.response.order.ResVehicleRentalOrderDetailDTO;
import com.pbl6.VehicleBookingRental.user.interfaces.VehicleRentalOrdersInterface;
import com.pbl6.VehicleBookingRental.user.repository.OrdersRepo;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRepository;
import com.pbl6.VehicleBookingRental.user.repository.chat.NotificationAccountRepo;
import com.pbl6.VehicleBookingRental.user.repository.chat.NotificationRepo;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleRentalServiceRepo;

import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import com.pbl6.VehicleBookingRental.user.util.constant.AccountEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static com.pbl6.VehicleBookingRental.user.util.constant.NotificationTypeEnum.NEW_BOOKING;
import static com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum.CAR_RENTAL_PARTNER;

@Service
@RequiredArgsConstructor
public class VehicleRentalOrderService implements VehicleRentalOrdersInterface {
    @Autowired
    private OrdersRepo ordersRepo;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private VehicleRentalServiceRepo vehicleRentalServiceRepo;
    @Autowired
    private final NotificationService notificationService;
    @Autowired
    private  NotificationRepo notificationRepo;
    @Autowired
    private NotificationAccountRepo notificationAccountRepo;
    private final AccountService accountService;
    private final RedisService<String, String, OrderVehicleRentalRedisDTO> redisService;


    //create order using redis
    @Override
    public OrderVehicleRentalRedisDTO create_order_Rental(VehicleRentalOrdersDTO vehicleRentalOrdersDTO) throws ApplicationException {
        String email = SecurityUtil.getCurrentLogin().isPresent() ? SecurityUtil.getCurrentLogin().get() : null;
        if(email==null){
            throw new ApplicationException("Email is invalid");
        }
        Account currentAccount = accountService.handleGetAccountByUsername(email);
        String orderId = UUID.randomUUID().toString().replaceAll("-", "");
        CarRentalService carRentalService = vehicleRentalServiceRepo.findById(vehicleRentalOrdersDTO.getVehicle_rental_service_id())
                .orElseThrow(() -> new ApplicationException("Vehicle Rental Service not found"));
        //create redis order
        OrderVehicleRentalRedisDTO orderVehicleRentalRedisDTO = new OrderVehicleRentalRedisDTO();
        orderVehicleRentalRedisDTO.setId(orderId);
        orderVehicleRentalRedisDTO.setPickup_location(vehicleRentalOrdersDTO.getPickup_location());
        orderVehicleRentalRedisDTO.setNumberOfVehicles(vehicleRentalOrdersDTO.getAmount());
        orderVehicleRentalRedisDTO.setStart_rental_time(vehicleRentalOrdersDTO.getStart_rental_time());
        orderVehicleRentalRedisDTO.setEnd_rental_time(vehicleRentalOrdersDTO.getEnd_rental_time());
        orderVehicleRentalRedisDTO.setAccount_Id(currentAccount.getId());
        orderVehicleRentalRedisDTO.setPriceTotal(vehicleRentalOrdersDTO.getTotal());
        orderVehicleRentalRedisDTO.setCreated_at(Instant.now());
        orderVehicleRentalRedisDTO.setVehicle_rental_service_id(carRentalService.getId());
        orderVehicleRentalRedisDTO.setCustomerName(vehicleRentalOrdersDTO.getCustomerName());
        orderVehicleRentalRedisDTO.setCustomerPhoneNumber(vehicleRentalOrdersDTO.getCustomerPhoneNumber());

        //Create key in redis
        // Save orderBusTrip trong redis
        String redisKeyOrderVehicleRental = "order:" + currentAccount.getEmail()
                + "-" + "VEHICLE_RENTAL"
                + "-" + orderId
                + "-" + carRentalService.getId()
                + "-" + vehicleRentalOrdersDTO.getAmount();
        orderVehicleRentalRedisDTO.setKey(redisKeyOrderVehicleRental);

        //
        redisService.setHashSet(redisKeyOrderVehicleRental, "order-detail", orderVehicleRentalRedisDTO);
        redisService.setTimeToLive(redisKeyOrderVehicleRental, 10);
        //update service amount
//        carRentalService.getVehicleRegister().setAmount(carRentalService.getVehicleRegister().getAmount() - vehicleRentalOrdersDTO.getAmount());
//        vehicleRentalServiceRepo.save(carRentalService);
        return orderVehicleRentalRedisDTO;
    }

    @Override
    public ResOrderKey getKeyOfOrderVehicleRentalRedisDTO(OrderVehicleRentalRedisDTO orderVehicleRentalRedisDTO) throws ApplicationException {
        return ResOrderKey.builder()
                .keyOrder(orderVehicleRentalRedisDTO.getKey())
                .build();
    }



    @Override
    public ResVehicleRentalOrderDetailDTO convertToResVehicleRentalOrderDetailDTO(Orders orders) throws ApplicationException {
        Account account = orders.getCarRentalOrders().getAccount();
        CarRentalOrders carRentalOrder = orders.getCarRentalOrders();

        ResVehicleRentalOrderDetailDTO.CustomerInfo customerInfo = ResVehicleRentalOrderDetailDTO.CustomerInfo.builder()
                .email(account.getEmail())
                .name(orders.getCustomerName())
                .phoneNumber(orders.getCustomerPhoneNumber())
                .build();

        ResVehicleRentalOrderDetailDTO.RentalInfo rentalInfo = this.createRentalInfo(carRentalOrder);

        ResVehicleRentalOrderDetailDTO.PricingInfo pricingInfo = this.createPricingInfo(carRentalOrder);

        ResVehicleRentalOrderDetailDTO res = ResVehicleRentalOrderDetailDTO.builder()
                .orderId(orders.getId())
                .transactionCode(orders.getTransactionCode())
                .customerInfo(customerInfo)
                .rentalInfo(rentalInfo)
                .pricingInfo(pricingInfo)
                .createAt(orders.getCreate_at())
                .build();

        return res;
    }

    private ResVehicleRentalOrderDetailDTO.RentalInfo createRentalInfo(CarRentalOrders carRentalOrder) throws ApplicationException {
        ResVehicleRentalOrderDetailDTO.RentalInfo rentalInfo = ResVehicleRentalOrderDetailDTO.RentalInfo.builder()
                .carRentalServiceId(carRentalOrder.getCarRentalService().getId())
                .numberOfVehicles(carRentalOrder.getAmount())
                .startRentalTime(carRentalOrder.getStart_rental_time())
                .endRentalTime(carRentalOrder.getEnd_rental_time())
                .pickupLocation(carRentalOrder.getPickup_location())
                .build();

        return rentalInfo;
    }

    private ResVehicleRentalOrderDetailDTO.PricingInfo createPricingInfo(CarRentalOrders carRentalOrder) throws ApplicationException {
        ResVehicleRentalOrderDetailDTO.PricingInfo pricingInfo = ResVehicleRentalOrderDetailDTO.PricingInfo.builder()
                .price(carRentalOrder.getPrice())
                .voucherValue(carRentalOrder.getVoucher_value())
                .voucherPercentage(carRentalOrder.getVoucher_percentage())
                .carDeposit(carRentalOrder.getCar_deposit())
                .reservationFee(carRentalOrder.getReservation_fee())
                .priceTotal(carRentalOrder.getTotal())
                .build();
        return pricingInfo;
    }
}
