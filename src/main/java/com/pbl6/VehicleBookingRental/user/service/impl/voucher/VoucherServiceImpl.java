package com.pbl6.VehicleBookingRental.user.service.impl.voucher;

import com.pbl6.VehicleBookingRental.user.domain.Voucher.Voucher;
import com.pbl6.VehicleBookingRental.user.dto.Meta;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.voucher.ReqVoucherDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.voucher.ResVoucherDTO;
import com.pbl6.VehicleBookingRental.user.repository.voucher.VoucherRepository;
import com.pbl6.VehicleBookingRental.user.service.voucher.VoucherService;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoucherServiceImpl implements VoucherService {
    private final VoucherRepository voucherRepository;

    @Override
    public ResVoucherDTO createVoucher(ReqVoucherDTO reqVoucherDTO) {
        Voucher voucher = new Voucher();
        voucher.setName(reqVoucherDTO.getName());
        voucher.setDescription(reqVoucherDTO.getDescription());
        voucher.setStartDate(reqVoucherDTO.getStartDate());
        voucher.setEndDate(reqVoucherDTO.getEndDate());
        voucher.setVoucherPercentage(reqVoucherDTO.getVoucherPercentage());
        voucher.setMaxDiscountValue(reqVoucherDTO.getMaxDiscountValue());
        voucher.setRemainingQuantity(reqVoucherDTO.getRemainingQuantity());

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
                .description(voucher.getDescription())
                .startDate(voucher.getStartDate())
                .endDate(voucher.getEndDate())
                .voucherPercentage(voucher.getVoucherPercentage())
                .maxDiscountValue(voucher.getMaxDiscountValue())
                .minOrderValue(voucher.getMinOrderValue())
                .remainingQuantity(voucher.getRemainingQuantity())
                .build();
        return res;
    }


}
