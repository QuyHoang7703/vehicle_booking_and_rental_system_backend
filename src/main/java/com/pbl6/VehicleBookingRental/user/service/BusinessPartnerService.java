package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqCancelPartner;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResBusinessPartnerDTO;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface BusinessPartnerService {
    boolean isRegistered(int accountId, PartnerTypeEnum partnerType);
    ResBusinessPartnerDTO convertToResBusinessPartnerDTO(BusinessPartner businessPartner);
    void verifyRegister(int id, PartnerTypeEnum partnerType) throws IdInvalidException, ApplicationException;
    void cancelPartnership(ReqCancelPartner reqCancelPartner) throws Exception;
    ResultPaginationDTO handleFetchAllBusinessPartner(Specification<BusinessPartner> specification, Pageable pageable);
    BusinessPartner fetchByIdAndPartnerType(int id, PartnerTypeEnum partnerType);

}
