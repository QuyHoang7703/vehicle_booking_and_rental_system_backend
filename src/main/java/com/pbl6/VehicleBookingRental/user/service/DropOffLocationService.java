package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.DropOffLocation;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqDropOffLocationDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResDropOffLocationDTO;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;

public interface DropOffLocationService {
    void createDropOffLocation(ReqDropOffLocationDTO reqDropOffLocationDTO) throws IdInvalidException, ApplicationException;
    ResDropOffLocationDTO convertToResDropOffLocationDTO(DropOffLocation dropOffLocation);

}
