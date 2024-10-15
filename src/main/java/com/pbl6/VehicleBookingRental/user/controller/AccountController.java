package com.pbl6.VehicleBookingRental.user.controller;


import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.account.ResAccountInfoDTO;

import com.pbl6.VehicleBookingRental.user.dto.response.login.ResLoginDTO;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pbl6.VehicleBookingRental.user.service.AccountService;
import com.pbl6.VehicleBookingRental.user.util.annotation.ApiMessage;
import com.pbl6.VehicleBookingRental.user.util.error.IdInValidException;

import com.turkraft.springfilter.boot.Filter;

@RestController
@RequestMapping("api/v1")
public class AccountController {
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    public AccountController(AccountService accountService, PasswordEncoder passwordEncoder) {
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/account")
    @ApiMessage("fetch account info")
    public ResponseEntity<ResAccountInfoDTO> getAccount() {
        String username = SecurityUtil.getCurrentLogin().isPresent()?
                SecurityUtil.getCurrentLogin().get():"";
        Account currentAccount = this.accountService.handleGetAccountByUsername(username);
        return ResponseEntity.status(HttpStatus.OK).body(this.accountService.convertToResAccountInfoDTO(currentAccount));
    }


    @GetMapping("/accounts")
    @ApiMessage("fetch all account success")
    public ResponseEntity<ResultPaginationDTO> getAllAccounts(@Filter Specification<Account> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.accountService.fetchAllAccounts(spec, pageable));
    }

    @PutMapping("/account")
    public ResponseEntity<ResAccountInfoDTO> updateAccount(@RequestBody Account account) throws IdInValidException {
        if(this.accountService.fetchAccountById(account.getId()) ==null) {
            throw new IdInValidException("Account with id = " + account.getId() + " is not exist");
        }
        Account updateAccount = this.accountService.handleUpdateAccount(account);
        return ResponseEntity.status(HttpStatus.OK).body(this.accountService.convertToResAccountInfoDTO(updateAccount));
    }

    @DeleteMapping("/accounts/{id}")
    @ApiMessage("Deleted a account")
    public ResponseEntity<Void> deleteAccount(@PathVariable("id") int id) throws IdInValidException{
        Account account = this.accountService.fetchAccountById(id);
        if(account==null) {
            throw new IdInValidException("Account with id = " + id + " is not exist");
        }
        this.accountService.handleDeleteAccount(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/accounts/{id}")
    @ApiMessage("Updated a account")
    public ResponseEntity<ResAccountInfoDTO> fetchAccountById(@PathVariable("id") int id) throws IdInValidException{
        Account account = this.accountService.fetchAccountById(id);
        if(account==null) {
            throw new IdInValidException("Account with id = " + id + " is not exist");
        }
       
        return ResponseEntity.status(HttpStatus.OK).body(this.accountService.convertToResAccountInfoDTO(account));
    } 

}
