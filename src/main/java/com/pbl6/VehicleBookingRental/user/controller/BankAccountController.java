package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.dto.request.bankAccount.ReqUpdateBankAccount;
import com.pbl6.VehicleBookingRental.user.dto.response.bankAccount.ResBankAccountDTO;
import com.pbl6.VehicleBookingRental.user.service.BankAccountService;
import com.pbl6.VehicleBookingRental.user.util.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Slf4j
public class BankAccountController {
    private final BankAccountService bankAccountService;
    @PutMapping("bankAccount")
    @ApiMessage("Updated this bank of account")
    public ResponseEntity<Void> updateBankAccount(@RequestBody ReqUpdateBankAccount reqUpdateBankAccount) throws Exception {
        this.bankAccountService.updateBankAccount(reqUpdateBankAccount);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
