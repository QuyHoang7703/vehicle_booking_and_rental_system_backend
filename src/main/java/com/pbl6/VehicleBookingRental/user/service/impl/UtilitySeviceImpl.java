package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.Utility;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.UtilityRepository;
import com.pbl6.VehicleBookingRental.user.service.BusinessPartnerService;
import com.pbl6.VehicleBookingRental.user.service.UtilityService;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilitySeviceImpl implements UtilityService {
    private final UtilityRepository utilityRepository;
    private final BusinessPartnerService businessPartnerService;

    @Override
    public Utility createUtility(Utility reqUtility) throws ApplicationException {
        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);
//        reqUtility.set

        return null;
    }

    @Override
    public Utility updateUtility(Utility reqUtility) {
        return null;
    }

    @Override
    public void deleteUtility(int idUtility) {

    }

    @Override
    public Utility getUtility(int idUtility) {
        return null;
    }

    @Override
    public ResultPaginationDTO getAllUtility(Specification<Utility> specification, Pageable pageable) {
        return null;
    }
}
