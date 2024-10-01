package com.pbl6.VehicleBookingRental.user.domain.car_rental;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="car_rental_service")
public class CarRentalService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private double price;
    private int type;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "service_id")
    private VehicleRegister vehicleRegister;

    @OneToMany(mappedBy = "carRentalService",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<CarRentalOrders> carRentalOrdersList;
}
