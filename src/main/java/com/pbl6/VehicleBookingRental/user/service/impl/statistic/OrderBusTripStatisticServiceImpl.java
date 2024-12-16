package com.pbl6.VehicleBookingRental.user.service.impl.statistic;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.OrderBusTrip;
import com.pbl6.VehicleBookingRental.user.dto.ResultStatisticDTO;
import com.pbl6.VehicleBookingRental.user.dto.RevenueStatisticDTO;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusTripScheduleRepository;
import com.pbl6.VehicleBookingRental.user.repository.order.OrderBusTripRepository;
import com.pbl6.VehicleBookingRental.user.service.BusinessPartnerService;
import com.pbl6.VehicleBookingRental.user.service.statistic.OrderBusTripStatisticService;
import com.pbl6.VehicleBookingRental.user.util.CurrencyFormatterUtil;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderBusTripStatisticServiceImpl implements OrderBusTripStatisticService {
    private final OrderBusTripRepository orderBusTripRepository;
    private final BusinessPartnerService businessPartnerService;

    @Override
    public ResultStatisticDTO getOrderBusTripRevenueByPeriod(Integer year) throws ApplicationException {
        Map<String, Double> statistics = new LinkedHashMap<>();
        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);
        // Get order bus trip of bus partner has
        List<OrderBusTrip> orderBusTrips = this.orderBusTripRepository.findOrderBusTripsOfBusPartner(businessPartner.getBusPartner().getId());

        // Create key in hash map
        for(int i = 1; i<=12; i++) {
            String key = i + "-" + year;
            statistics.put(key, 0.0);
        }

        // Calculate revenue for keys
        for(OrderBusTrip orderBusTrip : orderBusTrips) {
            Orders order = orderBusTrip.getOrder();
            LocalDateTime createdOrderAt = order.getCreate_at().atZone(ZoneId.systemDefault()).toLocalDateTime();
            if(createdOrderAt.getYear() == year) {
                String key = createdOrderAt.getMonthValue() + "-" + createdOrderAt.getYear();
                double revenue = orderBusTrip.getPriceTotal();
                double currentRevenue = statistics.getOrDefault(key, 0.0);
                statistics.put(key, revenue+currentRevenue);
            }
        }

        List<RevenueStatisticDTO> revenueStatisticDTOS = statistics.entrySet().stream()
                .map(entry -> new RevenueStatisticDTO(entry.getKey(), CurrencyFormatterUtil.formatToVND(entry.getValue())))
                .toList();

        Double totalRevenue = 0.0;
        for(Map.Entry<String, Double> entry : statistics.entrySet()) {
            totalRevenue += entry.getValue();
        }

        ResultStatisticDTO resultStatisticDTO = ResultStatisticDTO.builder()
                .totalRevenue(CurrencyFormatterUtil.formatToVND(totalRevenue))
                .revenueStatistic(revenueStatisticDTOS)
                .build();


        return resultStatisticDTO;
    }
}
