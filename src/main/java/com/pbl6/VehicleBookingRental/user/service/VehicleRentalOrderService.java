package com.pbl6.VehicleBookingRental.user.service;


import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalOrders;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalService;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.VehicleRegister;
import com.pbl6.VehicleBookingRental.user.domain.notification.Notification;
import com.pbl6.VehicleBookingRental.user.domain.notification.NotificationAccount;
import com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO.VehicleRentalOrdersDTO;

import com.pbl6.VehicleBookingRental.user.dto.chat_dto.NotificationDTO;
import com.pbl6.VehicleBookingRental.user.interfaces.VehicleRentalOrdersInterface;
import com.pbl6.VehicleBookingRental.user.repository.OrdersRepo;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRepository;
import com.pbl6.VehicleBookingRental.user.repository.chat.NotificationAccountRepo;
import com.pbl6.VehicleBookingRental.user.repository.chat.NotificationRepo;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleRentalServiceRepo;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static com.pbl6.VehicleBookingRental.user.util.constant.NotificationTypeEnum.NEW_BOOKING;
import static com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum.BUS_PARTNER;
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
    @Override
    public boolean save_order(VehicleRentalOrdersDTO vehicleRentalOrdersDTO) {
        // Create New Order
        Orders order_vehicle_rental = new Orders();
        order_vehicle_rental.setOrder_type("VehicleRentalOrders");
        order_vehicle_rental.setCreate_at(Instant.now());

        // Create VehicleRentalOrders
        CarRentalOrders carRentalOrders = new CarRentalOrders();
        carRentalOrders.setStart_rental_time(vehicleRentalOrdersDTO.getStart_rental_time());
        carRentalOrders.setEnd_rental_time(vehicleRentalOrdersDTO.getEnd_rental_time());
        carRentalOrders.setPickup_location(vehicleRentalOrdersDTO.getPickup_location());
        carRentalOrders.setCreated_at(order_vehicle_rental.getCreate_at());
        carRentalOrders.setStatus(vehicleRentalOrdersDTO.getStatus());
        carRentalOrders.setVoucher_value(vehicleRentalOrdersDTO.getVoucher_value());
        carRentalOrders.setVoucher_percentage(vehicleRentalOrdersDTO.getVoucher_percentage());
        carRentalOrders.setAmount(vehicleRentalOrdersDTO.getAmount());
        carRentalOrders.setCar_deposit(vehicleRentalOrdersDTO.getCar_deposit());
        carRentalOrders.setReservation_fee(vehicleRentalOrdersDTO.getReservation_fee());
        carRentalOrders.setPrice(vehicleRentalOrdersDTO.getPrice());

        // Set Account and VehicleRentalService for VehicleRentalOrder
        Optional<Account> account = accountRepository.findById(vehicleRentalOrdersDTO.getAccount_id());
        Optional<CarRentalService> carRentalService = vehicleRentalServiceRepo.findById(vehicleRentalOrdersDTO.getVehicle_rental_service_id());

        if (account.isPresent() && carRentalService.isPresent()) {
            carRentalOrders.setCarRentalService(carRentalService.get());
            carRentalOrders.setAccount(account.get());

            // Connect Order to VehicleRentalOrder
            carRentalOrders.setOrder(order_vehicle_rental);  // Thiết lập quan hệ hai chiều
            order_vehicle_rental.setCarRentalOrders(carRentalOrders);  // Thiết lập quan hệ hai chiều

            try {
                ordersRepo.save(order_vehicle_rental);  // Lưu cả hai đối tượng nhờ Cascade
                //create new Notification
                int carRentalPartnerId = order_vehicle_rental.getCarRentalOrders().getCarRentalService().
                        getVehicleRegister().getCarRentalPartner().getId();
                int accountIdOfPartner = order_vehicle_rental.getCarRentalOrders().getCarRentalService().
                        getVehicleRegister().getCarRentalPartner().getBusinessPartner().getAccount().getId();

                Notification notification = new Notification();
                notification.setCreate_at(new Date());
                notification.setMessage("Bạn có một đơn đặt xe mới ");
                notification.setTitle("Đơn thuê xe mới");
                notification.setType(NEW_BOOKING);
                notificationRepo.save(notification);

                NotificationAccount notificationAccount = new NotificationAccount();
                notificationAccount.setNotification(notification);
                Optional<Account> partnerAccount = accountRepository.findById(accountIdOfPartner);
                notificationAccount.setAccount(partnerAccount.get());
                notificationAccount.setPartnerType(CAR_RENTAL_PARTNER);
                notificationAccount.setSeen(false);
                notificationAccountRepo.save(notificationAccount);

                notificationService.sendNotification
                        (carRentalPartnerId
                        ,"CAR_RENTAL_PARTNER"
                        , NotificationDTO.builder()
                                        .id(notification.getId())
                                        .type(notification.getType())
                                        .title(notification.getTitle())
                                        .message(notification.getMessage())
                                        .create_at(notification.getCreate_at())
                                        .isSeen(notificationAccount.isSeen())
                                        .build());
                return true;
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
        }
        return false;
    }

    @Override
    public boolean update_amount(int vehicle_rental_service_id, int amount) {
        Optional<CarRentalService> carRentalService = vehicleRentalServiceRepo.findById(vehicle_rental_service_id);
        if (carRentalService.isPresent()) {
            VehicleRegister vehicleRegister = carRentalService.get().getVehicleRegister();
            vehicleRegister.setAmount(vehicleRegister.getAmount() - amount);
            carRentalService.get().setVehicleRegister(vehicleRegister);
            vehicleRentalServiceRepo.save(carRentalService.get());
            return true;
        }
        return false;
    }
}
