package com.pbl6.VehicleBookingRental.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "images")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Images {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String pathImage;
//    private String typeImage;
    private int ownerId;
    private String ownerType;
//    @ManyToOne(@JoinColumn="owner_id")
//    private Object owner;
}
