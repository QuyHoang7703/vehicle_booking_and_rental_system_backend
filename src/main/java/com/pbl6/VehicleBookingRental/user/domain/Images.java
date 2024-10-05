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
    private String path_image;
    private String type_image;
    private int owner_id;
}
