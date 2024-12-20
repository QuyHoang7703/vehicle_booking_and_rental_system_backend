package com.pbl6.VehicleBookingRental.user.service.impl.statistic;

import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.dto.RevenueStatisticDTO;
import com.pbl6.VehicleBookingRental.user.repository.OrdersRepo;
import com.pbl6.VehicleBookingRental.user.service.statistic.StatisticService;
import com.pbl6.VehicleBookingRental.user.util.constant.OrderTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final OrdersRepo ordersRepo;


    @Override
    public Map<OrderTypeEnum, List<RevenueStatisticDTO>> getRevenueStatisticFromBusinessPartner(Integer year) {
        Map<OrderTypeEnum, List<RevenueStatisticDTO>> revenueStatistic = new HashMap<>();

        return Map.of();
    }

    public List<RevenueStatisticDTO> getRevenueOfBusPartner(Integer month, Integer year) {
//        List<Orders> ordersOfBusPartners = this.ordersRepo.findByOrder_Type("BUS_TRIP_ORDER");
//        Map<String, Double> statistics = new LinkedHashMap<>();
//        // Create key
//        for(int i = 1; i<=12; i++){
//            String key = i + "-" + year;
//
//        }

        return null;
    }
}
