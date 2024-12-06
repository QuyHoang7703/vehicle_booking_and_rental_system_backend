package com.pbl6.VehicleBookingRental.user.dto.response.bus;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.util.List;
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResDropOffLocationDTO {
    private String departureLocation;
    private String province;
    private Duration journeyDuration;
    private String priceTicket;
    private List<String> dropOffLocations;
}
