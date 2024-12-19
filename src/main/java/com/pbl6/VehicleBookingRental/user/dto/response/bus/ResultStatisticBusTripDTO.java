package com.pbl6.VehicleBookingRental.user.dto.response.bus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultStatisticBusTripDTO {
    private String route;
    private int soldTickets;
//    private int remainingTickets;
    private int cancelledTickets;

}
