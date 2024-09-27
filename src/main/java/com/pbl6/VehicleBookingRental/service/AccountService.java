package com.pbl6.VehicleBookingRental.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.pbl6.VehicleBookingRental.domain.Account;
import com.pbl6.VehicleBookingRental.domain.dto.Meta;
import com.pbl6.VehicleBookingRental.domain.dto.ResAccountDTO;
import com.pbl6.VehicleBookingRental.domain.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.repository.AccountRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        List<ResAccountDTO> list = pageAccount.getContent().stream().map(item -> new ResAccountDTO(
                                            item.getId(),
                                            item.getEmail(),
                                            item.getName(),
                                            item.getPhoneNumber(),
                                            item.getBirthDay(),
                                            item.isMale(),
                                            item.getAvatar(),
                                            item.isActive(),
                                            item.getLockReason(),
                                            item.getAccountType()))
                                        .collect(Collectors.toList());    

        res.setResult(list);
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
            // accountUpdate.setPassword(reqAccount.getPassword());
            accountUpdate.setMale(reqAccount.isMale());
            accountUpdate.setAvatar(reqAccount.getAvatar());
            accountUpdate.setActive(reqAccount.isActive());
            accountUpdate.setBirthDay(reqAccount.getBirthDay());
            // accountUpdate.setLockReason(reqAccount.getLockReason());
            accountUpdate.setAccountType(reqAccount.getAccountType());
        }
        return this.accountRepository.save(accountUpdate);
       
    }

    public void handleDeleteAccount(long id) {
        this.accountRepository.deleteById(id);
    }
    
    public Account handleGetAccountByUsername(String username) {
        return this.accountRepository.findByEmail(username)
                .or(() -> this.accountRepository.findByPhoneNumber(username))
                .orElse(null);
    }

    public ResAccountDTO convertToResAccount(Account account){
        ResAccountDTO resAccount = new ResAccountDTO();
        resAccount.setId(account.getId());
        resAccount.setEmail(account.getEmail());
        resAccount.setName(account.getName());
        resAccount.setPhoneNumber(account.getPhoneNumber());
        resAccount.setMale(account.isMale());
        resAccount.setAvatar(account.getAvatar());
        resAccount.setActive(account.isActive());
        // resAccount.setLockReason(account.getLockReason());
        resAccount.setBirthDay(account.getBirthDay());
        resAccount.setAccountType(account.getAccountType());

        return resAccount;
    }

    public boolean checkAvailableUsername(String username) {
        return accountRepository.existsByEmail(username) || accountRepository.existsByPhoneNumber(username);
    }
}
