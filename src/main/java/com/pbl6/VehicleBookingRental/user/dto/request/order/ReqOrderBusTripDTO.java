package com.pbl6.VehicleBookingRental.user.dto.request.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReqOrderBusTripDTO {
    private String customerName;
    private String customerPhoneNumber;
    private int busTripScheduleId;
    private String province;
    private int numberOfTicket;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate departureDate;
    private Integer voucherId;
}
