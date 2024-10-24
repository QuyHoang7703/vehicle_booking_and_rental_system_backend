package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalOrders;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalService;
import com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO.VehicleRentalOrdersDTO;
import com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO.VehicleRentalServiceDTO;
import com.pbl6.VehicleBookingRental.user.interfaces.VehicleRentalOrdersInterface;
import com.pbl6.VehicleBookingRental.user.repository.OrdersRepo;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRepository;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleRentalOrderRepo;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleRentalServiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Date;
import java.util.Optional;

@Service
public class VehicleRentalOrderService implements VehicleRentalOrdersInterface {

    @Autowired
    private VehicleRentalOrderRepo vehicleRentalOrderRepo;
    @Autowired
    private OrdersRepo ordersRepo;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private VehicleRentalServiceRepo vehicleRentalServiceRepo;
    @Override
    public boolean save_order(VehicleRentalOrdersDTO vehicleRentalOrdersDTO) {
        // Create New Order
        Orders order_vehicle_rental = new Orders();
        order_vehicle_rental.setOrder_type("VehicleRentalOrders");
        order_vehicle_rental.setCreate_at(new Date());

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
                return true;
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
        }
        return false;
    }

}
