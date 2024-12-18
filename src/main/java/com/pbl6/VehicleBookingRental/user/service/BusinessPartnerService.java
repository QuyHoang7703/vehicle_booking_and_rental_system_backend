package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqPartnerAction;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResBusinessPartnerDTO;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

public interface BusinessPartnerService {
    boolean isRegistered(int accountId, PartnerTypeEnum partnerType);
    ResBusinessPartnerDTO convertToResBusinessPartnerDTO(BusinessPartner businessPartner) throws ApplicationException;
    void verifyRegister(int id, PartnerTypeEnum partnerType) throws IdInvalidException, ApplicationException, IOException;
    void cancelPartnership(ReqPartnerAction reqPartnerAction) throws Exception;
    ResultPaginationDTO handleFetchAllBusinessPartner(Specification<BusinessPartner> specification, Pageable pageable);
    BusinessPartner fetchByIdAndPartnerType(int id, PartnerTypeEnum partnerType);
    BusinessPartner getCurrentBusinessPartner(PartnerTypeEnum partnerType) throws ApplicationException;
    void refuseOrDeleteRegisterBusinessPartner(ReqPartnerAction reqPartnerAction) throws IdInvalidException, ApplicationException, IOException;
    BusinessPartner getBusinessPartnerById(int id) throws IdInvalidException;
    String getStatusRegisterPartner(PartnerTypeEnum partnerType) throws ApplicationException;
    List<String> getPolicies(int businessPartnerId) throws IdInvalidException;
}
