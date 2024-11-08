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
    private int amount;


    @OneToMany(mappedBy = "vehicleRegister", cascade = CascadeType.ALL)
    @JsonIgnore
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