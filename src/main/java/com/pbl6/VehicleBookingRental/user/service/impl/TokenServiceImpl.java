package com.pbl6.VehicleBookingRental.user.service.impl;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRepository;
import com.pbl6.VehicleBookingRental.user.service.EmailService;
import com.pbl6.VehicleBookingRental.user.service.TokenService;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService{
    private final EmailService emailService;
    private final AccountRepository accountRepository;

    @Override
    public void createToken(String email) throws IdInvalidException, IOException {
        Optional<Account> optionalAccount = this.accountRepository.findByEmail(email);
        if(optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            if(!(account.isActive() || account.isVerified())) {
                throw new IdInvalidException("Tài khoản này đã bị kháo");
            }
      
            String token = UUID.randomUUID().toString();
            account.setToken(token);
            account.setExpirationTime(Instant.now().plus(3, ChronoUnit.MINUTES));
            

            this.accountRepository.save(account);
            this.sendRequestForgotPassword(email, account.getName(), token);
            System.out.println(">>>>>>> Token: " + token);
        }else{
            throw new IdInvalidException("Email này chưa được đăng ký trong hệ thống");
        }
    }

    @Override
    public boolean isValidToken(String token) {
        Optional<Account> optionalAccount = this.accountRepository.findByToken(token);
        if(optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            // boolean checkToken = account.getToken() != null && account.getToken().equals(token);
            boolean checkTokenExpired = Instant.now().isAfter(account.getExpirationTime());
            if(!checkTokenExpired) {
                // account.setToken(null);
                // account.setRefreshToken(null);
                this.accountRepository.save(account);
                return true;
            }
        }
        return false;
    }

    @Override
    public void sendRequestForgotPassword(String email, String name, String token) throws IOException {
        String subject = "Yêu cầu đặt lại mật khẩu";

        // Tạo liên kết chứa token để người dùng nhấn vào => chuyển đến fe xử lý
//        String resetPasswordLink = "http://localhost:3000/reset-password?token=" + token;
        String resetPasswordLink = "http://150.95.110.230:3000/reset-password?token=" + token;
        // Tạo nội dung email từ template HTML
        Context context = new Context();
        if(name==null) {
            context.setVariable("userName", email);
        }else{
            context.setVariable("userName", name);
        }
        context.setVariable("resetPasswordLink", resetPasswordLink);
        context.setVariable("cssContent", this.emailService.loadCssFromFile());

        this.emailService.sendEmail(email, subject, "reset_password_email", context);
    
    }


}
