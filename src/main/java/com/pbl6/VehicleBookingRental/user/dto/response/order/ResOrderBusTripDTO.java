package com.pbl6.VehicleBookingRental.user.dto.response.order;

import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripScheduleDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripScheduleDetailForAdminDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResOrderBusTripDTO {
    private ResBusTripScheduleDTO.BusinessPartnerInfo businessPartner;
    private OrderInfo orderInfo;
    private TripInfo tripInfo;
    private ResBusDTO busInfo;
//    private ResBusTripScheduleDetailForAdminDTO.BusInfo busInfo;

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
        private Duration durationJourney;
        private Instant arrivalDateTime;
    }
}
