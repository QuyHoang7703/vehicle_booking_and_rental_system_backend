package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusPartner;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqBusPartnerDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResBusPartnerDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BusPartnerService {
    public BusPartner registerBusPartner(ReqBusPartnerDTO reqBusPartnerDTO, List<MultipartFile> licenses, List<MultipartFile> images);
    public ResBusPartnerDTO convertToResBusPartnerDTO(BusPartner busPartner);
}
