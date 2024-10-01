package com.pbl6.VehicleBookingRental.user.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.service.AccountService;
import com.pbl6.VehicleBookingRental.user.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

@RestController
public class AccountController {
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    public AccountController(AccountService accountService, PasswordEncoder passwordEncoder) {
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@RequestBody Account reqAccount){
        String hashPassword = this.passwordEncoder.encode(reqAccount.getPassword());
        reqAccount.setPassword(hashPassword);
        Account account = this.accountService.handleCreateAccount(reqAccount);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);

    }

    @GetMapping("/accounts")
    @ApiMessage("fetch all account success")
    public ResponseEntity<ResultPaginationDTO> getAllAccounts(@Filter Specification<Account> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.accountService.fetchAllAccounts(spec, pageable));
    }

    @PutMapping("/accounts")
    public ResponseEntity<Account> updateAccount(@RequestBody Account account) {
        return ResponseEntity.status(HttpStatus.OK).body(this.accountService.handleUpdateAccount(account));
    }

    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable("id") long id){
        this.accountService.handleDeleteAccount(id);
        return ResponseEntity.status(HttpStatus.OK).body("Đã xóa account");
    }
}
