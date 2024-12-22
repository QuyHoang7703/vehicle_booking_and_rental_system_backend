package com.pbl6.VehicleBookingRental.user.dto.request.voucher;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReqVoucherDTO {
    private String name;
//    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate endDate;
    private double voucherPercentage;
    private double maxDiscountValue;
    private double minOrderValue;
    private int remainingQuantity;
}
