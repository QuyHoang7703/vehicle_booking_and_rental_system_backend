package com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class VehicleRentalServiceDTO {
    private int vehicle_rental_service_id;
    private int vehicle_register_id;
    private double driverPrice;
    private double selfDriverPrice;
    private int type;
    private String location;
    private String manufacturer;
    private String vehicleLife;
    private String vehicle_type;
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
    private int amount;
    private List<String> imagesVehicleRegister;
    private String partnerName;
    private String partnerPhoneNumber;
    private int partnerId;
    private int partnerAccountId;
    private int vehicle_type_id;
}
