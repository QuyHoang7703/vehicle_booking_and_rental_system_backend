package com.pbl6.VehicleBookingRental.controller;

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

import com.pbl6.VehicleBookingRental.domain.Account;
import com.pbl6.VehicleBookingRental.domain.dto.ResAccountDTO;
import com.pbl6.VehicleBookingRental.domain.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.service.AccountService;
import com.pbl6.VehicleBookingRental.util.annotation.ApiMessage;
import com.pbl6.VehicleBookingRental.util.error.IdInValidException;
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

    @PostMapping("/accounts")
    @ApiMessage("Create a new account")
    public ResponseEntity<ResAccountDTO> createAccount(@RequestBody Account reqAccount) throws IdInValidException{
        if(this.accountService.checkAvailableUsername(reqAccount.getEmail()) || this.accountService.checkAvailableUsername(reqAccount.getPhoneNumber())){
            throw new IdInValidException("Email or Phone Number already exist, please use another one");
        }
        String hashPassword = this.passwordEncoder.encode(reqAccount.getPassword());
        reqAccount.setPassword(hashPassword);
        Account account = this.accountService.handleCreateAccount(reqAccount);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.accountService.convertToResAccount(account));

    }

    @GetMapping("/accounts")
    @ApiMessage("fetch all account success")
    public ResponseEntity<ResultPaginationDTO> getAllAccounts(@Filter Specification<Account> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.accountService.fetchAllAccounts(spec, pageable));
    }

    @PutMapping("/accounts")
    public ResponseEntity<ResAccountDTO> updateAccount(@RequestBody Account account) throws IdInValidException {
        if(this.accountService.fetchAccountById(account.getId()) ==null) {
            throw new IdInValidException("Account with id = " + account.getId() + " is not exist");
        }
        Account updateAccount = this.accountService.handleUpdateAccount(account);
        return ResponseEntity.status(HttpStatus.OK).body(this.accountService.convertToResAccount(updateAccount));
    }

    @DeleteMapping("/accounts/{id}")
    @ApiMessage("Deleted a account")
    public ResponseEntity<Void> deleteAccount(@PathVariable("id") long id) throws IdInValidException{
        Account account = this.accountService.fetchAccountById(id);
        if(account==null) {
            throw new IdInValidException("Account with id = " + id + " is not exist");
        }
        this.accountService.handleDeleteAccount(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/accounts/{id}")
    @ApiMessage("Updated a account")
    public ResponseEntity<ResAccountDTO> fetchAccountById(@PathVariable("id") long id) throws IdInValidException{
        Account account = this.accountService.fetchAccountById(id);
        if(account==null) {
            throw new IdInValidException("Account with id = " + id + " is not exist");
        }
       
        return ResponseEntity.status(HttpStatus.OK).body(this.accountService.convertToResAccount(account));
    } 



    
}
