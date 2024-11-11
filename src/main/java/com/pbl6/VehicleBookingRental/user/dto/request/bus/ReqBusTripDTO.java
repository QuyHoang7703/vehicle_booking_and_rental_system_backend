package com.pbl6.VehicleBookingRental.user.dto.request.bus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.DropOffLocation;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.PickupLocation;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
public class ReqBusTripDTO {
    private String departureLocation;
    private String arrivalLocation;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate startOperationDay;
    private String durationJourney;
    private Double priceTicket;
    private Double discountPercentage;
    private List<PickupLocation> pickupLocationList;
    private List<DropOffLocation> dropOffLocationList;
//    private int busId;

}
