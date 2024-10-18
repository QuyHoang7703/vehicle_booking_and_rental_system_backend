package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalPartner;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqCarRentalPartnerDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResBusinessPartnerDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResCarRentalPartnerDTO;
import com.pbl6.VehicleBookingRental.user.util.error.IdInValidException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CarRentalPartnerService {
    public ResBusinessPartnerDTO registerBusPartner(ReqCarRentalPartnerDTO reqCarRentalPartnerDTO, MultipartFile avatar, List<MultipartFile> licenses, List<MultipartFile> images);
    public CarRentalPartner getCarRentalPartnerByBusinessPartnerId(int id) throws IdInValidException;
    public ResCarRentalPartnerDTO convertoCarRentalPartnerDTO(CarRentalPartner carRentalPartner) throws IdInValidException;
}
