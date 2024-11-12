package com.pbl6.VehicleBookingRental.user.dto.response.bus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTripSchedule;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResBusTripDTO {

    private BusTripInfo busTripInfo;
    private List<String> pickupLocations;
    private List<String> dropOffLocations;
    private List<BusTripSchedule> busTripSchedules;

    @Data
    @Builder
    public static class BusTripInfo{
        private int id;
        private String departureLocation;
        private String arrivalLocation;
        private String durationJourney;
    }
}
