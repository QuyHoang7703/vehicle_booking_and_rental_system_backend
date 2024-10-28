package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.BankAccount;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.dto.request.bankAccount.ReqBankAccount;
import com.pbl6.VehicleBookingRental.user.dto.response.bankAccount.ResBankAccountDTO;
import com.pbl6.VehicleBookingRental.user.repository.BankAccountRepository;
import com.pbl6.VehicleBookingRental.user.service.BankAccountService;
import com.pbl6.VehicleBookingRental.user.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final EncryptionUtil encryptionUtil;
    @Value("${rsa.public.key}")
    private String publicKeyString;

    @Value("${rsa.private.key}")
    private String privateKeyString;

    @Override
    public void createBankAccount(ReqBankAccount reqBankAccount, Account account) throws Exception {
        BankAccount bankAccount = new BankAccount();

        SecretKey secretKeyAES = this.encryptionUtil.generateAESKey();
        String encryptedAccountNumber = this.encryptionUtil.encrypt(reqBankAccount.getAccountNumber(), secretKeyAES);
        PublicKey publicKeyRSE = this.encryptionUtil.getPublicKeyFromString(publicKeyString);
        String encryptedAESKey = this.encryptionUtil.encryptAESKey(secretKeyAES, publicKeyRSE);

        bankAccount.setAccountNumber(encryptedAccountNumber);
        bankAccount.setAccountHolderName(reqBankAccount.getAccountHolderName());
        bankAccount.setBankName(reqBankAccount.getBankName());
        bankAccount.setAesKey(encryptedAESKey);
        bankAccount.setAccount(account);

        this.bankAccountRepository.save(bankAccount);
        log.info("Bank account created");

    }

    @Override
    public ResBankAccountDTO convertoResBankAccountDTO(Account account) throws Exception {
        ResBankAccountDTO resBankAccount = new ResBankAccountDTO();
        BankAccount bankAccount = account.getBankAccounts().get(0);
        PrivateKey privateKeyRSE = this.encryptionUtil.getPrivateKeyFromString(privateKeyString);
        SecretKey secretKeyAES = this.encryptionUtil.decryptAESKey(bankAccount.getAesKey(), privateKeyRSE);
        String decryptedAccountNumber = this.encryptionUtil.decrypt(bankAccount.getAccountNumber(), secretKeyAES);
        resBankAccount.setAccountNumber(decryptedAccountNumber);
        resBankAccount.setAccountHolderName(bankAccount.getAccountHolderName());
        resBankAccount.setBankName(bankAccount.getBankName());
        resBankAccount.setIdAccount(bankAccount.getAccount().getId());

        return resBankAccount;

    }
}
