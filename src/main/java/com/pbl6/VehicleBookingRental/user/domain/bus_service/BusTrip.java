package com.pbl6.VehicleBookingRental.user.domain.bus_service;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Entity
@Table(name = "bus_trip")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BusTrip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id ;
    private String  departure_location ;
    @Temporal(TemporalType.TIMESTAMP)
    private Date departure_time ;
    private String destination ;
    private double duration_journey ;
    private double price_ticket;
    private String status ;
    @Temporal(TemporalType.TIMESTAMP)
    private Date update_status_at ;
    private double discount_percentage;
    private int available_seat ;
    private double rating_total;

}
