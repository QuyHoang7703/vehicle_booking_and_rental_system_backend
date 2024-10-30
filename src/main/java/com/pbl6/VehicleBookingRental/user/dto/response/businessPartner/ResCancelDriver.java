package com.pbl6.VehicleBookingRental.user.dto.response.businessPartner;

import lombok.Data;

import java.time.Instant;
@Data
public class ResCancelDriver {
    private String lockReason;

    private Instant timeCancel;
}
