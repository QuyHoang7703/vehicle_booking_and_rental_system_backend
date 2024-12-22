package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTrip;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.DropOffLocation;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqDropOffLocationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqUpdateDropOffLocationDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResDropOffLocationDTO;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusTripRepository;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.DropOffLocationRepository;
import com.pbl6.VehicleBookingRental.user.service.BusinessPartnerService;
import com.pbl6.VehicleBookingRental.user.service.DropOffLocationService;
import com.pbl6.VehicleBookingRental.user.util.CurrencyFormatterUtil;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DropOffLocationServiceImpl implements DropOffLocationService {
    private final DropOffLocationRepository dropOffLocationRepository;
    private final BusTripRepository busTripRepository;
    private final BusinessPartnerService businessPartnerService;
    @Override
    public void createDropOffLocation(ReqDropOffLocationDTO reqDropOffLocationDTO) throws IdInvalidException, ApplicationException {
        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);

        BusTrip busTrip = busTripRepository.findById(reqDropOffLocationDTO.getBusTripId()).
                orElseThrow(() -> new IdInvalidException("Bus trip not found"));

        if(!busTrip.getBusPartner().getBusinessPartner().equals(businessPartner)) {
            throw new ApplicationException("You don't have permission to add drop off location to this bus trip");
        }

        DropOffLocation dropOffLocationDb = this.dropOffLocationRepository.findByProvinceAndBusTripId(reqDropOffLocationDTO.getProvince(), busTrip.getId())
                .orElse(null);
        if(dropOffLocationDb != null) {
            throw new ApplicationException("Drop off location already exists");
        }

        DropOffLocation dropOffLocation = new DropOffLocation();
        dropOffLocation.setProvince(reqDropOffLocationDTO.getProvince());
        dropOffLocation.setPriceTicket(reqDropOffLocationDTO.getPriceTicket());
        dropOffLocation.setDropOffLocation(String.join("!", reqDropOffLocationDTO.getDropOffLocations()));
        dropOffLocation.setJourneyDuration(reqDropOffLocationDTO.getJourneyDuration());
        dropOffLocation.setBusTrip(busTrip);

        this.dropOffLocationRepository.save(dropOffLocation);
    }

    @Override
    public ResDropOffLocationDTO convertToResDropOffLocationDTO(DropOffLocation dropOffLocation) {
        ResDropOffLocationDTO res = ResDropOffLocationDTO.builder()
                .departureLocation(dropOffLocation.getBusTrip().getDepartureLocation())
                .province(dropOffLocation.getProvince())
                .journeyDuration(dropOffLocation.getJourneyDuration())
                .priceTicket(CurrencyFormatterUtil.formatToVND(dropOffLocation.getPriceTicket()))
                .dropOffLocations(Arrays.asList(dropOffLocation.getDropOffLocation().split("!")))
                .build();

        return res;
    }

    @Override
    public ResDropOffLocationDTO updateDropOffLocation(ReqUpdateDropOffLocationDTO req) throws ApplicationException, IdInvalidException {
        if(req.getDropOffLocations() == null || req.getDropOffLocations().isEmpty()) {
            throw new ApplicationException("Can't left blank drop off location");
        }
        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);

        DropOffLocation dropOffLocationDb = this.dropOffLocationRepository.findById(req.getId())
                .orElseThrow(()-> new IdInvalidException("Drop off location not found"));

        if(dropOffLocationDb.getBusTrip().getBusPartner().getId() != businessPartner.getBusPartner().getId()){
            throw new ApplicationException("You don't have permission to update drop off location");
        }

        String dropOffLocation = String.join("!", req.getDropOffLocations());
        dropOffLocationDb.setDropOffLocation(dropOffLocation);

        return this.convertToResDropOffLocationDTO(this.dropOffLocationRepository.save(dropOffLocationDb));
    }
}
