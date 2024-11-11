package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.DropOffLocation;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;

import java.util.List;

public interface DropOffLocaionSevice {
    DropOffLocation createDropOffLocation(DropOffLocation dropOffLocation);
    DropOffLocation updateDropOffLocation(DropOffLocation dropOffLocation) throws IdInvalidException;
    void deleteDropOffLocation(int dropOffLocationId) throws IdInvalidException;
    List<String> getDropOffLocationsByProvinceName(String provinceName);
}
