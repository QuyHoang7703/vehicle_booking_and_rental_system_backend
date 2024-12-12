package com.pbl6.VehicleBookingRental.user.service.impl.voucher;

import com.pbl6.VehicleBookingRental.user.domain.Voucher.AccountVoucher;
import com.pbl6.VehicleBookingRental.user.domain.Voucher.Voucher;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.voucher.ResVoucherDTO;
import com.pbl6.VehicleBookingRental.user.repository.voucher.AccountVoucherRepository;
import com.pbl6.VehicleBookingRental.user.repository.voucher.VoucherRepository;
import com.pbl6.VehicleBookingRental.user.service.AccountService;
import com.pbl6.VehicleBookingRental.user.service.voucher.AccountVoucherService;
import com.pbl6.VehicleBookingRental.user.service.voucher.VoucherService;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import com.pbl6.VehicleBookingRental.user.util.constant.VoucherStatusEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountVoucherServiceImpl implements AccountVoucherService {
    private final AccountVoucherRepository accountVoucherRepository;
    private final VoucherRepository voucherRepository;
    private final AccountService accountService;
    private final VoucherService voucherService;

    @Override
    @Transactional
    public void claimVoucher(int voucherId) throws IdInvalidException, ApplicationException {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new IdInvalidException("Voucher not found"));

        String email = SecurityUtil.getCurrentLogin().isPresent() ? SecurityUtil.getCurrentLogin().get() : null;
        if(email == null) {
            throw new ApplicationException("Access token is expired or invalid");
        }
        Account currentAccount = this.accountService.handleGetAccountByUsername(email);

        AccountVoucher accountVoucher = new AccountVoucher();
        accountVoucher.setAccount(currentAccount);
        accountVoucher.setVoucher(voucher);
        accountVoucher.setStatus(VoucherStatusEnum.UNUSED);
        this.accountVoucherRepository.save(accountVoucher);

        voucher.setRemainingQuantity(voucher.getRemainingQuantity() - 1);
        this.voucherRepository.save(voucher);

    }

    @Override
    public List<ResVoucherDTO> getSuitableVouchersOfAccountForOrder(double totalOrder) throws ApplicationException {
        String email = SecurityUtil.getCurrentLogin().isPresent() ? SecurityUtil.getCurrentLogin().get() : null;
        if(email == null) {
            throw new ApplicationException("Access token is expired or invalid");
        }
        Account currentAccount = this.accountService.handleGetAccountByUsername(email);

        List<Voucher> vouchers = this.voucherRepository.getSuitableVoucherOfAccountForOrder(currentAccount.getId(), totalOrder);
        log.info("Voucher of account: " + vouchers.size());

        List<ResVoucherDTO> res = vouchers.stream()
                .map(voucher -> this.voucherService.convertToResVoucherDTO(voucher))
                .toList();

        return res;
    }

    @Override
    public List<ResVoucherDTO> getAvailableVouchersForUser() throws ApplicationException {
        String email = SecurityUtil.getCurrentLogin().isPresent() ? SecurityUtil.getCurrentLogin().get() : null;
        if(email == null) {
            throw new ApplicationException("Access token is expired or invalid");
        }
        Account currentAccount = this.accountService.handleGetAccountByUsername(email);

        List<Voucher> vouchers = this.voucherRepository.getAvailableVoucher(currentAccount.getId());

        List<ResVoucherDTO> res = vouchers.stream()
                .map(voucher -> this.voucherService.convertToResVoucherDTO(voucher))
                .toList();

        return res;
    }

    @Override
    public void updateVoucherStatus(int accountId, int voucherId) throws IdInvalidException {

        AccountVoucher accountVoucher = this.accountVoucherRepository.findByAccount_IdAndVoucher_Id(accountId, voucherId)
                .orElseThrow(() -> new IdInvalidException("Account Voucher not found"));

        accountVoucher.setStatus(VoucherStatusEnum.USED);
        this.accountVoucherRepository.save(accountVoucher);
    }


}
