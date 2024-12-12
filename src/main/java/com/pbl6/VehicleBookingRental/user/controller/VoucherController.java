package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.domain.Voucher.Voucher;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.voucher.ReqVoucherDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.voucher.ResVoucherDTO;
import com.pbl6.VehicleBookingRental.user.service.voucher.VoucherService;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
@PreAuthorize("hasRole('ADMIN')")
public class VoucherController {
    private final VoucherService voucherService;

    @PostMapping("/vouchers")
    public ResponseEntity<ResVoucherDTO> createVoucher(@RequestBody ReqVoucherDTO reqVoucherDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.voucherService.createVoucher(reqVoucherDTO));
    }

    @GetMapping("/vouchers/{voucherId}")
    public ResponseEntity<ResVoucherDTO> getVoucher(@PathVariable("voucherId") int voucherId) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.OK).body(this.voucherService.getVoucher(voucherId));
    }

    @GetMapping("vouchers")
    public ResponseEntity<ResultPaginationDTO> getAllVouchers(@Filter Specification<Voucher> spec,
                                                              @PageableDefault(size = 3) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.voucherService.getAllVouchers(spec, pageable));
    }

}
