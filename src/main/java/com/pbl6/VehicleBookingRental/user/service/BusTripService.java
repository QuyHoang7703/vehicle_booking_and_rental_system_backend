package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBusTripDTO;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;

public interface BusTripService {
    public void createBusTrip(ReqBusTripDTO reqBusTripDTO) throws IdInvalidException;

}
