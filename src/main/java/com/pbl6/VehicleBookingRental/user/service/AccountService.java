package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.account.AccountRole;
import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import com.pbl6.VehicleBookingRental.user.dto.Meta;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.account.ReqAccountInfoDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.account.ReqChangePasswordDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.register.ReqRegisterDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.account.ResAccountInfoDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.login.ResLoginDTO;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRepository;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.account.RoleRepository;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import java.util.Optional;
import java.util.regex.Pattern;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final RoleService roleSerivice;
    private final S3Service s3Service;


    public Account handleRegisterUser(ReqRegisterDTO registerDTO) throws IdInvalidException {
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

    public ResLoginDTO convertToResLoginDTO(Account account) {
        ResLoginDTO res = new ResLoginDTO();
        ResLoginDTO.AccountLogin accountLogin = new ResLoginDTO.AccountLogin();
        accountLogin.setId(account.getId());
        accountLogin.setUsername(account.getEmail());
        accountLogin.setName(account.getName());
        accountLogin.setAvatar(account.getAvatar());
//        accountLogin.setBirthDay(account.getBirthDay());
        accountLogin.setGender(account.getGender());
        accountLogin.setPhoneNumber(account.getPhoneNumber());
        accountLogin.setActive(account.isActive());
        List<String> roles = this.roleSerivice.getNameRolesByAccountID(account.getId());
        accountLogin.setRoles(roles);
        res.setAccountLogin(accountLogin);
        return res;
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
        accountInfoDTO.setActive(account.isActive());
//        if(account.getLockReason()!=null){
//
//        }

        List<String> roles = this.roleSerivice.getNameRolesByAccountID(account.getId());
        accountInfoDTO.setRoles(roles);

        resAccountDTO.setAccountInfo(accountInfoDTO);

        return resAccountDTO;
    }
    
    public ResultPaginationDTO fetchAllAccounts(Specification<Account> spec, Pageable pageable) {
        Page<Account> pageAccount = this.accountRepository.findAll(spec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageAccount.getTotalPages());
        meta.setTotal(pageAccount.getTotalElements());
        res.setMeta(meta);

        List<ResAccountInfoDTO> resAccountInfoDTOList = pageAccount.getContent().stream().map(item -> convertToResAccountInfoDTO(item))
                        .collect(Collectors.toList());
        res.setResult(resAccountInfoDTOList);
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

    public void handleDeactivateAccount(int id) {
        Account accountDb = this.fetchAccountById(id);
        accountDb.setActive(false);
        this.accountRepository.save(accountDb);
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

    public void verify(String email, String otp) throws IdInvalidException {
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
                throw new IdInvalidException("OTP is expired");
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

    public void handleChangePassword(ReqChangePasswordDTO changePasswordDTO) throws IdInvalidException {
        Optional<Account> optionalAccount = this.accountRepository.findByToken(changePasswordDTO.getToken());
        if(!optionalAccount.isPresent()) {
            throw new IdInvalidException("Token không hợp lệ");
        }
        if(!changePasswordDTO.getPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new IdInvalidException("Mật khẩu không trùng khớp");
        }
        Account account = optionalAccount.get();
       
        String decodedPassword = this.passwordEncoder.encode(changePasswordDTO.getPassword());
        account.setPassword(decodedPassword);
        account.setExpirationTime(null);
        account.setToken(null);
        this.accountRepository.save(account); 

    }

    public ResAccountInfoDTO updateAccountInfo(MultipartFile avatar, ReqAccountInfoDTO reqAccountInfoDTO) throws IdInvalidException {
//        Account account = this.accountRepository.findById(reqAccountInfoDTO.getId())
//                .orElseThrow(()-> new IdInvalidException("Account not found"));
        String email = SecurityUtil.getCurrentLogin().isPresent()?
                SecurityUtil.getCurrentLogin().get() : "";
        Account account = this.handleGetAccountByUsername(email);
        account.setName(reqAccountInfoDTO.getName());
        account.setBirthDay(reqAccountInfoDTO.getBirthDay());
        account.setGender(reqAccountInfoDTO.getGender());
        account.setPhoneNumber(reqAccountInfoDTO.getPhoneNumber());
        if(avatar != null) {
            String urlAvatar = this.s3Service.uploadFile(avatar);
            if(account.getAvatar()!=null) {
                this.s3Service.deleteFile(account.getAvatar());
            }
            account.setAvatar(urlAvatar);
        }
        this.accountRepository.save(account);

        return this.convertToResAccountInfoDTO(account);
    }





}
