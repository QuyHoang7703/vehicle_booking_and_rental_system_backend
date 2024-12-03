package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTrip;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.DropOffLocation;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqDropOffLocationDTO;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusTripRepository;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.DropOffLocationRepository;
import com.pbl6.VehicleBookingRental.user.service.DropOffLocationService;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DropOffLocationServiceImpl implements DropOffLocationService {
    private final DropOffLocationRepository dropOffLocationRepository;
    private final BusTripRepository busTripRepository;
    @Override
    public void createDropOffLocation(ReqDropOffLocationDTO reqDropOffLocationDTO) throws IdInvalidException {
        BusTrip busTrip = busTripRepository.findById(reqDropOffLocationDTO.getBusTripId()).
                orElseThrow(() -> new IdInvalidException("Bus trip not found"));

        DropOffLocation dropOffLocation = new DropOffLocation();
        dropOffLocation.setProvince(reqDropOffLocationDTO.getProvince());
        dropOffLocation.setPriceTicket(reqDropOffLocationDTO.getPriceTicket());
        dropOffLocation.setDropOffLocation(String.join("!", reqDropOffLocationDTO.getDropOffLocations()));
        dropOffLocation.setJourneyDuration(reqDropOffLocationDTO.getJourneyDuration());
        dropOffLocation.setBusTrip(busTrip);

        this.dropOffLocationRepository.save(dropOffLocation);
    }
}
