package com.pbl6.VehicleBookingRental.user.dto.response.bus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResPickupAndDropOffLocation {
    private List<String> pickupLocations;
    private List<String> dropOffLocations;
}
