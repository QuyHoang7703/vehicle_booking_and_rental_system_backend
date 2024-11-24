package com.pbl6.VehicleBookingRental.user.dto.response.bus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResBusDTO {
    private int busId;
    private String licensePlate;
    private String nameBusType;
    private String imageRepresentative;
}
