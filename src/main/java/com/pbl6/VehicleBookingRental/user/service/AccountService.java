package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.Voucher.AccountVoucher;
import com.pbl6.VehicleBookingRental.user.domain.Voucher.Voucher;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.account.AccountRole;
import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import com.pbl6.VehicleBookingRental.user.dto.Meta;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.account.ReqAccountInfoDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.account.ReqChangePasswordDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.account.ReqDeactivateAccount;
import com.pbl6.VehicleBookingRental.user.dto.request.account.ReqUpdatePasswordDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.register.ReqRegisterDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.account.ResAccountInfoDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.account.ResDeactivateAccount;
import com.pbl6.VehicleBookingRental.user.dto.response.login.ResLoginDTO;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRepository;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.account.RoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.businessPartner.BusinessPartnerRepository;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
//    private final CloudinaryService cloudinaryService;
    private final AccountRoleService accountRoleService;
    private final CloudinaryService cloudinaryService;
    private final BusinessPartnerRepository businessPartnerRepository;


    public Account handleRegisterUser(ReqRegisterDTO registerDTO) throws IdInvalidException, IOException {
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
        accountLogin.setEmail(account.getEmail());
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

        BusinessPartner formRegisterBusPartner = this.businessPartnerRepository.findByAccount_IdAndPartnerType(account.getId(), PartnerTypeEnum.BUS_PARTNER)
                        .orElse(null);
        if(formRegisterBusPartner!=null){
            accountInfoDTO.setFormRegisterBusPartnerId(formRegisterBusPartner.getId());
        }

        BusinessPartner formRegisterCarRentalPartner = this.businessPartnerRepository.findByAccount_IdAndPartnerType(account.getId(), PartnerTypeEnum.CAR_RENTAL_PARTNER)
                .orElse(null);
        if(formRegisterCarRentalPartner!=null){
            accountInfoDTO.setFormRegisterCarRentalPartnerId(formRegisterCarRentalPartner.getId());
        }

        resAccountDTO.setAccountInfo(accountInfoDTO);

        return resAccountDTO;
    }
    
    public ResultPaginationDTO fetchAllAccounts(Specification<Account> spec, Pageable pageable) {
        Specification<Account> roleNotAdminSpec = (root, query, criteriaBuilder) -> {
            // Join the AccountRole and Role entities
            Join<Account, AccountRole> accounRoleJoin = root.join("accountRole");
            Join<AccountRole, Role> roleJoin = accounRoleJoin.join("role");

            // Apply the "not equal" condition for the role name
            Predicate userPredicate = criteriaBuilder.equal(roleJoin.get("name"), "USER");

            return userPredicate;
        };


        Specification<Account> finalSpec = spec.and(roleNotAdminSpec);
//        Specification<Account> finalSpec = (spec != null) ? spec.and(roleNotAdminSpec) : roleNotAdminSpec;

        Page<Account> pageAccount = this.accountRepository.findAll(spec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setPages(pageAccount.getTotalPages());
        meta.setTotal(pageAccount.getTotalElements());

//        Page<Account> pageAccount = this.accountRepository.findAll(finalSpec, pageable);
        meta.setCurrentPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        res.setMeta(meta);

        List<ResAccountInfoDTO> resAccountInfoDTOList = pageAccount.getContent().stream()

                .map(item -> convertToResAccountInfoDTO(item))
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

    public Account handleUpdateAccount(MultipartFile file, ReqAccountInfoDTO reqAccountInfoDTO) throws IOException {
        Account accountUpdate = this.handleGetAccountByUsername(reqAccountInfoDTO.getUsername());
        if(accountUpdate == null) {
            throw new UsernameNotFoundException("Username not found");
        }
        accountUpdate.setName(reqAccountInfoDTO.getName());
        accountUpdate.setPhoneNumber(reqAccountInfoDTO.getPhoneNumber());
        accountUpdate.setGender(reqAccountInfoDTO.getGender());
        accountUpdate.setBirthDay(reqAccountInfoDTO.getBirthDay());
        // accountUpdate.setLockReason(reqAccount.getLockReason());
        if(file != null) {
            String urlAvatar = this.cloudinaryService.uploadFile(file);
//            this.cloudinaryService.deleteFile(account.getAvatar());
            accountUpdate.setAvatar(urlAvatar);

        }
         return this.accountRepository.save(accountUpdate);
    }

    public void handleActivateAccount(int id) throws ApplicationException {
        Account accountDb = this.fetchAccountById(id);
        if(!accountDb.isActive()){
            accountDb.setActive(true);
            this.accountRepository.save(accountDb);
            AccountRole accountRole = this.accountRoleService.getAccountRole(accountDb.getEmail(), "USER");
            accountRole.setLockReason(null);
            this.accountRoleRepository.save(accountRole);
        }
        else{
            throw new ApplicationException("You already activated this account");
        }
    }

    public void handleDeactivateAccount(ReqDeactivateAccount reqDeactivateAccount) throws Exception {
        Account accountDb = this.fetchAccountById(reqDeactivateAccount.getId());
        if(accountDb.isActive()){
            accountDb.setActive(false);
            this.accountRepository.save(accountDb);
            Role roleUser = this.roleRepository.findByName("USER")
                    .orElseThrow(() -> new ApplicationException("Role not found"));
            AccountRole accountRole = this.accountRoleRepository.findByAccount_IdAndRole_Id(accountDb.getId(), roleUser.getId())
                    .orElseThrow(() -> new ApplicationException("AccountRole not found"));
            accountRole.setLockReason(reqDeactivateAccount.getLockReason());
            this.accountRoleRepository.save(accountRole);
            Context context = new Context();
            context.setVariable("cssContent", this.emailService.loadCssFromFile());
            context.setVariable("lockReason", reqDeactivateAccount.getLockReason());
            this.emailService.sendEmail(accountDb.getEmail(), "Thông báo khóa tài khoản người dùng", "deactivate_account", context);
        }else{
            throw new ApplicationException("You already lock this account");
        }

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

    public void sendVerificationEmail(String email, String otp) throws IOException {
        String subject = "Email verification";
        String body = "Your verification OTP is: " + otp;
        Context context = new Context();
        context.setVariable("email", email);
        context.setVariable("otp", otp);
        context.setVariable("cssContent", this.emailService.loadCssFromFile());
        this.emailService.sendEmail(email, subject, "otp_email", context);
//        this.emailService.sendEmail(email, subject, body);
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
                accountRole.setActive(true);
                this.accountRoleRepository.save(accountRole);
            }else {
                throw new IdInvalidException("OTP is expired");
            }
        }
    }

    public void resendOtp(String email) throws IOException {
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

    public ResAccountInfoDTO updateAccountInfo(MultipartFile avatar, ReqAccountInfoDTO reqAccountInfoDTO) throws IOException {
        String username = SecurityUtil.getCurrentLogin().isPresent()?
                SecurityUtil.getCurrentLogin().get() : "";
        Account account = this.handleGetAccountByUsername(username);
        account.setName(reqAccountInfoDTO.getName());
        account.setBirthDay(reqAccountInfoDTO.getBirthDay());
        account.setGender(reqAccountInfoDTO.getGender());
        account.setPhoneNumber(reqAccountInfoDTO.getPhoneNumber());
        if(avatar != null) {
            String urlAvatar = this.cloudinaryService.uploadFile(avatar);
            if(account.getAvatar()!=null) {
                this.cloudinaryService.deleteFile(account.getAvatar());
            }
            account.setAvatar(urlAvatar);
        }
        this.accountRepository.save(account);

        return this.convertToResAccountInfoDTO(account);
    }

    public void createAccountWithRole(Account account, String roleName) throws ApplicationException {
        Role role = this.roleRepository.findByName(roleName)
                .orElseThrow(()-> new ApplicationException("Role not found"));
        AccountRole accountRole = new AccountRole();
        accountRole.setAccount(account);
        accountRole.setRole(role);
        this.accountRoleRepository.save(accountRole);
    }

    public void updatePassword(ReqUpdatePasswordDTO reqUpdatePasswordDTO) throws ApplicationException {
        String username = SecurityUtil.getCurrentLogin().isPresent() ? SecurityUtil.getCurrentLogin().get() : "";
        Account account = this.handleGetAccountByUsername(username);
        String currentPass = account.getPassword();
        if(!passwordEncoder.matches(reqUpdatePasswordDTO.getCurrentPassword(), account.getPassword())) {
            throw new ApplicationException("Mật khẩu hiện tại không chính xác");
        }
        if(reqUpdatePasswordDTO.getNewPassword().equals(reqUpdatePasswordDTO.getCurrentPassword())) {
            throw new ApplicationException("Mật khẩu mới không được trùng với mật khẩu hiện tại");
        }
        if(!reqUpdatePasswordDTO.getNewPassword().equals(reqUpdatePasswordDTO.getConfirmPassword())) {
            throw new ApplicationException("Mật khẩu không trùng khớp");
        }
        account.setPassword(passwordEncoder.encode(reqUpdatePasswordDTO.getNewPassword()));
        this.accountRepository.save(account);
    }

    public Account registerAccountAdmin(ReqRegisterDTO reqRegisterDTO) throws ApplicationException {
        if(!reqRegisterDTO.getPassword().equals(reqRegisterDTO.getConfirmPassword())) {
            throw new ApplicationException("Mật khẩu không trùng khớp");
        }
        Account account = new Account();
        account.setEmail(reqRegisterDTO.getEmail());
        account.setPassword(this.passwordEncoder.encode(reqRegisterDTO.getPassword()));
        account.setActive(true);
        account.setVerified(true);
        Account savedAccount = this.accountRepository.save(account);
        this.createAccountWithRole(savedAccount, "ADMIN");

        return savedAccount;
    }

    public ResDeactivateAccount getInfoDeactivatedAccount(String email) throws ApplicationException {
        AccountRole accountRole = this.accountRoleService.getAccountRole(email, "USER");
        ResDeactivateAccount res = new ResDeactivateAccount();
        res.setLockReason(accountRole.getLockReason());
//        res.setTimeCancel(res.getTimeCancelFormatted(accountRole.getTimeCancel()));
        res.setTimeCancel(accountRole.getTimeUpdate());
        return res;


    }

    @Transactional
    @Scheduled(cron = "0 */1 * * * *")
    public void deleteAccountNotVerify() {
        List<Account> accounts = this.accountRepository.findAccountNotVerify();
        this.accountRepository.deleteAll(accounts);
        log.info("Deleted accounts not verified");
    }






}
