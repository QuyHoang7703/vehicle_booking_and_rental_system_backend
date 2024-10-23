package com.pbl6.VehicleBookingRental.user.controller;


import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.account.AccountRole;
import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.account.ReqAccountInfoDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.account.ResAccountInfoDTO;

import com.pbl6.VehicleBookingRental.user.service.RoleService;
import com.pbl6.VehicleBookingRental.user.service.S3Service;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
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
    private final S3Service s3Service;
    private final RoleService roleService;
    private final FilterSpecificationConverter filterSpecificationConverter;
    private final FilterBuilder filterBuilder;

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

//    @PutMapping("/account")
//    public ResponseEntity<ResAccountInfoDTO> updateAccount(@RequestBody Account account) throws IdInvalidException {
//        if(this.accountService.fetchAccountById(account.getId()) ==null) {
//            throw new IdInvalidException("Account with id = " + account.getId() + " is not exist");
//        }
//        Account updateAccount = this.accountService.handleUpdateAccount(account);
//        return ResponseEntity.status(HttpStatus.OK).body(this.accountService.convertToResAccountInfoDTO(updateAccount));
//    }

    @PutMapping("/accounts")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiMessage("Deactivate the account")
    public ResponseEntity<Void> deactivateAccount(@RequestParam("idAccount") int id) throws IdInvalidException {
        Account account = this.accountService.fetchAccountById(id);
        if(account==null) {
            throw new IdInvalidException("Account with id = " + id + " is not exist");
        }
        this.accountService.handleDeactivateAccount(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PutMapping(value="/accounts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiMessage("Updated information for account")
    public ResponseEntity<ResAccountInfoDTO> updateInfoUser(@RequestParam(value="fileAvatar", required = false) MultipartFile file
            , @RequestPart("account_info") ReqAccountInfoDTO reqAccountInfoDTO) throws IdInvalidException {
        ResAccountInfoDTO resAccountInfoDTO  = this.accountService.updateAccountInfo(file, reqAccountInfoDTO);
        return ResponseEntity.ok(resAccountInfoDTO);
    }

//    @PostMapping("/auth/upload-multiple")
//    public ResponseEntity<List<String>> uploadMultipleFiles(@RequestParam("files") List<MultipartFile> files) {
//        List<String> urls = this.s3Service.uploadFiles(files);
//        return ResponseEntity.ok(urls);
//    }

}
