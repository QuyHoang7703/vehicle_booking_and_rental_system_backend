package com.pbl6.VehicleBookingRental.user.controller;


import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.account.AccountRole;
import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import com.pbl6.VehicleBookingRental.user.dto.ResponseInfo;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.account.ReqAccountInfoDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.account.ReqChangePasswordDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.account.ReqDeactivateAccount;
import com.pbl6.VehicleBookingRental.user.dto.request.account.ReqUpdatePasswordDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.register.ReqRegisterDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.account.ResAccountInfoDTO;

import com.pbl6.VehicleBookingRental.user.dto.response.account.ResDeactivateAccount;
import com.pbl6.VehicleBookingRental.user.service.RoleService;
import com.pbl6.VehicleBookingRental.user.service.S3Service;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.pbl6.VehicleBookingRental.user.service.AccountService;
import com.pbl6.VehicleBookingRental.user.util.annotation.ApiMessage;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;

import com.turkraft.springfilter.boot.Filter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
@Slf4j
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/account")
    @ApiMessage("fetch account info")
    public ResponseEntity<ResAccountInfoDTO> getAccount() {
        String username = SecurityUtil.getCurrentLogin().isPresent()?
                SecurityUtil.getCurrentLogin().get():"";
        Account currentAccount = this.accountService.handleGetAccountByUsername(username);
        return ResponseEntity.status(HttpStatus.OK).body(this.accountService.convertToResAccountInfoDTO(currentAccount));
    }

    @GetMapping("/accounts")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiMessage("fetch all account success")
    @Transactional
    public ResponseEntity<ResultPaginationDTO> getAllAccounts(@Filter Specification<Account> spec, Pageable pageable) {
        Specification<Account> roleNotAdminSpec = (root, query, criteriaBuilder) ->{
            Join<Account, AccountRole> accounRoleJoin = root.join("accountRole");
            Join<AccountRole, Role> roleJoin = accounRoleJoin.join("role");
            return criteriaBuilder.notEqual(roleJoin.get("name"), "ADMIN");
        };

        Specification<Account> finalSpec = spec.and(roleNotAdminSpec);
        return ResponseEntity.status(HttpStatus.OK).body(this.accountService.fetchAllAccounts(finalSpec, pageable));
    }

    @PutMapping("/accounts/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiMessage("Activated the account")
    public ResponseEntity<Void> activateAccount(@RequestParam("idAccount") int id) throws Exception {
        Account account = this.accountService.fetchAccountById(id);
        if(account==null) {
            throw new IdInvalidException("Account with id = " + id + " is not exist");
        }
        this.accountService.handleActivateAccount(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PutMapping("/accounts/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiMessage("Deactivated the account")
    public ResponseEntity<Void> deactivateAccount(@RequestBody ReqDeactivateAccount reqDeactivateAccount) throws Exception {
        int idAccount = reqDeactivateAccount.getId();
        Account account = this.accountService.fetchAccountById(idAccount);
        if(account==null) {
            throw new IdInvalidException("Account with id = " + idAccount + " is not exist");
        }
        this.accountService.handleDeactivateAccount(reqDeactivateAccount);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PutMapping(value="/accounts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiMessage("Updated information for account")
    public ResponseEntity<ResAccountInfoDTO> updateInfoUser(@RequestParam(value="fileAvatar", required = false) MultipartFile file
            , @RequestPart("account_info") ReqAccountInfoDTO reqAccountInfoDTO) throws IdInvalidException {
        ResAccountInfoDTO resAccountInfoDTO  = this.accountService.updateAccountInfo(file, reqAccountInfoDTO);
        return ResponseEntity.ok(resAccountInfoDTO);
    }

    @PostMapping("accounts/register-admin")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiMessage("Registered account with role admin")
    public ResponseEntity<Void> registerAccountAdmin(@RequestBody ReqRegisterDTO registerDTO) throws ApplicationException {
        Account account = this.accountService.registerAccountAdmin(registerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PutMapping("accounts/update-password")
    @ApiMessage("Updated password for the account")
    public ResponseEntity<ResponseInfo<String>> updatePassword(@RequestBody ReqUpdatePasswordDTO reqUpdatePasswordDTO) throws ApplicationException {
        this.accountService.updatePassword(reqUpdatePasswordDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Đã thay đổi mật khẩu"));
    }

    @GetMapping("/account-detail-user")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiMessage("fetch account info user")
    public ResponseEntity<ResAccountInfoDTO> getAccountInfoUser(@RequestParam("username") String username) throws ApplicationException {
        Account currentAccount = this.accountService.handleGetAccountByUsername(username);
        if(currentAccount==null) {
            throw new ApplicationException("Username not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.accountService.convertToResAccountInfoDTO(currentAccount));
    }

    @GetMapping("accounts/reason-lock-account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResDeactivateAccount> getReasonLockAccount(@RequestParam("email") String email) throws ApplicationException {
        return ResponseEntity.status(HttpStatus.OK).body(this.accountService.getInfoDeactivatedAccount(email));
    }
}
