package com.pbl6.VehicleBookingRental.user.dto.response.order;

import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResOrderBusTripDTO {
    private OrderInfo orderInfo;
    private TripInfo tripInfo;
    private ResBusDTO busInfo;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderInfo {
        private String orderId;
        private String transactionCode;
        private int numberOfTicket;
        private String pricePerTicket;
        private double discountPercentage;
        private String priceTotal;
        private Instant orderDate;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TripInfo {
        private String departureLocation;
        private String arrivalLocation;
        private Instant departureDateTime;
        private Instant arrivalDateTime;
    }
}
