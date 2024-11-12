package com.pbl6.VehicleBookingRental.user.dto.response.bus;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusType;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.Utility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResBusDetailDTO {
    private String licensePlate;
    private List<Utility> utilities;
    private BusType busType;
    private List<String> imagesBus;
}
