package com.pbl6.VehicleBookingRental.user.service.impl.voucher;

import com.pbl6.VehicleBookingRental.user.domain.Voucher.AccountVoucher;
import com.pbl6.VehicleBookingRental.user.domain.Voucher.Voucher;
import com.pbl6.VehicleBookingRental.user.dto.Meta;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.voucher.ReqUpdateVoucherDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.voucher.ReqVoucherDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.voucher.ResVoucherDTO;
import com.pbl6.VehicleBookingRental.user.repository.voucher.AccountVoucherRepository;
import com.pbl6.VehicleBookingRental.user.repository.voucher.VoucherRepository;
import com.pbl6.VehicleBookingRental.user.service.voucher.VoucherService;
import com.pbl6.VehicleBookingRental.user.util.CurrencyFormatterUtil;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoucherServiceImpl implements VoucherService {
    private final VoucherRepository voucherRepository;
    private final AccountVoucherRepository accountVoucherRepository;

    @Override
    public ResVoucherDTO createVoucher(ReqVoucherDTO reqVoucherDTO) throws ApplicationException {
        if(reqVoucherDTO.getEndDate().isBefore(reqVoucherDTO.getStartDate())){
            throw new ApplicationException("End day of voucher must be after start day");
        }
        Voucher voucher = new Voucher();
        voucher.setName(reqVoucherDTO.getName());
        voucher.setStartDate(reqVoucherDTO.getStartDate());
        voucher.setEndDate(reqVoucherDTO.getEndDate());
        voucher.setVoucherPercentage(reqVoucherDTO.getVoucherPercentage());
        voucher.setMaxDiscountValue(reqVoucherDTO.getMaxDiscountValue());
        voucher.setMinOrderValue(reqVoucherDTO.getMinOrderValue());
        voucher.setRemainingQuantity(reqVoucherDTO.getRemainingQuantity());
        voucher.setExpired(false);

        return this.convertToResVoucherDTO(this.voucherRepository.save(voucher));
    }

    @Override
    public ResVoucherDTO getVoucher(int voucherId) throws IdInvalidException {
        Voucher voucher = this.voucherRepository.findById(voucherId)
                .orElseThrow(() -> new IdInvalidException("Voucher not found"));
        return this.convertToResVoucherDTO(voucher);
    }

    @Override
    public ResultPaginationDTO getAllVouchers(Specification<Voucher> specification, Pageable pageable) {
        Page<Voucher> voucherPage = this.voucherRepository.findAll(specification, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();

        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(voucherPage.getTotalPages());
        meta.setTotal(voucherPage.getTotalElements());

        res.setMeta(meta);

        List<ResVoucherDTO> resVoucherDTOS = voucherPage.getContent().stream()
                .map(this::convertToResVoucherDTO)
                .toList();
        res.setResult(resVoucherDTOS);

        return res;
    }

    @Override
    public ResVoucherDTO convertToResVoucherDTO(Voucher voucher) {
        ResVoucherDTO res = ResVoucherDTO.builder()
                .id(voucher.getId())
                .name(voucher.getName())
                .startDate(voucher.getStartDate())
                .endDate(voucher.getEndDate())
                .voucherPercentage(voucher.getVoucherPercentage())
                .maxDiscountValue(CurrencyFormatterUtil.formatToVND(voucher.getMaxDiscountValue()))
                .minOrderValue(CurrencyFormatterUtil.formatToVND(voucher.getMinOrderValue()))
                .remainingQuantity(voucher.getRemainingQuantity())
                .expired(voucher.isExpired())
                .build();
        return res;
    }

    @Override
    public void deleteVoucher(int voucherId) throws IdInvalidException, ApplicationException {
        Voucher voucher = this.voucherRepository.findById(voucherId)
                .orElseThrow(() -> new IdInvalidException("Voucher not found"));

        // User have already claim voucher -> Can't delete this voucher
        if(voucher.getAccountVouchers() != null && !voucher.getAccountVouchers().isEmpty()) {
            throw new ApplicationException("Someone already claimed this voucher");
        }

        this.voucherRepository.delete(voucher);

    }

    @Override
    public ResVoucherDTO updateVoucher(ReqUpdateVoucherDTO req) throws IdInvalidException, ApplicationException {
        Voucher voucherDb = this.voucherRepository.findById(req.getId())
                .orElseThrow(() -> new IdInvalidException("Voucher not found"));
        if(voucherDb.getAccountVouchers() != null && !voucherDb.getAccountVouchers().isEmpty()) {
            throw new ApplicationException("Someone already claimed this voucher");
        }

        voucherDb.setName(req.getName());
        voucherDb.setStartDate(req.getStartDate());
        voucherDb.setEndDate(req.getEndDate());
        voucherDb.setVoucherPercentage(req.getVoucherPercentage());
        voucherDb.setMaxDiscountValue(req.getMaxDiscountValue());
        voucherDb.setMinOrderValue(req.getMinOrderValue());
        voucherDb.setRemainingQuantity(req.getRemainingQuantity());

        return this.convertToResVoucherDTO(this.voucherRepository.save(voucherDb));
    }

    @Transactional
    @Scheduled(cron = "0 */2 * * * *")
    public void updateStatusOfVoucher() {
        LocalDate today = LocalDate.now();
        // Update status of voucher expired
        List<Voucher> vouchersToUpdate = voucherRepository.findByEndDateBefore(today);

        if (!vouchersToUpdate.isEmpty()) {
            vouchersToUpdate.forEach(voucher -> {
                voucher.setExpired(true);
                log.info("Voucher with ID: {} marked as expired.", voucher.getId());
                // Delete expired vouchers of accounts
                List<AccountVoucher> accountVouchers = voucher.getAccountVouchers();
                this.accountVoucherRepository.deleteAll(accountVouchers);
            });
            voucherRepository.saveAll(vouchersToUpdate);
        } else {
            log.debug("No vouchers found to be expired.");
        }
    }


}
