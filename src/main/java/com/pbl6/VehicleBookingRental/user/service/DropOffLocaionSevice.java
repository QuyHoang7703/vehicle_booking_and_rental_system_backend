package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.DropOffLocation;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;

public interface DropOffLocaionSevice {
    DropOffLocation createDropOffLocation(DropOffLocation dropOffLocation);
    DropOffLocation updateDropOffLocation(int dropOffLocationId) throws IdInvalidException;
    void deleteDropOffLocation(int dropOffLocationId) throws IdInvalidException;
}