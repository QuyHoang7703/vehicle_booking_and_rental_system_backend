package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.dto.LocationDTO;
import com.pbl6.VehicleBookingRental.user.dto.OpenRouteServiceDTO;

import java.io.IOException;
import java.time.Duration;

public interface OSRService {
    public OpenRouteServiceDTO getDistanceAndDuration(LocationDTO source, LocationDTO destination) throws IOException, InterruptedException;
}
