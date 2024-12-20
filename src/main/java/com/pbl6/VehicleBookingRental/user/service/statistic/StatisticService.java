package com.pbl6.VehicleBookingRental.user.service.statistic;

import com.pbl6.VehicleBookingRental.user.dto.RevenueStatisticDTO;
import com.pbl6.VehicleBookingRental.user.util.constant.OrderTypeEnum;

import java.util.List;
import java.util.Map;

public interface StatisticService {
    Map<OrderTypeEnum, List<RevenueStatisticDTO>> getRevenueStatisticFromBusinessPartner(Integer year);
}
