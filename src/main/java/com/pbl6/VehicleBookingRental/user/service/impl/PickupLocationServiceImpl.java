package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.PickupLocation;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.PickupLocationRepository;
import com.pbl6.VehicleBookingRental.user.service.PickupLocationService;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PickupLocationServiceImpl implements PickupLocationService {
    private final PickupLocationRepository pickupLocationRepository;

    @Override
    public PickupLocation createPickupLocation(PickupLocation pickupLocation) {
        return this.pickupLocationRepository.save(pickupLocation);
    }

    @Override
    public PickupLocation updatePickupLocation(PickupLocation pickupLocation) throws IdInvalidException {
        PickupLocation pickupLocationDb = this.pickupLocationRepository.findById(pickupLocation.getId())
                .orElseThrow(() -> new IdInvalidException("Pickup location not found"));
        pickupLocationDb.setName(pickupLocation.getName());
        return this.pickupLocationRepository.save(pickupLocationDb);
    }

    @Override
    public void deletePickupLocation(int pickupLocationId) throws IdInvalidException {
        PickupLocation pickupLocation = this.pickupLocationRepository.findById(pickupLocationId)
                .orElseThrow(() -> new IdInvalidException("Pickup location not found"));
        this.pickupLocationRepository.delete(pickupLocation);
    }
}
