package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.dto.response.voucher.ResVoucherDTO;
import com.pbl6.VehicleBookingRental.user.service.voucher.AccountVoucherService;
import com.pbl6.VehicleBookingRental.user.util.annotation.ApiMessage;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Slf4j
public class AccountVoucherController {
    private final AccountVoucherService accountVoucherService;

    @PostMapping("/vouchers/{voucherId}")
    @ApiMessage("Claim voucher successfully")
    public ResponseEntity<Void> claimVoucher(@PathVariable("voucherId") int voucherId) throws Exception {
        this.accountVoucherService.claimVoucher(voucherId);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @GetMapping("/vouchers/get-voucher-of-account")
    public ResponseEntity<List<ResVoucherDTO>> getSuitableVouchersOfAccountForOrder(@RequestParam("totalOrder") double totalOrder) throws ApplicationException {
        return ResponseEntity.status(HttpStatus.OK).body(this.accountVoucherService.getSuitableVouchersOfAccountForOrder(totalOrder));
    }

    @GetMapping("vouchers/available-voucher")
    public ResponseEntity<List<ResVoucherDTO>> getAvailableVoucherForUser() throws ApplicationException {
        return ResponseEntity.status(HttpStatus.OK).body(this.accountVoucherService.getAvailableVouchersForUser());
    }



}
