package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusPartner;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.BusinessPartnerDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqBusPartnerDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BusPartnerService {
    public BusPartner registerBusPartner(ReqBusPartnerDTO reqBusPartnerDTO, List<MultipartFile> licenses, List<MultipartFile> images);

}
