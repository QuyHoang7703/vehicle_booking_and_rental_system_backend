package com.pbl6.VehicleBookingRental.user.service.impl;

import com.amazonaws.services.dynamodbv2.xspec.S;
import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.*;
import com.pbl6.VehicleBookingRental.user.dto.Meta;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBusTripDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResDropOffLocationDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResPickupAndDropOffLocation;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.*;
import com.pbl6.VehicleBookingRental.user.service.BusTripService;
import com.pbl6.VehicleBookingRental.user.service.BusinessPartnerService;
import com.pbl6.VehicleBookingRental.user.service.DropOffLocationService;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BusTripServiceImpl implements BusTripService {
    private final BusTripRepository busTripRepository;
    private final BusinessPartnerService businessPartnerService;
    private final DropOffLocationRepository dropOffLocationRepository;
    private final BusTripScheduleRepository busTripScheduleRepository;
    private final DropOffLocationService dropOffLocationService;
    @Override
    public BusTrip createBusTrip(ReqBusTripDTO reqBusTripDTO) throws ApplicationException {
        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);

        // Check new bus trip has already existed in the account bus partner
        BusTrip busTrips = this.busTripRepository.findBusTripByDepartureLocationAndArrivalLocationAndBusPartner(
                businessPartner.getBusPartner().getId(),
                reqBusTripDTO.getDepartureLocation(),
                reqBusTripDTO.getArrivalLocation())
                .orElse(null);

        if(busTrips!=null) {
            throw new ApplicationException("This bus trip already exists");
        }

        BusTrip busTrip = new BusTrip();
        busTrip.setDepartureLocation(reqBusTripDTO.getDepartureLocation());
        busTrip.setArrivalLocation(reqBusTripDTO.getArrivalLocation());
        List<String> pickupLocations = reqBusTripDTO.getPickupLocations();
        busTrip.setPickupLocations(String.join("!", pickupLocations));
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

        // Check busTrip has busTripSchedule ? -> Can't update
        if(busTripDb.getBusTripSchedules()!=null && !busTripDb.getBusTripSchedules().isEmpty()) {
            // Don't allow to update departure and arrival location for bus trip had schedules
            if(!reqBusTripDTO.getDepartureLocation().equals(busTripDb.getDepartureLocation()) ||
            !reqBusTripDTO.getArrivalLocation().equals(busTripDb.getArrivalLocation())) {
                throw new ApplicationException("Can't update departure or arrival location for a bus trip with schedules.");
            }
            // Only update pickupLocations
            List<String> pickupLocations = reqBusTripDTO.getPickupLocations();
            busTripDb.setPickupLocations(String.join("!", pickupLocations));
            return this.busTripRepository.save(busTripDb);
        }

        busTripDb.setDepartureLocation(reqBusTripDTO.getDepartureLocation());
        busTripDb.setArrivalLocation(reqBusTripDTO.getArrivalLocation());
        List<String> pickupLocations = reqBusTripDTO.getPickupLocations();
        busTripDb.setPickupLocations(String.join("!", pickupLocations));
        return this.busTripRepository.save(busTripDb);
    }

    @Override
    public ResBusTripDTO findBusTripById(int id) throws IdInvalidException, ApplicationException {
        BusTrip busTripDb = this.busTripRepository.findById(id)
                .orElseThrow(()-> new IdInvalidException("BusTrip not found"));
        return this.convertToResBusTripDTO(busTripDb);
    }

    @Override
    public ResBusTripDTO convertToResBusTripDTO(BusTrip busTrip) throws ApplicationException {
        String pickupLocations = busTrip.getPickupLocations();
        List<String> pickupLocationsToList = Arrays.asList(pickupLocations.split("!"));

        ResBusTripDTO.BusTripInfo busTripInfo = this.convertToBusTripInfo(busTrip);

        ResBusTripDTO resBusTripDTO = new ResBusTripDTO();
        resBusTripDTO.setBusTripInfo(busTripInfo);
        resBusTripDTO.setPickupLocations(pickupLocationsToList);

        if(busTrip.getDropOffLocations()!=null && !busTrip.getDropOffLocations().isEmpty()) {
            List<ResDropOffLocationDTO> dropOffLocationDTOS = busTrip.getDropOffLocations().stream()
                    .map(dropOffLocation -> this.dropOffLocationService.convertToResDropOffLocationDTO(dropOffLocation))
                    .toList();
            resBusTripDTO.setDropOffLocationInfos(dropOffLocationDTOS);
        }
//
//        ResBusTripDTO resBusTripDTO = ResBusTripDTO.builder()
//                .busTripInfo(busTripInfo)
//                .pickupLocations(pickupLocationsToList)
//                .dropOffLocationInfos(dropOffLocationDTOS)
//                .build();
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
                .map(busTrip -> {
                    try {
                        return this.convertToBusTripInfo(busTrip);
                    } catch (ApplicationException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        res.setResult(resBusTripDTOList);
        return res;
    }

    @Override
    public ResBusTripDTO.BusTripInfo convertToBusTripInfo(BusTrip busTrip) throws ApplicationException {
            DropOffLocation dropOffLocation = this.dropOffLocationRepository.findByProvinceAndBusTripId(busTrip.getArrivalLocation(), busTrip.getId())
                .orElse(null);

        ResBusTripDTO.BusTripInfo busTripInfo = ResBusTripDTO.BusTripInfo.builder()
                .id(busTrip.getId())
                .departureLocation(busTrip.getDepartureLocation())
                .arrivalLocation(busTrip.getArrivalLocation())
                .journeyDuration(dropOffLocation!=null ? dropOffLocation.getJourneyDuration() : null)
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

    @Override
    public ResPickupAndDropOffLocation getPickupAndDropOffLocationById(int busTripScheduleId, String arrivalProvince) throws IdInvalidException, ApplicationException {
        BusTripSchedule busTripSchedule = this.busTripScheduleRepository.findById(busTripScheduleId)
                .orElseThrow(()-> new IdInvalidException("BusTripSchedule not found"));
        String pickupLocations = busTripSchedule.getBusTrip().getPickupLocations();
        List<String> pickupLocationsToList = Arrays.asList(pickupLocations.split("!"));

        DropOffLocation dropOffLocation = this.dropOffLocationRepository.findByProvinceAndBusTripScheduleId(arrivalProvince, busTripScheduleId)
                .orElseThrow(()-> new ApplicationException("DropOffLocation not found"));

        List<String> dropOffLocationToList = Arrays.asList(dropOffLocation.getDropOffLocation().split("!"));
        ResPickupAndDropOffLocation res = ResPickupAndDropOffLocation.builder()
                .pickupLocations(pickupLocationsToList)
                .dropOffLocations(dropOffLocationToList)
                .build();
        return res;
    }

    @Override
    public List<String> getRouteOfBusTrips() throws ApplicationException {
        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);
        List<BusTrip> busTrips = businessPartner.getBusPartner().getBusTrips();

        List<String> routes = busTrips.stream()
                .map(busTrip -> busTrip.getDepartureLocation() + "-" + busTrip.getArrivalLocation())
                .toList();

        return routes;
    }


}
