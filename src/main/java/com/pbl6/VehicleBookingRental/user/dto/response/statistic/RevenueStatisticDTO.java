package com.pbl6.VehicleBookingRental.user.dto.response.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueStatisticDTO {
    private String period;
    private String revenue;
}
