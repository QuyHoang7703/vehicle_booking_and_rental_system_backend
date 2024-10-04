package com.pbl6.VehicleBookingRental.user.domain.car_rental;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pbl6.VehicleBookingRental.user.domain.VehicleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="vehicle_register")
public class VehicleRegister {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
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


    @OneToMany(mappedBy = "vehicleRegister", cascade = CascadeType.ALL)
    private List<CarRentalService> typeOfRentalServiceList;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "vehicle_type_id")
    private VehicleType vehicleType;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "car_rental_partner_id")
    private CarRentalPartner carRentalPartner;


}