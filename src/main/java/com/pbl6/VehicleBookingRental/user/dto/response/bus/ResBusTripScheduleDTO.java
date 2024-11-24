package com.pbl6.VehicleBookingRental.user.dto.response.bus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pbl6.VehicleBookingRental.user.config.CustomDurationSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResBusTripScheduleDTO {
    private int busTripScheduleId;
    private BusinessPartnerInfo businessPartnerInfo;

    private ResBusTripDTO.BusTripInfo busTripInfo;
    private ResBusDTO busInfo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime departureTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime arrivalTime;
    private double discountPercentage;
    private String priceTicket;
    private int availableSeats;
    private boolean isOperation;

    @Data
    @Builder
    public static class BusinessPartnerInfo {
        private int id;
        private String name;
    }



}
