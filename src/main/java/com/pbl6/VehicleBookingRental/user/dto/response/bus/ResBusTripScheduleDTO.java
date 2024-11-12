package com.pbl6.VehicleBookingRental.user.dto.response.bus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BreakDay;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTrip;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Data
@Builder
public class ResBusTripScheduleDTO {
    private ResBusTripDTO.BusTripInfo busTripInfo;
    private BusInfo busInfo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime departureTime;
    private double discountPercentage;
    private double priceTicket;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate startOperationDay;
    private int availableSeats;
    private List<BreakDay> breakDays;

    @Data
    @Builder
    public static class BusInfo{
        private String licensePlate;
        private BusType busType;
    }

}
