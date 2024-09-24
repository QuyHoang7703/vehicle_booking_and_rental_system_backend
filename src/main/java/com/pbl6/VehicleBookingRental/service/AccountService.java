package com.pbl6.VehicleBookingRental.service;

import org.springframework.stereotype.Service;

import com.pbl6.VehicleBookingRental.domain.Account;
import com.pbl6.VehicleBookingRental.repository.AccountRepository;
import java.util.List;
import java.util.Optional;

@Service    
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account handleCreateAccount(Account account) {
        return this.accountRepository.save(account);
    }
    
    public List<Account> fetchAllAccounts() {
        List<Account> accounts = this.accountRepository.findAll();
        return accounts;
    }

    public Account fetchAccountById(long id) {
        Optional<Account> optionalAccount =  this.accountRepository.findById(id);
        if(optionalAccount.isPresent()){
            return optionalAccount.get();
        }
        return null;
    }

    public Account handleUpdateAccount(Account reqAccount) {
        long id = reqAccount.getId();
        Account accountUpdate = this.fetchAccountById(id);
        if(accountUpdate != null) {
            accountUpdate.setPassword(reqAccount.getPassword());
            accountUpdate.setPhoneNumber(reqAccount.getPhoneNumber());
            accountUpdate.setGender(reqAccount.getGender());
            accountUpdate.setEmail(reqAccount.getEmail());
            accountUpdate.setAvatar(reqAccount.getAvatar());
            accountUpdate.setActive(reqAccount.isActive());
            accountUpdate.setLockReason(reqAccount.getLockReason());
        }
        return this.accountRepository.save(accountUpdate);
       
    }

    public void handleDeleteAccount(long id) {
        this.accountRepository.deleteById(id);
    }
    
}
