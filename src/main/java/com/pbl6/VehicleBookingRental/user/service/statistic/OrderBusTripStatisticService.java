package com.pbl6.VehicleBookingRental.user.service.statistic;

import com.pbl6.VehicleBookingRental.user.dto.ResultStatisticDTO;
import com.pbl6.VehicleBookingRental.user.dto.RevenueStatisticDTO;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;

import java.util.List;

public interface OrderBusTripStatisticService {
    ResultStatisticDTO getOrderBusTripRevenueByMonthOfYear(Integer year) throws ApplicationException;
    ResultStatisticDTO getOrderBusTripRevenueByYear() throws ApplicationException;
}
