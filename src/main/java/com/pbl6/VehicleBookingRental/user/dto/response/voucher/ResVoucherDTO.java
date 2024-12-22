package com.pbl6.VehicleBookingRental.user.dto.response.voucher;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResVoucherDTO {
    private int id;
    private String name;
//    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate endDate;
    private double voucherPercentage;
    private String maxDiscountValue;
    private String minOrderValue;
    private int remainingQuantity;
    private boolean expired;
    private String claimStatus;

}
