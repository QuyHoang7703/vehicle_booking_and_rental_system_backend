package com.pbl6.VehicleBookingRental.user.dto.response.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.Instant;
@Data
public class ResDeactivateAccount {
    private String lockReason;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-mm-yyyy")
    private Instant timeCancel;
}
