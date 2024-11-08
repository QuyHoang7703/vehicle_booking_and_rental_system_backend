package com.pbl6.VehicleBookingRental.user.dto.request.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqUpdatePasswordDTO {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
