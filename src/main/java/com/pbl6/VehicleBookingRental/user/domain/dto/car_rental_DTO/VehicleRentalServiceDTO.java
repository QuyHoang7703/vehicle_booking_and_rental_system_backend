package com.pbl6.VehicleBookingRental.user.domain.dto.car_rental_DTO;

import lombok.Data;

import java.util.Date;

@Data
public class VehicleRentalServiceDTO {
    private int id;
    private double price;
    private int type;
    private String manufacturer;
    private String description;
    private int quantity;
    private String status;
    private Date dateOfStatus;
    private double discountPercentage;
    private double carDeposit;
    private double reservationFees;
    private String ulties;
    private String policy;
    private double ratingTotal;
    private double amount;
}
