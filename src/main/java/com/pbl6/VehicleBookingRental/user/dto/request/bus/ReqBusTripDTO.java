package com.pbl6.VehicleBookingRental.user.dto.request.bus;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.Duration;
import java.util.List;
@Data

public class ReqBusTripDTO {
    private int id;
    private String departureLocation;
    private String arrivalLocation;
    private Duration durationJourney;
    private List<String> pickupLocations;
    private List<String> dropOffLocations;
}
