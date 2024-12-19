package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTrip;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBusTripDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResPickupAndDropOffLocation;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface BusTripService {
    public BusTrip createBusTrip(ReqBusTripDTO reqBusTripDTO) throws ApplicationException;
    public BusTrip updateBusTrip(ReqBusTripDTO reqBusTripDTO) throws IdInvalidException, ApplicationException;
    public ResBusTripDTO findBusTripById(int id) throws IdInvalidException, ApplicationException;
    ResBusTripDTO convertToResBusTripDTO(BusTrip busTrip) throws ApplicationException;
    ResultPaginationDTO getAllBusTrips(Specification<BusTrip> specification, Pageable pageable) throws ApplicationException;
    ResBusTripDTO.BusTripInfo convertToBusTripInfo(BusTrip busTrip) throws ApplicationException;
    void deleteBusTrip(int busTripId) throws IdInvalidException, ApplicationException;
    ResPickupAndDropOffLocation getPickupAndDropOffLocationById(int id, String arrivalProvince) throws IdInvalidException, ApplicationException;
    List<String> getRouteOfBusTrips() throws ApplicationException;
}
