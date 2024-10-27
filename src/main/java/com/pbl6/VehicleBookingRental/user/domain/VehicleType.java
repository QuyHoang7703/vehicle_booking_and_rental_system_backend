package com.pbl6.VehicleBookingRental.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pbl6.VehicleBookingRental.user.domain.bookingcar.Driver;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.VehicleRegister;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "VehicleType")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VehicleType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    private double price;
    @OneToMany(mappedBy = "vehicleType",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<VehicleRegister> vehicleRegisters;

    @OneToMany(mappedBy = "vehicleType", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Driver> drivers;
}
