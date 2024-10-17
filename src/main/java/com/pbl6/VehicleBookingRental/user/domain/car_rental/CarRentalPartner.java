package com.pbl6.VehicleBookingRental.user.domain.car_rental;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="car_rental_partner")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarRentalPartner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(mappedBy = "carRentalPartner",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<VehicleRegister> vehicleRegisterList;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "business_partner_id")
    private BusinessPartner businessPartner;
}
