package com.pbl6.VehicleBookingRental.user.dto.request.voucher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqUpdateVoucherDTO {
    private int id;
    private String name;
    private int remainingQuantity;
}
