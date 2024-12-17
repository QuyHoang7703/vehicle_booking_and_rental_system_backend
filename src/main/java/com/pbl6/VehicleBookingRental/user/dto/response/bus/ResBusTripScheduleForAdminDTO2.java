package com.pbl6.VehicleBookingRental.user.dto.response.bus;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalTime;

@Data
@Builder
public class ResBusTripScheduleForAdminDTO2 {
    private int busTripScheduleId;
    private ResBusTripDTO.BusTripInfo busTripInfo;
    private ResBusTripScheduleForAdminDTO.BusInfo busInfo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime departureTime;
    private Duration journeyDuration;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime arrivalTime;
//    private double discountPercentage;
    private String priceTicket;
//    private int availableSeats;
    private boolean isOperation;
    private double ratingTotal;
}
