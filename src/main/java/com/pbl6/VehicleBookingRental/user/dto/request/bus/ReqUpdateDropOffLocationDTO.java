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
public class ReqUpdateDropOffLocationDTO {
    private int id;
    private String province;
    private Double priceTicket;
    private Duration journeyDuration;
    private List<String> dropOffLocations;
}
