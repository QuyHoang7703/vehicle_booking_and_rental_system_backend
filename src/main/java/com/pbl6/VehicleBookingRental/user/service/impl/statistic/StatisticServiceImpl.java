package com.pbl6.VehicleBookingRental.user.service.impl.statistic;

import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.dto.ResultStatisticDTO;
import com.pbl6.VehicleBookingRental.user.dto.RevenueStatisticDTO;
import com.pbl6.VehicleBookingRental.user.repository.OrdersRepo;
import com.pbl6.VehicleBookingRental.user.service.statistic.OrderBusTripStatisticService;
import com.pbl6.VehicleBookingRental.user.service.statistic.StatisticService;
import com.pbl6.VehicleBookingRental.user.util.CurrencyFormatterUtil;
import com.pbl6.VehicleBookingRental.user.util.constant.OrderTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    @Override
    public Map<OrderTypeEnum, List<RevenueStatisticDTO>> getRevenueStatisticFromBusinessPartner(Integer year) {
//        Map<OrderTypeEnum, List<RevenueStatisticDTO>> revenueStatistic = new HashMap<>();
//        revenueStatistic.put("BUS_TRIP_ORDER")
        return Map.of();
    }

    @Override
    public ResultStatisticDTO createResultStatisticDTO(Map<String, Double> statistics) {
        List<RevenueStatisticDTO> revenueStatisticDTOS = statistics.entrySet().stream()
                .map(entry -> new RevenueStatisticDTO(entry.getKey(), CurrencyFormatterUtil.formatToVND(entry.getValue())))
                .toList();

        Double totalRevenue = 0.0;
        for(Map.Entry<String, Double> entry : statistics.entrySet()) {
            totalRevenue += entry.getValue();
        }

        return ResultStatisticDTO.builder()
                .totalRevenue(CurrencyFormatterUtil.formatToVND(totalRevenue))
                .revenueStatistic(revenueStatisticDTOS)
                .build();
    }

}
