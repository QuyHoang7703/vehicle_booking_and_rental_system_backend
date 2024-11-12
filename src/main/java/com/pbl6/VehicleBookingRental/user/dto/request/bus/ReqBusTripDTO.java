package com.pbl6.VehicleBookingRental.user.dto.request.bus;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
@Data

public class ReqBusTripDTO {
    private int id;
    private String departureLocation;
    private String arrivalLocation;
    private String durationJourney;
    private List<String> pickupLocations;
    private List<String> dropOffLocations;
}
