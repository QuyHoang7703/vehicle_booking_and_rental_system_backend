package com.pbl6.VehicleBookingRental.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
    private long id;
    private String name;
    private int numberOfSeat;
    private String chairType;

    @OneToMany(mappedBy = "busType", fetch = FetchType.LAZY)
    private List<Bus> buses;
}
