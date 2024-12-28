package com.pbl6.VehicleBookingRental.user.dto.response.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResCustomerInfoForOrderBusTrip {
    private int accountId;
    private String name;
    private String email;
    private String phoneNumber;
    private Instant orderTime;
    private int numberOfTicker;
    private String totalPrice;
    private Instant cancelTime;

}
