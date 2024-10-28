package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.domain.BankAccount;
import com.pbl6.VehicleBookingRental.user.dto.request.bankAccount.ReqBankAccount;
import com.pbl6.VehicleBookingRental.user.repository.BankAccountRepository;
import com.pbl6.VehicleBookingRental.user.util.EncryptionUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TestAuthorizationController {
    private final EncryptionUtil bankAccountEncryption;
    private final BankAccountRepository bankAccountRepository ;


    // Nhận khóa công khai và khóa riêng từ biến môi trường
    @Value("${rsa.public.key}")
    private String publicKeyString;

    @Value("${rsa.private.key}")
    private String privateKeyString;

    @GetMapping("/test")
    @PreAuthorize("hasRole('USER')")
    public String test() {
        log.info("AUthenication: " + SecurityContextHolder.getContext().getAuthentication().getName());
        return "test";
    }

    @GetMapping("/test2")
    @PreAuthorize("hasAuthority('Get all accounts v2')")
    public String test2() {
        log.info("Authenication after using jwt: " + SecurityContextHolder.getContext().getAuthentication());
        return "test2";
    }

    @PostMapping("test-encrypt-account-bank")
    public String testEncryptAccountBank(@RequestBody ReqBankAccount reqBankAccount) throws Exception {
        // Tạo khóa AES
        SecretKey secretKey = this.bankAccountEncryption.generateAESKey();

        // Mã hóa số tài khoản ngân hàng bằng khóa AES
        String encryptAccountNumber = this.bankAccountEncryption.encrypt(reqBankAccount.getAccountNumber(), secretKey);

        // Giải mã khóa công khai từ chuỗi
        PublicKey publicKey = bankAccountEncryption.getPublicKeyFromString(publicKeyString);

        // Mã hóa khóa AES bằng khóa công khai RSA
        String encryptedAESKey = bankAccountEncryption.encryptAESKey(secretKey, publicKey);

        // Lưu tài khoản vào cơ sở dữ liệu
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber(encryptAccountNumber);
        bankAccount.setAccountHolderName(reqBankAccount.getAccountHolderName());
        bankAccount.setBankName(reqBankAccount.getBankName());
        bankAccount.setAesKey(encryptedAESKey); // Lưu khóa AES đã mã hóa

        this.bankAccountRepository.save(bankAccount);
        return "oke";
    }

    @GetMapping("test-encrypt-account-bank/{id}")
    public String decryptAccountNumber(@PathVariable("id") int id) throws Exception {
        Optional<BankAccount> optionalBankAccount = this.bankAccountRepository.findById(id);
        if (!optionalBankAccount.isPresent()) {
            throw new EntityNotFoundException("Bank account not found with id: " + id);
        }

        BankAccount bankAccount = optionalBankAccount.get();

        try {
            // Giải mã khóa riêng từ chuỗi
            PrivateKey privateKey = bankAccountEncryption.getPrivateKeyFromString(privateKeyString);

            // Giải mã khóa AES bằng khóa riêng RSA
            SecretKey aesKey = bankAccountEncryption.decryptAESKey(bankAccount.getAesKey(), privateKey);

            // Giải mã số tài khoản ngân hàng bằng khóa AES
            return bankAccountEncryption.decrypt(bankAccount.getAccountNumber(), aesKey);
        } catch (Exception e) {
            throw new RuntimeException("Error during decryption process", e);
        }
    }

}
