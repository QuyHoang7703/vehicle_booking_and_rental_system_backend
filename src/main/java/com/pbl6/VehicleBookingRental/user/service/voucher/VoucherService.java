package com.pbl6.VehicleBookingRental.user.service.voucher;

import com.pbl6.VehicleBookingRental.user.domain.Voucher.Voucher;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.voucher.ReqVoucherDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.voucher.ResVoucherDTO;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface VoucherService {
    ResVoucherDTO createVoucher(ReqVoucherDTO reqVoucherDTO) throws ApplicationException;
    ResVoucherDTO getVoucher(int voucherId) throws IdInvalidException;
    ResultPaginationDTO getAllVouchers(Specification<Voucher> specification, Pageable pageable);
    ResVoucherDTO convertToResVoucherDTO(Voucher voucher);
}
