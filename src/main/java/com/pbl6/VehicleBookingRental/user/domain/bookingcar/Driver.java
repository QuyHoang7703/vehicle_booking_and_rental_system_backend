package com.pbl6.VehicleBookingRental.user.domain.bookingcar;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pbl6.VehicleBookingRental.user.domain.VehicleType;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "driver")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String driver_license;
    private int images_id;
    private String citizen_id;
    private String status;
    private String location;
    private String license_plate;
    private String car_insurance;
    private double rating_total;

    @OneToMany(mappedBy = "driver",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Booking> bookings;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "vehicle_type_id")
    private VehicleType vehicleType;

}
