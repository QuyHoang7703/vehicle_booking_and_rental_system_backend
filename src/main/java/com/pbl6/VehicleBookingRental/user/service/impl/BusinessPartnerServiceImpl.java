package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.account.AccountRole;
import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import com.pbl6.VehicleBookingRental.user.dto.AccountInfo;
import com.pbl6.VehicleBookingRental.user.dto.Meta;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqPartnerAction;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResBusinessPartnerDTO;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.account.RoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.businessPartner.BusinessPartnerRepository;
import com.pbl6.VehicleBookingRental.user.service.*;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
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

import java.io.IOException;
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
    private final AccountService accountService;
    private final ImageService imageService;
    private final BankAccountService bankAccountService;

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
            resBusinessPartnerDTO.setTimeBecomePartner(accountRole.getTimeBecomePartner());
            resBusinessPartnerDTO.setCancelReason(accountRole.getLockReason());
            resBusinessPartnerDTO.setTimeUpdate(accountRole.getTimeUpdate());

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
    public void verifyRegister(int id, PartnerTypeEnum partnerType) throws IdInvalidException, ApplicationException, IOException {
        // Change approval status in business partner form
        BusinessPartner businessPartner = this.businessPartnerRepository.findById(id)
                .orElseThrow(()-> new IdInvalidException("Id is invalid"));
        businessPartner.setApprovalStatus(ApprovalStatusEnum.APPROVED);
        this.businessPartnerRepository.save(businessPartner);

        // Update status active in AccountRole, check accountRole is available?
        String email = businessPartner.getAccount().getEmail();
        AccountRole accountRoleDb = this.accountRoleService.getAccountRole(email, String.valueOf(partnerType));
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

        // Send accepted request register to user
        Context context = new Context();
        context.setVariable("cssContent", this.emailService.loadCssFromFile());
        context.setVariable("email", email);
        String partner = String.valueOf(partnerType).equals("BUS_PARTNER") ? "nhà xe" : "cho thuê xe";
        context.setVariable("partner", partner);
        this.emailService.sendEmail(email, "Xác nhận trở thành đối tác", "verify_partner", context);
        log.info("Sent email verification");
    }

    @Override
    @Transactional
    public void cancelPartnership(ReqPartnerAction reqPartnerAction) throws Exception {
        BusinessPartner businessPartner = this.businessPartnerRepository.findById(reqPartnerAction.getFormRegisterId())
                .orElseThrow(()-> new IdInvalidException("Id is invalid"));
        businessPartner.setApprovalStatus(ApprovalStatusEnum.CANCEL);
        this.businessPartnerRepository.save(businessPartner);
        PartnerTypeEnum partnerType = businessPartner.getPartnerType();
        Account account = businessPartner.getAccount();
        String email = account.getEmail();
        AccountRole accountRole = this.accountRoleService.getAccountRole(email, String.valueOf(partnerType));
        accountRole.setActive(false);
        accountRole.setLockReason(reqPartnerAction.getReason());
        this.accountRoleRepository.save(accountRole);
        Context context = new Context();
        context.setVariable("cssContent", this.emailService.loadCssFromFile());
//        context.setVariable("partnerType", reqCancelPartner.getPartnerType());
        String partner = String.valueOf(partnerType).equals("BUS_PARTNER") ? "nhà xe" : "cho thuê xe";
        context.setVariable("partner", partner);
        context.setVariable("reasonCancel", reqPartnerAction.getReason());
        this.emailService.sendEmail(email, "Thông báo dừng việc hợp tác đối tác", "cancel_partner", context);

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

    @Override
    public BusinessPartner getCurrentBusinessPartner(PartnerTypeEnum partnerType) throws ApplicationException {
        String username = SecurityUtil.getCurrentLogin().isPresent()
                ? SecurityUtil.getCurrentLogin().get() : "";
        Account account = this.accountService.handleGetAccountByUsername(username);
        BusinessPartner businessPartner = this.businessPartnerRepository.findByAccount_IdAndPartnerType(account.getId(), partnerType)
                .orElseThrow(() -> new ApplicationException("Business Partner don't exist"));
        return businessPartner;
    }

    @Override
    public void refuseOrDeleteRegisterBusinessPartner(ReqPartnerAction reqPartnerAction) throws IdInvalidException, ApplicationException, IOException {
        BusinessPartner businessPartner = this.businessPartnerRepository.findById(reqPartnerAction.getFormRegisterId())
                .orElseThrow(() -> new IdInvalidException("Business partner not found"));

//        PartnerTypeEnum partnerType = reqPartnerAction.getPartnerType();
            PartnerTypeEnum partnerType = businessPartner.getPartnerType();
//        if(partnerType.equals(PartnerTypeEnum.BUS_PARTNER)){
//            this.imageService.deleteImages(businessPartner.getBusPartner().getId(), String.valueOf(partnerType));
//        }
//        else{
//            this.imageService.deleteImages(businessPartner.getCarRentalPartner().getId(), String.valueOf(partnerType));
//        }
        this.bankAccountService.deleteBankAccount(businessPartner.getAccount().getId(), businessPartner.getPartnerType());
        this.imageService.deleteImages(businessPartner.getId(), String.valueOf(partnerType));
        this.businessPartnerRepository.delete(businessPartner);

        // Delete accountRole in delete business partner
        AccountRole accountRole = this.accountRoleService.getAccountRole(businessPartner.getAccount().getEmail(), String.valueOf(partnerType));
        if(accountRole != null){
            this.accountRoleRepository.delete(accountRole);
        }
        if(reqPartnerAction.isRefuse()){
            Context context = new Context();
            context.setVariable("cssContent", this.emailService.loadCssFromFile());
            String email = businessPartner.getAccount().getEmail();
            String partner = String.valueOf(partnerType).equals("BUS_PARTNER") ? "nhà xe" : "cho thuê xe";
            context.setVariable("partner", partner);
            context.setVariable("reasonCancel", reqPartnerAction.getReason());
            this.emailService.sendEmail(email, "Thông báo từ chối hợp tác", "refuse_partner", context);
        }

//        if (reqPartnerAction.getPartnerType().equals(PartnerTypeEnum.BUS_PARTNER)) {
//            this.imageService.deleteImages(businessPartner.getBusPartner().getId(), String.valueOf(PartnerTypeEnum.BUS_PARTNER));
//            this.businessPartnerRepository.delete(businessPartner);
//            this.bankAccountService.deleteBankAccount(businessPartner.getAccount().getId(), PartnerTypeEnum.BUS_PARTNER);
//
//            // Delete accountRole in delete business partner
//            AccountRole accountRole = this.accountRoleService.getAccountRole(businessPartner.getAccount().getEmail(), String.valueOf(PartnerTypeEnum.BUS_PARTNER));
//            if(accountRole != null){
//                this.accountRoleRepository.delete(accountRole);
//            }
//
//        } else if (reqPartnerAction.getPartnerType().equals(PartnerTypeEnum.CAR_RENTAL_PARTNER)) {
//            this.imageService.deleteImages(businessPartner.getCarRentalPartner().getId(), String.valueOf(PartnerTypeEnum.CAR_RENTAL_PARTNER));
//            this.businessPartnerRepository.delete(businessPartner);
//            this.bankAccountService.deleteBankAccount(businessPartner.getAccount().getId(), PartnerTypeEnum.CAR_RENTAL_PARTNER);
//
//            // Delete accountRole in delete business partner
//            AccountRole accountRole = this.accountRoleService.getAccountRole(businessPartner.getAccount().getEmail(), String.valueOf(PartnerTypeEnum.CAR_RENTAL_PARTNER));
//            if(accountRole != null){
//                this.accountRoleRepository.delete(accountRole);
//            }
//        }

    }

    @Override
    public BusinessPartner getBusinessPartnerById(int id) throws IdInvalidException {
        return this.businessPartnerRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Business partner not found"));
    }
}
