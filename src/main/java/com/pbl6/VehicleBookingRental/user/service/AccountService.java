package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.repository.AccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.dto.Meta;

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
    
    public ResultPaginationDTO fetchAllAccounts(Specification<Account> spec, Pageable pageable) {
        Page<Account> pageAccount = this.accountRepository.findAll(spec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber());
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageAccount.getTotalPages());
        meta.setTotal(pageAccount.getTotalElements());
        res.setMeta(meta);
        res.setResult(pageAccount.getContent());

        return res;
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
            accountUpdate.setMale(reqAccount.getMale());
            accountUpdate.setEmail(reqAccount.getEmail());
//            accountUpdate.setAvatar(reqAccount.getAvatar());
            accountUpdate.setActive(reqAccount.isActive());
            accountUpdate.setLockReason(reqAccount.getLockReason());
        }
        return this.accountRepository.save(accountUpdate);
       
    }

    public void handleDeleteAccount(long id) {
        this.accountRepository.deleteById(id);
    }
    
    public Account handleGetAccountByUsername(String username) {
       return this.accountRepository.findByUsername(username);
    }
}
