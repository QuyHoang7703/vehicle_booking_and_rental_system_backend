package com.pbl6.VehicleBookingRental.user.dto.request.bus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BreakDay;
import com.pbl6.VehicleBookingRental.user.dto.LocationDTO;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class ReqBusTripScheduleDTO {
    private int busTripId;
    private int busId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime departureTime;
    private double discountPercentage;
    private double priceTicket;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate startOperationDay;
    private List<BreakDay> breakDays;

//    private LocationDTO departure
}
