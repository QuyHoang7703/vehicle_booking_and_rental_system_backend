package com.pbl6.VehicleBookingRental.user.dto.response.statistic;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ResultStatisticDTO {
    private String totalRevenue;
    private List<RevenueStatisticDTO> revenueStatistic;

//    @Data
//    @Builder
//    public static class RevenueStatisticDTO {
//        private String period;
//        private String revenue;
//    }
}
