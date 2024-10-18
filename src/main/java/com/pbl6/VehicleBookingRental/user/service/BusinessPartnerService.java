package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResBusinessPartnerDTO;
import com.pbl6.VehicleBookingRental.user.util.error.IdInValidException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface BusinessPartnerService {
    ResBusinessPartnerDTO convertToResBusinessPartnerDTO(BusinessPartner businessPartner);
    void verifyRegister(int id, String partnerType) throws IdInValidException;
    void cancelPartnership(int id, String partnerType) throws IdInValidException;
    ResultPaginationDTO handleFetchAllBusinessPartner(Specification<BusinessPartner> specification, Pageable pageable);
}
