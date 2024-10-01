package com.pbl6.VehicleBookingRental.user.domain.bus_service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "utilities")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Utilities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String image;
    private String description;

    @OneToMany(mappedBy = "utilities",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<BusUtilities> busUtilities;
}
