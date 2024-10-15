package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.account.AccountRole;
import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import com.pbl6.VehicleBookingRental.user.dto.*;
import com.pbl6.VehicleBookingRental.user.dto.request.account.ReqChangePasswordDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.register.ReqRegisterDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.account.ResAccountInfoDTO;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRepository;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.account.RoleRepository;
import com.pbl6.VehicleBookingRental.user.util.error.IdInValidException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final AccountRoleSerivice accountRoleSerivice;



    public Account handleRegisterUser(ReqRegisterDTO registerDTO) throws IdInValidException {
        Account account = new Account();
        account.setEmail(registerDTO.getEmail());
        account.setPassword(registerDTO.getPassword());
        account.setExpirationTime(Instant.now().plus(2, ChronoUnit.MINUTES));
        String otp = this.generateOTP();
        String otpDecoded = this.passwordEncoder.encode(otp);
        account.setOtp(otpDecoded);
        //Send OTP to email register
        this.sendVerificationEmail(registerDTO.getEmail(), otp);

        return this.accountRepository.save(account);

    }

    public ResAccountInfoDTO convertToResAccountInfoDTO(Account account){
        ResAccountInfoDTO resAccountDTO = new ResAccountInfoDTO();

        ResAccountInfoDTO.AccountInfo accountInfoDTO = new ResAccountInfoDTO.AccountInfo();
        accountInfoDTO.setId(account.getId());
        accountInfoDTO.setEmail(account.getEmail());
        accountInfoDTO.setName(account.getName());
        accountInfoDTO.setBirthDay(account.getBirthDay());
        accountInfoDTO.setPhoneNumber(account.getPhoneNumber());
        accountInfoDTO.setGender(account.getGender());
        accountInfoDTO.setAvatar(account.getAvatar());
        accountInfoDTO.setActive(true);

        resAccountDTO.setAccountInfo(accountInfoDTO);

        return resAccountDTO;
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
//        List<Role> roles = this.accountRoleSerivice.getAccountRolesByAccountID()
        List<ResAccountInfoDTO.AccountInfo> list = pageAccount.getContent().stream().map(item -> new ResAccountInfoDTO.AccountInfo(
                                            item.getId(),
                                            item.getEmail(),
                                            item.getName(),
                                            item.getPhoneNumber(),
                                            item.getBirthDay(),
                                            item.getGender(),
                                            item.getAvatar(),
                                            item.isActive()))
                                            // item.getLockReason()))
                                        .collect(Collectors.toList());

        res.setResult(list);
        return res;
    }

    public Account fetchAccountById(int id) {
        Optional<Account> optionalAccount =  this.accountRepository.findById(id);
        if(optionalAccount.isPresent()){
            return optionalAccount.get();
        }
        return null;
    }

    public Account handleUpdateAccount(Account reqAccount) {
        int id = reqAccount.getId();
        Account accountUpdate = this.fetchAccountById(id);
        if(accountUpdate != null) {
            accountUpdate.setName(reqAccount.getName());
            accountUpdate.setPhoneNumber(reqAccount.getPhoneNumber());
            accountUpdate.setGender(reqAccount.getGender());
            accountUpdate.setAvatar(reqAccount.getAvatar());
            accountUpdate.setBirthDay(reqAccount.getBirthDay());
            // accountUpdate.setLockReason(reqAccount.getLockReason());
             return this.accountRepository.save(accountUpdate);
        }
        return null;

    }

    public void handleDeleteAccount(int id) {
        this.accountRepository.deleteById(id);
    }
    
    public Account handleGetAccountByUsername(String username) {
        return this.accountRepository.findByEmail(username)
                    .or(() -> this.accountRepository.findByPhoneNumber(username))
                    .orElse(null);
      
    }


    public boolean checkAvailableUsername(String username) {
        return accountRepository.existsByEmail(username) || accountRepository.existsByPhoneNumber(username);
    }

    public void updateRefreshToken(String refreshToken, String username) {
        Account currentAccount = this.handleGetAccountByUsername(username);
        if(currentAccount != null ){
            currentAccount.setRefreshToken(refreshToken);
            this.accountRepository.save(currentAccount);
        }
    }

    public Account fetchAccountByRefreshTokenAndEmail(String refreshToken, String email) {
        Optional<Account> optionalAccount = this.accountRepository.findByRefreshTokenAndEmail(refreshToken, email);
        if(optionalAccount.isPresent()) {
            return optionalAccount.get();
        }
        return null;
    }

    public Account fetchAccountByRefreshTokenAndPhoneNumber(String refreshToken, String phoneNumber) {
        Optional<Account> optionalAccount = this.accountRepository.findByRefreshTokenAndPhoneNumber(refreshToken, phoneNumber);
        if(optionalAccount.isPresent()) {
            return optionalAccount.get();
        }
        return null;
    }

    public boolean isEmail(String username) {
        return EMAIL_PATTERN.matcher(username).matches();
    }

    private String generateOTP() {
        Random random = new Random();
        int otpValue = 100000 + random.nextInt(900000);
        
        return String.valueOf(otpValue);
    }

    public void sendVerificationEmail(String email, String otp) {
        String subject = "Email verification";
        String body = "Your verification OTP is: " + otp;
        this.emailService.sendEmail(email, subject, body);
    }

    public void verify(String email, String otp) throws IdInValidException{
        Optional<Account> optionalAccount = this.accountRepository.findByEmail(email);
        if(optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            boolean isValidOtp = this.passwordEncoder.matches(otp, account.getOtp());
            boolean isOtpExpired = Instant.now().isAfter(account.getExpirationTime());
            if(isValidOtp && !isOtpExpired){
                account.setVerified(true);
                account.setOtp(null);
                account.setExpirationTime(null);
                this.accountRepository.save(account);
                Role userRole = this.roleRepository.findByName("USER")
                        .orElseThrow(()-> new RuntimeException("Role not found"));
                AccountRole accountRole = new AccountRole();
                accountRole.setAccount(account);
                accountRole.setRole(userRole);
                this.accountRoleRepository.save(accountRole);
            }else {
                throw new IdInValidException("OTP is expired");
            }
        }
    }

    public void resendOtp(String email) {
        String otp = this.generateOTP();
        String otpDecoded = this.passwordEncoder.encode(otp);
        Optional<Account> optionalAccount = this.accountRepository.findByEmail(email);
        if(optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            account.setOtp(otpDecoded);
            account.setExpirationTime(Instant.now().plus(2, ChronoUnit.MINUTES));
            this.accountRepository.save(account);
        }
        this.sendVerificationEmail(email, otp);
    }
    
    public void handleLoginWithGoogle(Account account) {
     
         this.accountRepository.save(account);
    }

    public boolean isActiveAccount(String email) {
        Account account = this.handleGetAccountByUsername(email);

        return account.isActive();
    }

    public void handleChangePassword(ReqChangePasswordDTO changePasswordDTO) throws IdInValidException {
        Optional<Account> optionalAccount = this.accountRepository.findByToken(changePasswordDTO.getToken());
        if(!optionalAccount.isPresent()) {
            throw new IdInValidException("Token không hợp lệ");
        }
        if(!changePasswordDTO.getPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new IdInValidException("Mật khẩu không trùng khớp");
        }
        Account account = optionalAccount.get();
       
        String decodedPassword = this.passwordEncoder.encode(changePasswordDTO.getPassword());
        account.setPassword(decodedPassword);
        account.setExpirationTime(null);
        account.setToken(null);
        this.accountRepository.save(account); 

    }





}
