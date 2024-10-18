package com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO;

import lombok.Data;

import java.util.Date;

@Data
public class VehicleRentalServiceDTO {
    private int id;
    private double price;
    private int type;
    private String location;
    private String manufacturer;
    private String description;
    private int quantity;
    private String status;
    private Date date_of_status;
    private double discount_percentage;
    private double car_deposit;
    private double reservation_fees;
    private String ulties;
    private String policy;
    private double rating_total;
    private double amount;
    private int vehicle_register_id;
    private int vehicle_type_id;
}
