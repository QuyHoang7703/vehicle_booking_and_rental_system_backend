package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.dto.*;
import com.pbl6.VehicleBookingRental.user.dto.request.account.ReqChangePasswordDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.register.ReqRegisterDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.account.ResAccountInfoDTO;
import com.pbl6.VehicleBookingRental.user.repository.AccountRepository;
import com.pbl6.VehicleBookingRental.user.util.error.IdInValidException;
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
public class AccountService {
    private final AccountRepository accountRepository;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AccountService(AccountRepository accountRepository,PasswordEncoder passwordEncoder, EmailService emailService) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public Account handleRegisterUser(ReqRegisterDTO registerDTO) throws IdInValidException {
        // Optional<Role> optionalRole = this.roleRepository.findById(registerDTO.getRoleId());
        // if(!optionalRole.isPresent()) {
        //     throw new IdInValidException("Role is invalid");
        // }
        // Role role = optionalRole.get();
        Account account = new Account();
        account.setEmail(registerDTO.getEmail());
        account.setPassword(registerDTO.getPassword());
        // account.setRole(role);
        account.setExpirationTime(Instant.now().plus(2, ChronoUnit.MINUTES));
        String otp = this.generateOTP();
        
        String otpDecoded = this.passwordEncoder.encode(otp);
        account.setOtp(otpDecoded);
        //Send OTP to email register
        this.sendVerificationEmail(registerDTO.getEmail(), otp);

        // Add attribute of user
        return this.accountRepository.save(account);

    }

    public ResAccountInfoDTO convertToResAccountInfoDTO(Account account){
        ResAccountInfoDTO resAccountDTO = new ResAccountInfoDTO();
        resAccountDTO.setId(account.getId());
        resAccountDTO.setEmail(account.getEmail());
        resAccountDTO.setName(account.getName());
        resAccountDTO.setBirthDay(account.getBirthDay());
        resAccountDTO.setPhoneNumber(account.getPhoneNumber());
        resAccountDTO.setGender(account.getGender());
        resAccountDTO.setAvatar(account.getAvatar());
        resAccountDTO.setActive(true);
    
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
        List<ResAccountInfoDTO> list = pageAccount.getContent().stream().map(item -> new ResAccountInfoDTO (
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
        }
        return this.accountRepository.save(accountUpdate);
       
    }

    public void handleDeleteAccount(int id) {
        this.accountRepository.deleteById(id);
    }
    
    public Account handleGetAccountByUsername(String username) {
        return this.accountRepository.findByEmail(username)
                    .or(() -> this.accountRepository.findByPhoneNumber(username))
                    .orElse(null);
      
    }

    public ResAccountInfoDTO convertToResAccount(Account account){
        Account currentAccount = this.handleGetAccountByUsername(account.getEmail());
        ResAccountInfoDTO resAccount = new ResAccountInfoDTO();
        resAccount.setId(account.getId());
        resAccount.setEmail(account.getEmail());
        resAccount.setName(account.getName());
        resAccount.setPhoneNumber(account.getPhoneNumber());
        resAccount.setGender(account.getGender());
        resAccount.setAvatar(account.getAvatar());
        resAccount.setActive(currentAccount.isActive());
        // resAccount.setLockReason(account.getLockReason());
        resAccount.setBirthDay(account.getBirthDay());

        return resAccount;
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
