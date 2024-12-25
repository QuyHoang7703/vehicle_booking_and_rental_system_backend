package com.pbl6.VehicleBookingRental.user.service;


import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
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
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleRentalOrderRepo;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleRentalServiceRepo;

import com.pbl6.VehicleBookingRental.user.util.DateUtil;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import com.pbl6.VehicleBookingRental.user.util.constant.AccountEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.NotificationTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.pbl6.VehicleBookingRental.user.util.constant.NotificationTypeEnum.NEW_BOOKING;
import static com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum.CAR_RENTAL_PARTNER;

@Service
@RequiredArgsConstructor
public class VehicleRentalOrderService implements VehicleRentalOrdersInterface {
    @Autowired
    private VehicleRentalServiceRepo vehicleRentalServiceRepo;
    @Autowired
    private VehicleRentalOrderRepo vehicleRentalOrderRepo;
    @Autowired
    private BusinessPartnerService businessPartnerService;
    private final AccountService accountService;
    private final RedisService<String, String, OrderVehicleRentalRedisDTO> redisService;
    private final RedisService<String, String, Integer> redisService2;
    private final NotificationService notificationService;
    private final DateUtil dateUtil;
    //create order using redis
    @Override
    public OrderVehicleRentalRedisDTO create_order_Rental(VehicleRentalOrdersDTO vehicleRentalOrdersDTO) throws ApplicationException {
        String email = SecurityUtil.getCurrentLogin().isPresent() ? SecurityUtil.getCurrentLogin().get() : null;
        if(email==null){
            throw new ApplicationException("Email is invalid");
        }
        Account currentAccount = accountService.handleGetAccountByUsername(email);
        CarRentalService carRentalService = vehicleRentalServiceRepo.findById(vehicleRentalOrdersDTO.getVehicle_rental_service_id())
                .orElseThrow(() -> new ApplicationException("Vehicle Rental Service not found"));
        Integer amount =  carRentalService.getVehicleRegister().getAmount();
        Integer rentalAmount = vehicleRentalOrdersDTO.getAmount();
        //
        List<String> timeSlots = dateUtil.generateTimeSlots(vehicleRentalOrdersDTO.getStart_rental_time(),vehicleRentalOrdersDTO.getEnd_rental_time());
        System.out.println(timeSlots);
        for (String timeSlot : timeSlots) {
            String key = "vehicle-rental:" + carRentalService.getId();
            if(!redisService2.isHashFieldExists(key,timeSlot)){
                // Trường hợp key chưa tồn tại: thiết lập giá trị ban đầu
                redisService2.setHashSet(key, timeSlot, amount);
                // Thiết lập TTL
                redisService2.setTimeToLive(key, dateUtil.calculateAndSetTTL(timeSlot) / 60);
            }
            // Lấy số lượng hiện tại từ Redis
            Integer currentAmount = redisService2.getHashValue(key, timeSlot);
            if (currentAmount > 0) {
                // Trường hợp có dữ liệu: kiểm tra số lượng và giảm đi
                long updatedAmount = redisService2.incrementHashValue(key, timeSlot, -rentalAmount);

                // Nếu giảm thất bại (hết xe)
                if (updatedAmount < 0) {
                    // Tăng lại vì lệnh giảm đã trừ nhầm
                    redisService2.incrementHashValue(key, timeSlot, rentalAmount);
                    throw new ApplicationException("Không có xe khả dụng trong khung giờ "+ timeSlot );
                }
            } else {
                // Trường hợp hết xe
                throw new ApplicationException("Không có xe khả dụng trong khung giờ "+ timeSlot );
            }
        }
        String orderId = UUID.randomUUID().toString().replaceAll("-", "");
        //create redis order
        OrderVehicleRentalRedisDTO orderVehicleRentalRedisDTO = new OrderVehicleRentalRedisDTO();
        orderVehicleRentalRedisDTO.setId(orderId);
        orderVehicleRentalRedisDTO.setCustomerName(vehicleRentalOrdersDTO.getCustomerName());
        orderVehicleRentalRedisDTO.setCustomerPhoneNumber(vehicleRentalOrdersDTO.getCustomerPhoneNumber());
        orderVehicleRentalRedisDTO.setNumberOfVehicles(vehicleRentalOrdersDTO.getAmount());
        orderVehicleRentalRedisDTO.setStart_rental_time(vehicleRentalOrdersDTO.getStart_rental_time());
        orderVehicleRentalRedisDTO.setEnd_rental_time(vehicleRentalOrdersDTO.getEnd_rental_time());
        orderVehicleRentalRedisDTO.setPickup_location(vehicleRentalOrdersDTO.getPickup_location());
        orderVehicleRentalRedisDTO.setCreated_at(Instant.now());
        orderVehicleRentalRedisDTO.setVehicle_rental_service_id(carRentalService.getId());
        orderVehicleRentalRedisDTO.setVoucherId(vehicleRentalOrdersDTO.getVoucherId());
        orderVehicleRentalRedisDTO.setAccount_Id(currentAccount.getId());

        orderVehicleRentalRedisDTO.setPriceTotal(vehicleRentalOrdersDTO.getTotal());
        orderVehicleRentalRedisDTO.setVoucher_percentage(vehicleRentalOrdersDTO.getVoucher_percentage());
        orderVehicleRentalRedisDTO.setVoucher_value(vehicleRentalOrdersDTO.getVoucher_value());
        orderVehicleRentalRedisDTO.setReservation_fee(vehicleRentalOrdersDTO.getReservation_fee());
        orderVehicleRentalRedisDTO.setPrice(vehicleRentalOrdersDTO.getPrice());
        orderVehicleRentalRedisDTO.setCar_deposit(vehicleRentalOrdersDTO.getCar_deposit());

        //Create key in redis
        // Save orderBusTrip trong redis
        String redisKeyOrderVehicleRental = "order:" + currentAccount.getEmail()
                + "$" + "VEHICLE_RENTAL"
                + "$" + orderId
                + "$" + carRentalService.getId()
                + "$" + vehicleRentalOrdersDTO.getAmount()
                + "$" + vehicleRentalOrdersDTO.getStart_rental_time()
                + "$" + vehicleRentalOrdersDTO.getEnd_rental_time();
        orderVehicleRentalRedisDTO.setKey(redisKeyOrderVehicleRental);

        //
        redisService.setHashSet(redisKeyOrderVehicleRental, "order-detail", orderVehicleRentalRedisDTO);
        redisService.setTimeToLive(redisKeyOrderVehicleRental, 5);


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
                .accountId(account.getId())
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
                .cancelAt(carRentalOrder.getOrder().getCancelTime())
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

    public double calculatePriceOrderByStartAndEndDate(Instant startRentalTime,Instant endRentalTime,double priceOneDay){
        double total = 0.0;
        // Chuyển đổi Instant thành LocalDateTime để làm việc với giờ cụ thể
        LocalDateTime start = LocalDateTime.ofInstant(startRentalTime, ZoneId.systemDefault());
        LocalDateTime end = LocalDateTime.ofInstant(endRentalTime, ZoneId.systemDefault());

        //Thue trong cung 1 ngay
        if(start.toLocalDate().equals(end.toLocalDate())){
            total = calculatePriceInDay(start,end,priceOneDay);
        }else{
            //Thuê nhiều ngày
            //Tiền startTime
            total += calculatePriceInDay(start,LocalDateTime.of(start.toLocalDate(), LocalTime.of(22, 0)),priceOneDay);
            total += calculatePriceInDay(LocalDateTime.of(end.toLocalDate(), LocalTime.of(6, 0)),end,priceOneDay);

            // Tính giá cho các ngày đầy đủ ở giữa
            LocalDateTime currentDay = start.toLocalDate().atStartOfDay().plusDays(1); // 12-12-2024 (toLocalDate)-> 00:00:00 12-12-2024 -> 00:00:00 13-12-2024
            while (currentDay.isBefore(end.toLocalDate().atStartOfDay())) {
                total += priceOneDay;
                currentDay = currentDay.plusDays(1);
            }
        }
        return total;
    }

    @Override
    public List<ResVehicleRentalOrderDetailDTO> getOrderByServiceId(int id) {
        List<CarRentalOrders> carRentalOrders = vehicleRentalOrderRepo.findCarRentalOrdersByCarRentalServiceId(id);

        return Optional.ofNullable(carRentalOrders)
                .orElse(Collections.emptyList())
                .stream().map(carRentalOrder ->{
                    Orders orders = carRentalOrder.getOrder();
                    try {
                        return convertToResVehicleRentalOrderDetailDTO(orders);
                    } catch (ApplicationException e) {
                        return null;
                    }
                }).collect(Collectors.toList());
    }

    @Override
    public List<ResVehicleRentalOrderDetailDTO> getAllOrder(){
        try{
            BusinessPartner currenBP = businessPartnerService.getCurrentBusinessPartner(CAR_RENTAL_PARTNER);
            List<CarRentalOrders> orders = vehicleRentalOrderRepo.findFutureOrdersByCarRentalPartner(Instant.now(),currenBP.getCarRentalPartner().getId());
            return Optional.ofNullable(orders)
                    .orElse(Collections.emptyList())
                    .stream().map(order->{
                        try {
                            ResVehicleRentalOrderDetailDTO resVehicleRentalOrderDetailDTO = convertToResVehicleRentalOrderDetailDTO(order.getOrder());
                            return resVehicleRentalOrderDetailDTO;
                        } catch (ApplicationException e) {
                            System.out.println(e.getMessage());
                            return null;
                        }
                    }).collect(Collectors.toList());
        }catch (Exception e){
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }
    @Override
    public List<ResVehicleRentalOrderDetailDTO> getAllOrderUser(){
        try{
            String email = SecurityUtil.getCurrentLogin().isPresent() ? SecurityUtil.getCurrentLogin().get() : null;
            if(email==null){
                throw new ApplicationException("Email is invalid");
            }
            Account currentAccount = accountService.handleGetAccountByUsername(email);
            List<CarRentalOrders> orders = vehicleRentalOrderRepo.findCarRentalOrdersByAccountId(currentAccount.getId());
            return Optional.ofNullable(orders)
                    .orElse(Collections.emptyList())
                    .stream().map(order->{
                        try {
                            ResVehicleRentalOrderDetailDTO resVehicleRentalOrderDetailDTO = convertToResVehicleRentalOrderDetailDTO(order.getOrder());
                            return resVehicleRentalOrderDetailDTO;
                        } catch (ApplicationException e) {
                            System.out.println(e.getMessage());
                            return null;
                        }
                    }).collect(Collectors.toList());
        }catch (Exception e){
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }
    @Override
    public List<ResVehicleRentalOrderDetailDTO> getOrderByStatus(String status) {
        if (!status.equalsIgnoreCase("returned") && !status.equalsIgnoreCase("not_returned")) {
            throw new IllegalArgumentException("Invalid status. Use 'returned' or 'not_returned'.");
        }
        List<CarRentalOrders> carRentalOrders = vehicleRentalOrderRepo.findCarRentalOrdersByStatus(status.toLowerCase());

        return Optional.ofNullable(carRentalOrders).orElse(Collections.emptyList())
                .stream().map(order->{
                    try{
                        ResVehicleRentalOrderDetailDTO o = convertToResVehicleRentalOrderDetailDTO(order.getOrder());
                        return o;
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                        return null;
                    }
                }).collect(Collectors.toList());
    }

    @Override
    public boolean cancelVehicleRentalService(String vehicleRentalOrderId) throws ApplicationException {
        //Get current account
        String email = SecurityUtil.getCurrentLogin().isPresent()?SecurityUtil.getCurrentLogin().get():null;
        if(email==null) {
            throw new ApplicationException("Access token not found or expired");
        }
        Account currentAccount = this.accountService.handleGetAccountByUsername(email);
        //Get Vehicle rental service
        Optional<CarRentalOrders> carRentalOrders = vehicleRentalOrderRepo.findById(vehicleRentalOrderId);

        if(carRentalOrders.isPresent()){
            CarRentalService carRentalService = carRentalOrders.get().getCarRentalService();
            carRentalOrders.get().setStatus("canceled");
            //update amount vehicle register
            List<String> timeSlots = dateUtil.generateTimeSlots(carRentalOrders.get().getStart_rental_time(),carRentalOrders.get().getEnd_rental_time());
            for (String timeSlot : timeSlots) {
                String key = "vehicle-rental:" + carRentalService.getId();
                int rentalAmount = carRentalOrders.get().getAmount();
                int vehicleRegisterAmount = carRentalService.getVehicleRegister().getAmount();
                // Lấy số lượng hiện tại từ Redis
                Integer currentAmount = redisService2.getHashValue(key, timeSlot);
                if (currentAmount != null) {
                    // Trường hợp có dữ liệu: kiểm tra số lượng và tăng lại
                    long updatedAmount = redisService2.incrementHashValue(key, timeSlot, rentalAmount);

                    // Nếu tăng vượt quá số xe gốc
                    if (updatedAmount > vehicleRegisterAmount ) {
                        // Giảm lại vì tăng đã cộng  nhầm
                        redisService2.incrementHashValue(key, timeSlot, -rentalAmount);
                    }
                } else {
                    // Trường hợp hết xe
                    throw new ApplicationException("Khung giờ không khả dụng "+ timeSlot );
                }
            }

            //notify to user
            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setMessage(" Bạn đã huỷ đơn thuê xe thành công ");
            notificationDTO.setTitle("Hủy đơn thành công");
            notificationDTO.setType(NotificationTypeEnum.BOOKING_COMPLETED);
            notificationDTO.setCreate_at(Instant.now());
            notificationDTO.setSeen(false);
            notificationService.createNotificationToAccount(currentAccount.getId(),  AccountEnum.USER,notificationDTO);
            //notify to Partner
            int partnerId = carRentalOrders.get().getCarRentalService().getVehicleRegister().getCarRentalPartner()
                            .getBusinessPartner().getAccount().getId();
            NotificationDTO notificationDTO1 = new NotificationDTO();
            notificationDTO1.setMessage(currentAccount.getName()+" đã huỷ xe ");
            notificationDTO1.setTitle("Hủy đơn thuê xe");
            notificationDTO1.setType(NotificationTypeEnum.BOOKING_COMPLETED);
            notificationDTO1.setCreate_at(Instant.now());
            notificationDTO1.setSeen(false);
            notificationService.createNotificationToAccount(partnerId,  AccountEnum.CAR_RENTAL_PARTNER,notificationDTO1);

            return true;
        }else{
            throw new ApplicationException("Đơn thuê xe không tồn tại");
        }
    }

    //tính giá tiền khi startTime và endTime trong cùng 1 ngày
    public double calculatePriceInDay(LocalDateTime start,LocalDateTime end,double priceOneDay){
        double total = 0.0;
        if (start.getHour() < 12) {
            // Nếu bắt đầu trước 12 giờ
            if (end.getHour() <= 12) {
                total += (Duration.between(start, end).toHours() * priceOneDay) / 24; // Thue theo tieng
            } else if(end.getHour() < 18){
                total += priceOneDay / 2 + (Duration.between(LocalDateTime.of(end.toLocalDate(), java.time.LocalTime.NOON), end).toHours() * priceOneDay) / 24; // Thuê nửa ngày + số tiếng từ 12 giờ -> endDate
            }else{
                total += priceOneDay;
            }
        } else if (start.getHour() < 18) {
            // Từ 12 giờ đến 18 giờ
            if (end.getHour() <= 18) {
                total += (Duration.between(start, end).toHours() * priceOneDay) / 24; // Thue theo tieng
            } else if(end.getHour() < 22){
                total += priceOneDay / 2 + (Duration.between(LocalDateTime.of(end.toLocalDate(), LocalTime.of(18, 0)), end).toHours()
                        * priceOneDay) / 24; // Thuê nửa ngày + số tiếng từ 12 giờ -> endDate
            } else{
                //Sau 18 giờ
                total += priceOneDay;
            }
        } else {
            // Sau 18 giờ
            total += (Duration.between(start, end).toHours() * priceOneDay) / 24; // Tính theo giờ còn lại sau 18 giờ
        }
        return total;
    }
}
