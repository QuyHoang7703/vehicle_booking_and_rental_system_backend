package com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO;


import lombok.Data;

import java.util.Date;
@Data
public class VehicleRentalOrdersDTO {
    private int id;
    private Date created_at;
    private Date start_rental_time;
    private Date end_rental_time;
    private String pickup_location;
    private double total;
    private String status;
    private double voucher_value;
    private double voucher_percentage;
    private int amount;
    private double car_deposit;
    private double reservation_fee;
    private double price;

    private int vehicle_rental_service_id;
    private int account_id;
}
