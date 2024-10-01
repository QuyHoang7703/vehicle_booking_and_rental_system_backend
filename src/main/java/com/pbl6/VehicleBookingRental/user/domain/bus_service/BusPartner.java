package com.pbl6.VehicleBookingRental.user.domain.bus_service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.Images;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bus_partner")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BusPartner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id ;
    private String description ;
    private String url;
    private String policy;
    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "business_partner_id")
    private BusinessPartner businessPartner;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "image_id")
    private Images image;

}
