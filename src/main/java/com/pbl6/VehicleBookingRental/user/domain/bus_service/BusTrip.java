package com.pbl6.VehicleBookingRental.user.domain.bus_service;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "bus_trip")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BusTrip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id ;
    private String  departureLocation ;
    private String arrivalLocation ;
    private String durationJourney ;
    private Instant createAt;
    private Instant updateAt ;

    @ManyToOne
    @JoinColumn(name = "bus_partner_id")
    private BusPartner busPartner;

    @OneToMany(mappedBy = "busTrip")
    private List<PickupLocation> pickupLocationList;

    @OneToMany(mappedBy = "busTrip")
    private List<DropOffLocation> dropOffLocationList;

    @OneToMany(mappedBy = "busTrip")
    private List<BusTripSchedule> busTripScheduleList;

    @PrePersist
    public void prePersist(){
        this.createAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate(){
        this.updateAt = Instant.now();
    }

}
