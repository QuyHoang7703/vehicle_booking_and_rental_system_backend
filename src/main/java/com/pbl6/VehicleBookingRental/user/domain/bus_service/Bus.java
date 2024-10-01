package com.pbl6.VehicleBookingRental.user.domain.bus_service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "bus")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Bus {

    @Id
    private int id;
    private String licensePlate;


    @ManyToOne
    @JoinColumn(name = "bus_type_id")
    private BusType busType;


    @ManyToOne
    @JoinColumn(name = "bus_partner_id")
    private BusPartner busPartner;

    @OneToMany(mappedBy = "bus",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<BusUtilities> busUtilities;
}