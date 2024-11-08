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
    private final BusTypeRepository busTypeRepository;
    private final BusRepository busRepository;
    private final BreakDayRepository breakDayRepository;
    private final DepartTimeBusTripRepository departTimeBusTripRepository;
    @Override
    public void createBusTrip(ReqBusTripDTO reqBusTripDTO) throws IdInvalidException {
        BusTrip busTrip = new BusTrip();
        // Create a bus trip
        busTrip.setDepartureLocation(reqBusTripDTO.getDepartureLocation());
        busTrip.setArrivalLocation(reqBusTripDTO.getArrivalLocation());
        busTrip.setStartOperationDay(reqBusTripDTO.getStartOperationDay());
        busTrip.setDurationJourney(reqBusTripDTO.getDurationJourney());
        busTrip.setPriceTicket(reqBusTripDTO.getPriceTicket());
        busTrip.setDiscountPercentage(reqBusTripDTO.getDiscountPercentage());

        // Find pickup location for the bus trip by ids
        List<Integer> idOfPickupLocationList = reqBusTripDTO.getPickupLocationList().stream()
                .map(PickupLocation::getId).toList();
        List<PickupLocation> pickupLocationList = this.pickupLocationRepository.findByIdIn(idOfPickupLocationList);
        busTrip.setPickupLocationList(pickupLocationList);

        // Find drop off location for the bus trip by ids
        List<Integer> idOfDropOffLocationList = reqBusTripDTO.getDropOffLocationList().stream()
                .map(DropOffLocation::getId).toList();
        List<DropOffLocation> dropOffLocationList = this.dropOffLocationRepository.findByIdIn(idOfDropOffLocationList);
        busTrip.setDropOffLocationList(dropOffLocationList);

        // Add bus type for the bus trip
//        BusType busType = this.busTypeRepository.findById(reqBusTripDTO.getBusTypeId())
//                .orElseThrow(() -> new IdInvalidException("Bus type not found"));

        // Add bus for the bus trip
        Bus bus = this.busRepository.findById(reqBusTripDTO.getBusId())
                .orElseThrow(() -> new IdInvalidException("Bus not found"));
        busTrip.setBus(bus);

        List<BreakDay> breakDayList = reqBusTripDTO.getBreakDayList().stream()
                .map(item -> {
                    BreakDay breakDay  = new BreakDay();
                    breakDay.setStartDay(item.getStartDay());
                    breakDay.setEndDay(item.getEndDay());
                    return breakDay;
                }).toList();
        breakDayList.forEach(breakDay -> this.breakDayRepository.save(breakDay));

        List<DepartTimeBusTrip> departTimeBusTripList = reqBusTripDTO.getDepartTimeBusTripList().stream()
                .map(item -> {
                    DepartTimeBusTrip departTimeBusTrip = new DepartTimeBusTrip();
                    departTimeBusTrip.setDepartureTime(item.getDepartureTime());
                    return departTimeBusTrip;
                }).toList();
        List<DepartTimeBusTrip> savedDepartTimeBusTripList = this.departTimeBusTripRepository.saveAll(departTimeBusTripList);

        busTrip.setDepartTimeBusTripList(savedDepartTimeBusTripList);
        BusTrip savedBusTrip = this.busTripRepository.save(busTrip);
//        bre

    }
}
