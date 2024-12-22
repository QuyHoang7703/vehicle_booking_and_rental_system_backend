package com.pbl6.VehicleBookingRental.user.dto.request.bus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqUpdateDropOffLocationDTO {
    private int id;
    private List<String> dropOffLocations;
}
