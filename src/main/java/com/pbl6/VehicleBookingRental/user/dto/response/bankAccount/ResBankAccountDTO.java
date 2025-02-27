package com.pbl6.VehicleBookingRental.user.dto.response.bankAccount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResBankAccountDTO {
    private int bankAccountId;
    private String accountNumber;
    private String accountHolderName;
    private String bankName;
    private int idAccount;
}
