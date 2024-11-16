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
    private int idBusTripSchedule;
    private String businessName;
    private String busTypeName;
    private String departureLocation;
    private String arrivalLocation;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime departureTime;
    @JsonSerialize(using = CustomDurationSerializer.class)
    private Duration durationJourney;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime arrivalTime;
    private double discountPercentage;
    private double priceTicket;
    private int availableSeats;
    private boolean isOperation;

    public LocalTime getArrivalTime() {
        if(this.departureTime != null && this.durationJourney != null) {
            this.arrivalTime = this.departureTime.plus(this.durationJourney);
            return this.arrivalTime;
        }
        return null;

    }

}
