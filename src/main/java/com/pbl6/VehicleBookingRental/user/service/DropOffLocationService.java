package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.dto.LocationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqDropOffLocationDTO;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;

public interface DropOffLocationService {
    void createDropOffLocation(ReqDropOffLocationDTO reqDropOffLocationDTO) throws IdInvalidException;
}
