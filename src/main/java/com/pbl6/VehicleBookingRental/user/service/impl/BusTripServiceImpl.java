package com.pbl6.VehicleBookingRental.user.service.impl;

import com.amazonaws.services.dynamodbv2.xspec.S;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.*;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBusTripDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripDTO;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.*;
import com.pbl6.VehicleBookingRental.user.service.BusTripService;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BusTripServiceImpl implements BusTripService {
    private final BusTripRepository busTripRepository;
    @Override
    public BusTrip createBusTrip(ReqBusTripDTO reqBusTripDTO) throws IdInvalidException {
        BusTrip busTrip = new BusTrip();
        busTrip.setDepartureLocation(reqBusTripDTO.getDepartureLocation());
        busTrip.setArrivalLocation(reqBusTripDTO.getArrivalLocation());
        busTrip.setDurationJourney(reqBusTripDTO.getDurationJourney());
        List<String> pickupLocations = reqBusTripDTO.getPickupLocations();
        busTrip.setPickupLocations(String.join("!", pickupLocations));
        List<String> dropOffLocations = reqBusTripDTO.getDropOffLocations();
        busTrip.setDropOffLocations(String.join("!", dropOffLocations));
        return busTripRepository.save(busTrip);
    }

    @Override
    public BusTrip updateBusTrip(ReqBusTripDTO reqBusTripDTO) throws IdInvalidException {
        BusTrip busTripDb = this.busTripRepository.findById(reqBusTripDTO.getId())
                .orElseThrow(()-> new IdInvalidException("BusTrip not found"));
        busTripDb.setDepartureLocation(reqBusTripDTO.getDepartureLocation());
        busTripDb.setArrivalLocation(reqBusTripDTO.getArrivalLocation());
        busTripDb.setDurationJourney(reqBusTripDTO.getDurationJourney());
        List<String> pickupLocations = reqBusTripDTO.getPickupLocations();
        busTripDb.setPickupLocations(String.join("!", pickupLocations));
        List<String> dropOffLocations = reqBusTripDTO.getDropOffLocations();
        busTripDb.setDropOffLocations(String.join("!", dropOffLocations));
        return this.busTripRepository.save(busTripDb);
    }

    @Override
    public ResBusTripDTO findBusTripById(int id) throws IdInvalidException {
        BusTrip busTripDb = this.busTripRepository.findById(id)
                .orElseThrow(()-> new IdInvalidException("BusTrip not found"));
        return this.convertToBusTripDTO(busTripDb);
    }

    @Override
    public ResBusTripDTO convertToBusTripDTO(BusTrip busTrip) {
        String pickupLocations = busTrip.getPickupLocations();
        List<String> pickupLocationsToList = Arrays.asList(pickupLocations.split("!"));

        String dropOffLocations = busTrip.getDropOffLocations();
        List<String> dropOffLocationsToList =  Arrays.asList(dropOffLocations.split("!"));

        ResBusTripDTO.BusTripInfo busTripInfo = ResBusTripDTO.BusTripInfo.builder()
                .id(busTrip.getId())
                .departureLocation(busTrip.getDepartureLocation())
                .arrivalLocation(busTrip.getArrivalLocation())
                .durationJourney(busTrip.getDurationJourney())
                .build();

        ResBusTripDTO resBusTripDTO = ResBusTripDTO.builder()
                .busTripInfo(busTripInfo)
                .pickupLocations(pickupLocationsToList)
                .dropOffLocations(dropOffLocationsToList)
                .build();
        return resBusTripDTO;
    }


}
