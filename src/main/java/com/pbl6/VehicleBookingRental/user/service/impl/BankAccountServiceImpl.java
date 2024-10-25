package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.BankAccount;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.dto.request.bankAccount.ReqBankAccount;
import com.pbl6.VehicleBookingRental.user.repository.BankAccountRepository;
import com.pbl6.VehicleBookingRental.user.service.BankAccountService;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final SecurityUtil securityUtil;

    @Override
    public void createBankAccount(ReqBankAccount reqBankAccount, Account account) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber(reqBankAccount.getAccountNumber());
        bankAccount.setAccountHolderName(reqBankAccount.getAccountHolderName());
        bankAccount.setBankName(reqBankAccount.getBankName());
        bankAccount.setAccount(account);
        this.bankAccountRepository.save(bankAccount);
        log.info("Bank account created");
    }
}
