package com.pbl6.VehicleBookingRental.user.dto.request.bus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqDropOffLocationDTO {
    private int busTripId;
    private String province;
    private List<String> dropOffLocations;
    private double priceTicket;
    private Duration journeyDuration;
}
