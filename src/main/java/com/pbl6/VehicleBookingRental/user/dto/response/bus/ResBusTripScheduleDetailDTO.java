package com.pbl6.VehicleBookingRental.user.dto.response.bus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BreakDay;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Data
@Builder
public class ResBusTripScheduleDetailDTO {
    private int idBusTripSchedule;
    private ResBusTripDTO.BusTripInfo busTripInfo;
    private BusInfo busInfo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime departureTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime arrivalTime;
    private double discountPercentage;
    private String priceTicket;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate startOperationDay;
    private int availableSeats;
    private List<BreakDay> breakDays;
    private boolean isOperation;

    @Data
    @Builder
    public static class BusInfo{
        private String licensePlate;
        private BusType busType;
    }

}
