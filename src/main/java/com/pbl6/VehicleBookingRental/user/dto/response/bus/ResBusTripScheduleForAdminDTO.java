package com.pbl6.VehicleBookingRental.user.dto.response.bus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class ResBusTripScheduleForAdminDTO {
    private int busTripSchedule;
    private BusInfo busInfo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime departureTime;
    private String status;
    private double ratingValue;
    private double discountPercentage;
    private int availableSeats;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BusInfo{
        private String licensePlate;
        private String imageRepresentative;
        private BusType busType;
    }

}
