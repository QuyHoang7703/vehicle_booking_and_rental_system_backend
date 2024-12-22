package com.pbl6.VehicleBookingRental.user.service.impl.statistic;

import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalOrders;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalService;
import com.pbl6.VehicleBookingRental.user.dto.ResultStatisticDTO;
import com.pbl6.VehicleBookingRental.user.dto.RevenueStatisticDTO;
import com.pbl6.VehicleBookingRental.user.repository.OrdersRepo;
import com.pbl6.VehicleBookingRental.user.repository.businessPartner.BusPartnerRepository;
import com.pbl6.VehicleBookingRental.user.service.OrderBusTripService;
import com.pbl6.VehicleBookingRental.user.service.VehicleRentalStatisticService;
import com.pbl6.VehicleBookingRental.user.service.statistic.OrderBusTripStatisticService;
import com.pbl6.VehicleBookingRental.user.service.statistic.StatisticService;
import com.pbl6.VehicleBookingRental.user.util.CurrencyFormatterUtil;
import com.pbl6.VehicleBookingRental.user.util.constant.OrderTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticServiceImpl implements StatisticService {
    private final OrderBusTripStatisticService orderBusTripStatisticService;
    private final VehicleRentalStatisticService vehicleRentalStatisticService;

    public StatisticServiceImpl(@Lazy OrderBusTripStatisticService orderBusTripStatisticService, @Lazy VehicleRentalStatisticService vehicleRentalStatisticService) {
        this.orderBusTripStatisticService = orderBusTripStatisticService;
        this.vehicleRentalStatisticService = vehicleRentalStatisticService;
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

    @Override
    public ResultStatisticDTO getRevenueStatisticFromBusinessPartner(Integer year) throws ApplicationException {
        ResultStatisticDTO statisticFromBusPartner = this.orderBusTripStatisticService.getOrderBusTripRevenueByPeriod(year);
        ResultStatisticDTO statisticFromCarRentalPartner = this.vehicleRentalStatisticService.calculateMonthlyRevenue(year);

        Map<String, Double> combinedStatistics = new LinkedHashMap<>();


        for(RevenueStatisticDTO statistic : statisticFromBusPartner.getRevenueStatistic()) {
            String key = statistic.getPeriod();
            String revenueString = statistic.getRevenue().split("VND")[0];
            revenueString = revenueString.replace(".", "");
            double revenue = Double.parseDouble(revenueString);
            combinedStatistics.put(key, combinedStatistics.getOrDefault(key, 0.0) + revenue);
        }

        for (RevenueStatisticDTO statistic : statisticFromCarRentalPartner.getRevenueStatistic()) {
            String key = statistic.getPeriod();
            String revenueString = statistic.getRevenue().split("VND")[0];
            revenueString = revenueString.replace(".", "");
            double revenue = Double.parseDouble(revenueString);
            combinedStatistics.put(key, combinedStatistics.getOrDefault(key, 0.0) + revenue);
        }

        return this.createResultStatisticDTO(combinedStatistics);
    }

}
