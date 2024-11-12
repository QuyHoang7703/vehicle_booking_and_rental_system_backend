package com.pbl6.VehicleBookingRental.user.service.impl;

import com.amazonaws.services.dynamodbv2.xspec.S;
import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.*;
import com.pbl6.VehicleBookingRental.user.dto.Meta;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBusTripDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripDTO;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.*;
import com.pbl6.VehicleBookingRental.user.service.BusTripService;
import com.pbl6.VehicleBookingRental.user.service.BusinessPartnerService;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BusTripServiceImpl implements BusTripService {
    private final BusTripRepository busTripRepository;
    private final BusinessPartnerService businessPartnerService;
    @Override
    public BusTrip createBusTrip(ReqBusTripDTO reqBusTripDTO) throws ApplicationException {
        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);
        List<BusTrip> busTrips = this.busTripRepository.findBusTripByDepartureLocationAndArrivalLocation(businessPartner.getBusPartner().getId(),
                reqBusTripDTO.getDepartureLocation(), reqBusTripDTO.getArrivalLocation());

        if(busTrips!=null && !busTrips.isEmpty()) {
            throw new ApplicationException("This bus trip already exists");
        }

        BusTrip busTrip = new BusTrip();
        busTrip.setDepartureLocation(reqBusTripDTO.getDepartureLocation());
        busTrip.setArrivalLocation(reqBusTripDTO.getArrivalLocation());
        busTrip.setDurationJourney(reqBusTripDTO.getDurationJourney());
        List<String> pickupLocations = reqBusTripDTO.getPickupLocations();
        busTrip.setPickupLocations(String.join("!", pickupLocations));
        List<String> dropOffLocations = reqBusTripDTO.getDropOffLocations();
        busTrip.setDropOffLocations(String.join("!", dropOffLocations));
        busTrip.setBusPartner(businessPartner.getBusPartner());
        return busTripRepository.save(busTrip);
    }

    @Override
    public BusTrip updateBusTrip(ReqBusTripDTO reqBusTripDTO) throws IdInvalidException, ApplicationException {
        BusTrip busTripDb = this.busTripRepository.findById(reqBusTripDTO.getId())
                .orElseThrow(()-> new IdInvalidException("BusTrip not found"));
        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);
        if(!businessPartner.getBusPartner().equals(busTripDb.getBusPartner())) {
            throw new ApplicationException("You don't have the right business partner");
        }

        if(busTripDb.getBusTripSchedules()!=null && !busTripDb.getBusTripSchedules().isEmpty()) {
            throw new ApplicationException("Can't delete this bus trip scheduled");
        }

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
        return this.convertToResBusTripDTO(busTripDb);
    }

    @Override
    public ResBusTripDTO convertToResBusTripDTO(BusTrip busTrip) {
        String pickupLocations = busTrip.getPickupLocations();
        List<String> pickupLocationsToList = Arrays.asList(pickupLocations.split("!"));

        String dropOffLocations = busTrip.getDropOffLocations();
        List<String> dropOffLocationsToList =  Arrays.asList(dropOffLocations.split("!"));

        ResBusTripDTO.BusTripInfo busTripInfo = this.convertToBusTripInfo(busTrip);

        ResBusTripDTO resBusTripDTO = ResBusTripDTO.builder()
                .busTripInfo(busTripInfo)
                .pickupLocations(pickupLocationsToList)
                .dropOffLocations(dropOffLocationsToList)
                .build();
        return resBusTripDTO;
    }

    @Override
    public ResultPaginationDTO getAllBusTrips(Specification<BusTrip> spec, Pageable pageable) throws ApplicationException {
        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);

        Specification<BusTrip> newSpec = (root, query, criteriaBuilder) -> {
            Join<BusTrip, BusPartner> joinBusPartner = root.join("busPartner");
            return criteriaBuilder.equal(joinBusPartner.get("id"), businessPartner.getId());
        };
        Specification<BusTrip> finalSpec = spec.and(newSpec);
        Page<BusTrip> busTripPage = this.busTripRepository.findAll(finalSpec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();

        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(busTripPage.getTotalPages());
        meta.setTotal(busTripPage.getTotalElements());

        res.setMeta(meta);

        List<ResBusTripDTO.BusTripInfo> resBusTripDTOList = busTripPage.getContent().stream()
                .map(busTrip -> this.convertToBusTripInfo(busTrip))
                .toList();

        res.setResult(resBusTripDTOList);
        return res;
    }

    @Override
    public ResBusTripDTO.BusTripInfo convertToBusTripInfo(BusTrip busTrip) {
        ResBusTripDTO.BusTripInfo busTripInfo = ResBusTripDTO.BusTripInfo.builder()
                .id(busTrip.getId())
                .departureLocation(busTrip.getDepartureLocation())
                .arrivalLocation(busTrip.getArrivalLocation())
                .durationJourney(busTrip.getDurationJourney())
                .build();
        return busTripInfo;
    }

    @Override
    public void deleteBusTrip(int busTripId) throws IdInvalidException, ApplicationException {
        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);
        BusTrip busTrip = this.busTripRepository.findById(busTripId)
                .orElseThrow(()-> new IdInvalidException("BusTrip not found"));
        if(!businessPartner.getBusPartner().equals(busTrip.getBusPartner())) {
            throw new ApplicationException("You don't have permission to delete this bus trip");
        }
        if(busTrip.getBusTripSchedules()!=null && !busTrip.getBusTripSchedules().isEmpty()) {
            throw new ApplicationException("Can't delete the bus trip scheduled");
        }
        this.busTripRepository.deleteById(busTripId);
    }


}
