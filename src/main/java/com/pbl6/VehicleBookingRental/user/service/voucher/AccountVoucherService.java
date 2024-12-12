package com.pbl6.VehicleBookingRental.user.service.voucher;

import com.pbl6.VehicleBookingRental.user.dto.response.voucher.ResVoucherDTO;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;


import java.util.List;

public interface AccountVoucherService {
    void claimVoucher(int voucherId) throws IdInvalidException, ApplicationException;
    List<ResVoucherDTO> getSuitableVouchersOfAccountForOrder(double totalOrder) throws ApplicationException;
    List<ResVoucherDTO> getAvailableVouchersForUser() throws ApplicationException;
    void updateVoucherStatus(int accountId, int voucherId) throws IdInvalidException, ApplicationException;
    List<ResVoucherDTO> getAllVouchers() throws ApplicationException;
}
