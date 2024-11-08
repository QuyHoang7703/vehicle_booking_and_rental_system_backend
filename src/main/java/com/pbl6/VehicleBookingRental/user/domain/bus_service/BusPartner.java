package com.pbl6.VehicleBookingRental.user.domain.bus_service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.Images;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "bus_partner")
@AllArgsConstructor
@NoArgsConstructor
//@Data
@Getter
@Setter
public class BusPartner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id ;
    private String description ;
    private String urlFanpage;
//    private String policy;
    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "business_partner_id")
    private BusinessPartner businessPartner;

    @OneToMany(mappedBy = "busPartner")
    @JsonIgnore
    private List<Bus> buses;

    @OneToMany(mappedBy = "busPartner")
    @JsonIgnore
    private List<BusType> busTypes;



}
