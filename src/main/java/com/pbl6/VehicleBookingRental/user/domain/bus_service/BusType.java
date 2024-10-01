package com.pbl6.VehicleBookingRental.user.domain.bus_service;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bus_type")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BusType {

    @Id
    private int id;
    private String name;
    private int numberOfSeat;
    private String chairType;
}
