package com.pbl6.VehicleBookingRental.user.dto.request.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqDeactivateAccount {
    private int id;
    private String lockReason;
//    private Instant deactivationDate;
}
