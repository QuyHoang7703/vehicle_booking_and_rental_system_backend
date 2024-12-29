package com.pbl6.VehicleBookingRental.user.dto.response.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerStatisticDTO {
    private String customerName;
    private String customerPhoneNumber;
    private String totalPrice;
    private Instant orderDate;
}
