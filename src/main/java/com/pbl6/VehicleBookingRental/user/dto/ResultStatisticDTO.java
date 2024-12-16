package com.pbl6.VehicleBookingRental.user.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ResultStatisticDTO {
    private String totalRevenue;
    private List<RevenueStatisticDTO> revenueStatistic;
}
