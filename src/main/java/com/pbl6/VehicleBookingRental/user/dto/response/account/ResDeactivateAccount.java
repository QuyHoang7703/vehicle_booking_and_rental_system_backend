package com.pbl6.VehicleBookingRental.user.dto.response.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
public class ResDeactivateAccount {
    private String lockReason;

    private Instant timeCancel;

}
