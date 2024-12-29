package com.pbl6.VehicleBookingRental.user.service.statistic;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.statistic.ResultStatisticDTO;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;

public interface StatisticService {
    ResultStatisticDTO createResultStatisticDTO(Map<String, Double> statistics);
    ResultStatisticDTO getRevenueByMonthOrByYear(Integer year) throws ApplicationException;
    ResultPaginationDTO getRevenueOfBusinessPartner(Integer month, Integer year, PartnerTypeEnum partnerType, Pageable pageable) throws ApplicationException;
    ResultPaginationDTO getCustomerOfBusinessPartner(Integer month, Integer year, int businessPartnerId, Pageable pageable) throws IdInvalidException;
}
