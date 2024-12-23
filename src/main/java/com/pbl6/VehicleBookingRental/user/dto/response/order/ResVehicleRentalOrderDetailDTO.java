package com.pbl6.VehicleBookingRental.user.dto.response.order;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
@Data
@Builder
public class ResVehicleRentalOrderDetailDTO {
    private String orderId;
    private String transactionCode;

    private CustomerInfo customerInfo;
    private RentalInfo rentalInfo;
    private PricingInfo pricingInfo;

    private Instant createAt;


    @Data
    @Builder
    public static class CustomerInfo {
        private int accountId;
        private String email;
        private String name;
        private String phoneNumber;
    }

    @Data
    @Builder
    public static class RentalInfo {
        private int carRentalServiceId;
        private int numberOfVehicles;
        private Instant startRentalTime;
        private Instant endRentalTime;
        private String pickupLocation;
        private Instant cancelAt;
    }

    @Data
    @Builder
    public static class PricingInfo {
        private double price;
        private double voucherValue;
        private double voucherPercentage;
        private double carDeposit;
        private double reservationFee;
        private double priceTotal;
    }
}
