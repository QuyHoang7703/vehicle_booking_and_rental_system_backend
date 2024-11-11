package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.*;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBusTripDTO;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.*;
import com.pbl6.VehicleBookingRental.user.service.BusTripService;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusTripServiceImpl implements BusTripService {
    private final BusTripRepository busTripRepository;
    private final PickupLocationRepository pickupLocationRepository;
    private final DropOffLocationRepository dropOffLocationRepository;
    @Override
    public BusTrip createBusTrip(ReqBusTripDTO reqBusTripDTO) throws IdInvalidException {
        BusTrip busTrip = new BusTrip();
        busTrip.setDepartureLocation(reqBusTripDTO.getDepartureLocation());
        busTrip.setArrivalLocation(reqBusTripDTO.getArrivalLocation());
        busTrip.setDurationJourney(reqBusTripDTO.getDurationJourney());
        busTrip.setPickupLocations(this.getPickupLocations(reqBusTripDTO));
        busTrip.setDropOffLocations(this.getDropOffLocations(reqBusTripDTO));

        return busTripRepository.save(busTrip);
    }

    @Override
    public BusTrip updateBusTrip(ReqBusTripDTO reqBusTripDTO) throws IdInvalidException {
        BusTrip busTripDb = this.findBusTripById(reqBusTripDTO.getId());
        busTripDb.setDepartureLocation(reqBusTripDTO.getDepartureLocation());
        busTripDb.setArrivalLocation(reqBusTripDTO.getArrivalLocation());
        busTripDb.setDurationJourney(reqBusTripDTO.getDurationJourney());
        busTripDb.setPickupLocations(this.getPickupLocations(reqBusTripDTO));
        busTripDb.setDropOffLocations(this.getDropOffLocations(reqBusTripDTO));

        return this.busTripRepository.save(busTripDb);
    }

    @Override
    public BusTrip findBusTripById(int id) throws IdInvalidException {
        return this.busTripRepository.findById(id).orElseThrow(()-> new IdInvalidException("BusTrip not found"));
    }

    private List<PickupLocation> getPickupLocations(ReqBusTripDTO reqBusTripDTO) {
        List<Integer> idOfPickupLocations = reqBusTripDTO.getPickupLocations().stream()
                .map(PickupLocation::getId).toList();
        return this.pickupLocationRepository.findByIdIn(idOfPickupLocations);
    }

    private List<DropOffLocation> getDropOffLocations(ReqBusTripDTO reqBusTripDTO) {
        List<Integer> idOfDropOffLocations = reqBusTripDTO.getDropOffLocations().stream()
                .map(DropOffLocation::getId).toList();
        return this.dropOffLocationRepository.findByIdIn(idOfDropOffLocations);
    }


}
