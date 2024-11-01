package com.pbl6.VehicleBookingRental.user.domain.bus_service;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name="utilities")
@Getter
@Setter
public class Utility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String description;
    private String image;

    @ManyToMany(mappedBy = "utilities", fetch = FetchType.LAZY)
    private List<Bus> buses;

}
