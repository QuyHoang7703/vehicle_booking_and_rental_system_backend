package com.pbl6.VehicleBookingRental.user.dto.response.order;

import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripScheduleDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripScheduleDetailForAdminDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResOrderBusTripDetailDTO {
    private CustomerInfo customerInfo;
    private ResBusTripScheduleDTO.BusinessPartnerInfo businessPartnerInfo;
    private ResBusTripScheduleDetailForAdminDTO.BusInfo busInfo;
    private ResOrderBusTripDTO.OrderInfo orderInfo;
    private ResOrderBusTripDTO.TripInfo tripInfo;
    private Instant cancelTime;
//    private String key;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomerInfo {
        private String email;
        private String name;
        private String phoneNumber;
    }

//    @Data
//    @Builder
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class OrderInfo {
//        private String orderId;
//        private int numberOfTicket;
//        private String pricePerTicket;
//        private double discountPercentage;
//        private String priceTotal;
//        private Instant orderDate;
//    }
//
//    @Data
//    @Builder
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class TripInfo {
//        private String departureLocation;
//        private String arrivalLocation;
//        private Instant departureDateTime;
//        private Instant arrivalDateTime;
//    }
}
