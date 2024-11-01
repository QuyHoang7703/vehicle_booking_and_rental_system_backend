package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusPartner;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqBusPartnerDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResBusPartnerDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResBusinessPartnerDTO;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BusPartnerService {
    public ResBusinessPartnerDTO registerBusPartner(ReqBusPartnerDTO reqBusPartnerDTO,
                                                    MultipartFile avatar,
                                                    List<MultipartFile> licenses,
                                                    List<MultipartFile> images) throws Exception;
    public BusPartner getBusPartnerByBusinessPartnerId(int id) throws IdInvalidException;
    public ResBusPartnerDTO convertToResBusPartnerDTO(BusPartner busPartner) throws Exception;
    BusPartner findById(int id) throws IdInvalidException;
//    public void verifyRegister(int id, String partnerType) throws IdInValidException;
//    @Transactional
//    public void cancelPartnership(int id, String partnerType) throws IdInValidException;
}
