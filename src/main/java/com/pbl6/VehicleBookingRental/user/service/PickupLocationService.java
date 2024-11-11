package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.PickupLocation;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;

import java.util.List;

public interface PickupLocationService {
    PickupLocation createPickupLocation(PickupLocation pickupLocation);
    PickupLocation updatePickupLocation(PickupLocation pickupLocation) throws IdInvalidException;
    void deletePickupLocation(int pickupLocationId) throws IdInvalidException;
    List<String> getPickupLocationByProvinceName(String provinceName);


}
