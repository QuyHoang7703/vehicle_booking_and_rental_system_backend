package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.account.AccountRole;
import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import com.pbl6.VehicleBookingRental.user.dto.AccountInfo;
import com.pbl6.VehicleBookingRental.user.dto.Meta;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResBusinessPartnerDTO;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.account.RoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.businessPartner.BusinessPartnerRepository;
import com.pbl6.VehicleBookingRental.user.service.BusinessPartnerService;
import com.pbl6.VehicleBookingRental.user.util.constant.ApprovalStatusEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusinessPartnerServiceImpl implements BusinessPartnerService {
    private final BusinessPartnerRepository businessPartnerRepository;
    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;

    @Override
    public boolean isRegistered(int accountId, PartnerTypeEnum partnerType) {
        return this.businessPartnerRepository.existsByAccount_IdAndPartnerType(accountId, partnerType);
    }

    @Override
    public ResBusinessPartnerDTO convertToResBusinessPartnerDTO(BusinessPartner businessPartner) {
        ResBusinessPartnerDTO resBusinessPartnerDTO = new ResBusinessPartnerDTO();
        ResBusinessPartnerDTO.BusinessPartnerInfo businessPartnerInfo = new ResBusinessPartnerDTO.BusinessPartnerInfo();
        businessPartnerInfo.setId(businessPartner.getId());
        businessPartnerInfo.setBusinessName(businessPartner.getBusinessName());
        businessPartnerInfo.setEmailOfRepresentative(businessPartner.getEmailOfRepresentative());
        businessPartnerInfo.setNameOfRepresentative(businessPartner.getNameOfRepresentative());
        businessPartnerInfo.setPhoneOfRepresentative(businessPartner.getPhoneOfRepresentative());
        businessPartnerInfo.setAddress(businessPartner.getAddress());
        businessPartnerInfo.setPartnerType(businessPartner.getPartnerType());
        businessPartnerInfo.setApprovalStatus(businessPartner.getApprovalStatus());
        businessPartnerInfo.setAvatar(businessPartner.getAvatar());

        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setId(businessPartner.getAccount().getId());
        accountInfo.setEmail(businessPartner.getAccount().getEmail());
        businessPartnerInfo.setAccountInfo(accountInfo);

        resBusinessPartnerDTO.setBusinessInfo(businessPartnerInfo);
        return resBusinessPartnerDTO;
    }

    @Override
    public void verifyRegister(int id, PartnerTypeEnum partnerType) throws IdInvalidException {
        BusinessPartner businessPartner = this.businessPartnerRepository.findById(id)
                .orElseThrow(()-> new IdInvalidException("Id is invalid"));
        businessPartner.setApprovalStatus(ApprovalStatusEnum.APPROVED);
        this.businessPartnerRepository.save(businessPartner);
        Account account = businessPartner.getAccount();
        Role role = this.roleRepository.findByName(String.valueOf(partnerType))
                .orElseThrow(()-> new IdInvalidException("Role is invalid"));
        AccountRole accountRole = new AccountRole();
        accountRole.setRole(role);
        accountRole.setAccount(account);
        this.accountRoleRepository.save(accountRole);
    }

    @Override
    @Transactional
    public void cancelPartnership(int id, PartnerTypeEnum partnerType) throws IdInvalidException {
        BusinessPartner businessPartner = this.businessPartnerRepository.findById(id)
                .orElseThrow(()-> new IdInvalidException("Id is invalid"));
        businessPartner.setApprovalStatus(ApprovalStatusEnum.PENDING_APPROVAL);
        this.businessPartnerRepository.save(businessPartner);
        Account account = businessPartner.getAccount();
        List<AccountRole> accountRole = account.getAccountRole();
        Role role = this.roleRepository.findByName(String.valueOf(partnerType))
                .orElseThrow(()-> new IdInvalidException("Role is invalid"));

        this.accountRoleRepository.deleteAccountRolesByAccountAndRole(account, role);
    }

    @Override
    public ResultPaginationDTO handleFetchAllBusinessPartner(Specification<BusinessPartner> specification, Pageable pageable) {
        Page<BusinessPartner> businessPartnerPage = this.businessPartnerRepository.findAll(specification, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(businessPartnerPage.getTotalPages());
        meta.setTotal(businessPartnerPage.getTotalElements());
        resultPaginationDTO.setMeta(meta);

        List<ResBusinessPartnerDTO> resBusinessPartnerDTOList = businessPartnerPage.getContent().stream().map(item -> convertToResBusinessPartnerDTO(item))
                .collect(Collectors.toList());

        resultPaginationDTO.setResult(resBusinessPartnerDTOList);
        return resultPaginationDTO;
    }

    @Override
    public BusinessPartner fetchByIdAndPartnerType(int id, PartnerTypeEnum partnerType) {
        return this.businessPartnerRepository.findByIdAndPartnerType(id, partnerType).orElse(null);
    }


}
