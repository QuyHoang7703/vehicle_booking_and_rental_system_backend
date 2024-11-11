package com.pbl6.VehicleBookingRental.user.dto.request.bus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.DropOffLocation;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.PickupLocation;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
public class ReqBusTripDTO {
    private int id;
    private String departureLocation;
    private String arrivalLocation;
    private String durationJourney;
    private List<PickupLocation> pickupLocations;
    private List<DropOffLocation> dropOffLocations;
}
