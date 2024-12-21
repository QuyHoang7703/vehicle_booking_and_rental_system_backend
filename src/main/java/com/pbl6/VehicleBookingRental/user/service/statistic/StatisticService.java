package com.pbl6.VehicleBookingRental.user.service.statistic;

import com.pbl6.VehicleBookingRental.user.dto.ResultStatisticDTO;
import com.pbl6.VehicleBookingRental.user.dto.RevenueStatisticDTO;
import com.pbl6.VehicleBookingRental.user.util.constant.OrderTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;

import java.util.List;
import java.util.Map;

public interface StatisticService {
    ResultStatisticDTO createResultStatisticDTO(Map<String, Double> statistics);
    public ResultStatisticDTO getRevenueStatisticFromBusinessPartner(Integer year) throws ApplicationException;
}
