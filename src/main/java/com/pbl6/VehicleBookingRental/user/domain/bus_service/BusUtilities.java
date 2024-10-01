package com.pbl6.VehicleBookingRental.user.domain.bus_service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "bus_utilities")
public class BusUtilities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "bus_id")
    private Bus bus;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "utilities_id")
    private Utilities utilities;
}
