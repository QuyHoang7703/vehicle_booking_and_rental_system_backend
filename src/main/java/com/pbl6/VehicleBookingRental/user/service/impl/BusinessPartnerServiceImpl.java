package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.account.AccountRole;
import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import com.pbl6.VehicleBookingRental.user.dto.AccountInfo;
import com.pbl6.VehicleBookingRental.user.dto.Meta;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqCancelPartner;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResBusinessPartnerDTO;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.account.RoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.businessPartner.BusinessPartnerRepository;
import com.pbl6.VehicleBookingRental.user.service.AccountRoleService;
import com.pbl6.VehicleBookingRental.user.service.BusinessPartnerService;
import com.pbl6.VehicleBookingRental.user.service.EmailService;
import com.pbl6.VehicleBookingRental.user.util.constant.ApprovalStatusEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessPartnerServiceImpl implements BusinessPartnerService {
    private final BusinessPartnerRepository businessPartnerRepository;
    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final AccountRoleService accountRoleService;
    private final EmailService emailService;

    @Override
    public boolean isRegistered(int accountId, PartnerTypeEnum partnerType) {
        return this.businessPartnerRepository.existsByAccount_IdAndPartnerType(accountId, partnerType);
    }

    @Override
    public ResBusinessPartnerDTO convertToResBusinessPartnerDTO(BusinessPartner businessPartner) throws ApplicationException {
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
//
        AccountRole accountRole = this.accountRoleService.getAccountRole(businessPartner.getAccount().getEmail()
                , String.valueOf(businessPartner.getPartnerType()));
        if(accountRole != null) {
            resBusinessPartnerDTO.setCancelReason(accountRole.getLockReason());
            resBusinessPartnerDTO.setTimeCancel(accountRole.getTimeCancel());

        }
        log.info("reason: " + resBusinessPartnerDTO.getCancelReason());

        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setId(businessPartner.getAccount().getId());
        accountInfo.setEmail(businessPartner.getAccount().getEmail());
        businessPartnerInfo.setAccountInfo(accountInfo);

        resBusinessPartnerDTO.setBusinessInfo(businessPartnerInfo);
        return resBusinessPartnerDTO;
    }

    @Override
    public void verifyRegister(int id, PartnerTypeEnum partnerType) throws IdInvalidException, ApplicationException {
        // Change approval status in business partner form
        BusinessPartner businessPartner = this.businessPartnerRepository.findById(id)
                .orElseThrow(()-> new IdInvalidException("Id is invalid"));
        businessPartner.setApprovalStatus(ApprovalStatusEnum.APPROVED);
        this.businessPartnerRepository.save(businessPartner);

        // Update status active in AccountRole, check accountRole is available?
        AccountRole accountRoleDb = this.accountRoleService.getAccountRole(businessPartner.getAccount().getEmail(), String.valueOf(partnerType));
        if (accountRoleDb != null) {
            accountRoleDb.setActive(true);
            accountRoleDb.setLockReason(null);
            this.accountRoleRepository.save(accountRoleDb);
        }else{
            Account account = businessPartner.getAccount();
            Role role = this.roleRepository.findByName(String.valueOf(partnerType))
                    .orElseThrow(()-> new IdInvalidException("Role is invalid"));
            AccountRole accountRole = new AccountRole();
            accountRole.setRole(role);
            accountRole.setAccount(account);
            accountRole.setActive(true);
            this.accountRoleRepository.save(accountRole);
        }
    }

    @Override
    @Transactional
    public void cancelPartnership(ReqCancelPartner reqCancelPartner) throws Exception {
        BusinessPartner businessPartner = this.businessPartnerRepository.findById(reqCancelPartner.getFormRegisterId())
                .orElseThrow(()-> new IdInvalidException("Id is invalid"));
        businessPartner.setApprovalStatus(ApprovalStatusEnum.PENDING_APPROVAL);
        this.businessPartnerRepository.save(businessPartner);

        Account account = businessPartner.getAccount();
        AccountRole accountRole = this.accountRoleService.getAccountRole(account.getEmail(), String.valueOf(reqCancelPartner.getPartnerType()));
        accountRole.setActive(false);
        accountRole.setLockReason(reqCancelPartner.getReasonCancel());
        this.accountRoleRepository.save(accountRole);
        Context context = new Context();
        context.setVariable("cssContent", this.emailService.loadCssFromFile());
//        context.setVariable("partnerType", reqCancelPartner.getPartnerType());
        context.setVariable("reasonCancel", reqCancelPartner.getReasonCancel());
        this.emailService.sendEmail(account.getEmail(), "Thông báo dừng việc hợp tác đối tác", "cancel_partner", context);

//        this.accountRoleRepository.deleteAccountRolesByAccountAndRole(account, role);
//        this.accountRoleRepository.deleteAccountRolesByAccount_IdAndRole_Id(account.getId(), role.getId());
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

        List<ResBusinessPartnerDTO> resBusinessPartnerDTOList = businessPartnerPage.getContent().stream().map(item -> {
                    try {
                        return convertToResBusinessPartnerDTO(item);
                    } catch (ApplicationException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        resultPaginationDTO.setResult(resBusinessPartnerDTOList);
        return resultPaginationDTO;
    }

    @Override
    public BusinessPartner fetchByIdAndPartnerType(int id, PartnerTypeEnum partnerType) {
        return this.businessPartnerRepository.findByIdAndPartnerType(id, partnerType).orElse(null);
    }


}
