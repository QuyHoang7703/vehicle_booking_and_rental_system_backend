package com.pbl6.VehicleBookingRental.user.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusTripScheduleRedis {
    private int busTripScheduleId;
    private int availableSeats;
}
