package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.DropOffLocation;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.PickupLocation;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.DropOffLocationRepository;
import com.pbl6.VehicleBookingRental.user.service.DropOffLocaionSevice;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DropOffLocationServiceImpl implements DropOffLocaionSevice {
    private final DropOffLocationRepository dropOffLocationRepository;
    @Override
    public DropOffLocation createDropOffLocation(DropOffLocation dropOffLocation) {
        return this.dropOffLocationRepository.save(dropOffLocation);
    }

    @Override
    public DropOffLocation updateDropOffLocation(DropOffLocation dropOffLocation) throws IdInvalidException {
        DropOffLocation dropOffLocationDb = this.dropOffLocationRepository.findById(dropOffLocation.getId())
                .orElseThrow(() -> new IdInvalidException("Drop off location not found"));
        dropOffLocationDb.setName(dropOffLocation.getName());
        return this.dropOffLocationRepository.save(dropOffLocationDb);

    }

    @Override
    public void deleteDropOffLocation(int dropOffLocationId) throws IdInvalidException {
        DropOffLocation pickupLocation = this.dropOffLocationRepository.findById(dropOffLocationId)
                .orElseThrow(() -> new IdInvalidException("Drop off location not found"));
        this.dropOffLocationRepository.delete(pickupLocation);
    }
}
