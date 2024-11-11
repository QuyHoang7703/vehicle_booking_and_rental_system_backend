package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTrip;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBusTripDTO;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;

public interface BusTripService {
    public BusTrip createBusTrip(ReqBusTripDTO reqBusTripDTO) throws IdInvalidException;
    public BusTrip updateBusTrip(ReqBusTripDTO reqBusTripDTO) throws IdInvalidException;
    public BusTrip findBusTripById(int id) throws IdInvalidException;
}
