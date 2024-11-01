package com.pbl6.VehicleBookingRental.user.domain.bus_service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name="bus_types")
@Getter
@Setter
public class BusType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private int numberOfSeat;
    private String chairType;

    @OneToMany(mappedBy = "busType", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Bus> buses;

    @ManyToOne
    @JoinColumn(name="busPartnerId")
    BusPartner busPartner;



}
