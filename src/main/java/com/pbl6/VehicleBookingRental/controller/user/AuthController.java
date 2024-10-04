package com.pbl6.VehicleBookingRental.controller.user;
import java.security.Security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pbl6.VehicleBookingRental.domain.Account;
import com.pbl6.VehicleBookingRental.domain.dto.AccountInfoDTO;
import com.pbl6.VehicleBookingRental.domain.dto.LoginDTO;
import com.pbl6.VehicleBookingRental.domain.dto.RegisterDTO;
import com.pbl6.VehicleBookingRental.domain.dto.ResLoginDTO;
import com.pbl6.VehicleBookingRental.domain.dto.ResRegisterDTO;
import com.pbl6.VehicleBookingRental.domain.dto.ResponseInfo;
import com.pbl6.VehicleBookingRental.domain.dto.VerifyDTO;
import com.pbl6.VehicleBookingRental.service.AccountService;
import com.pbl6.VehicleBookingRental.service.SecurityUtil;
import com.pbl6.VehicleBookingRental.util.annotation.ApiMessage;
import com.pbl6.VehicleBookingRental.util.constant.GenderEnum;
import com.pbl6.VehicleBookingRental.util.error.IdInValidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    @Value("${pbl6.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;
    
    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil
                            , AccountService accountService, PasswordEncoder passwordEncoder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("auth/register")
    @ApiMessage("Register a new user")
    public ResponseEntity<ResponseInfo> createUser(@RequestBody RegisterDTO registerDTO) throws IdInValidException{
        // if(this.accountService.checkAvailableEmail(registerDTO.getEmail())){
        //     throw new IdInValidException("Email already exist, please use another one");
        // }
        if(!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())){
            throw new IdInValidException("Password and Confirm Password do not match");
        }
        String hashPassword = this.passwordEncoder.encode(registerDTO.getPassword());
        registerDTO.setPassword(hashPassword);
        Account newAccount = this.accountService.handleRegisterUser(registerDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseInfo("Check email to get OTP"));
        // return ResponseEntity.status(HttpStatus.CREATED).body(this.accountService.convertToResUserRegister(newAccount));
    }

    @PostMapping("auth/register-info")
    @ApiMessage("Register a new user")
    public ResponseEntity<ResRegisterDTO> addInfoUser(@RequestBody AccountInfoDTO accountInfoDTO) {
        Account account = this.accountService.handleGetAccountByUsername(accountInfoDTO.getUsername());
        account.setName(accountInfoDTO.getName());
        account.setBirthDay(accountInfoDTO.getBirthDay());
        account.setGender(accountInfoDTO.getGender());
        account.setPhoneNumber(accountInfoDTO.getPhoneNumber());
        account.setAvatar(accountInfoDTO.getAvatar());
        this.accountService.handleUpdateAccount(account);
        
        return ResponseEntity.ok(this.accountService.convertToResRegisterDTO(account));
    }

    @PostMapping("/auth/verify")
    public ResponseEntity<ResponseInfo> verify(@RequestBody VerifyDTO verifyDTO) throws IdInValidException{
        if(!this.accountService.checkAvailableUsername(verifyDTO.getEmail())){
            throw new IdInValidException("Email not found");
        }
        this.accountService.verify(verifyDTO.getEmail(), verifyDTO.getOtp());
        
        return ResponseEntity.ok(new ResponseInfo("Verified successful"));
    }

    @PostMapping("/auth/resend_otp")
    public ResponseEntity<ResponseInfo> resendOTP(@RequestParam String email) throws IdInValidException{
        // if(this.userService.checkAvailableEmail(email)){
        //     throw new IdInValidException("Email not found");
        // }
        this.accountService.resendOtp(email);
        return ResponseEntity.ok(new ResponseInfo("Resend OTP"));

    }


    @PostMapping("/auth/login")
    @ApiMessage("Login successfully")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) throws IdInValidException {
        if(!this.accountService.handleGetAccountByUsername(loginDTO.getUsername()).isVerified()){
            throw new IdInValidException("Account not found or not verified !!!");
        }
        //Load username and password into Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        //User Authentication => overwrite LoadUserByUsername in UserDetailService
        Authentication authentication = this.authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        Account account = this.accountService.handleGetAccountByUsername(loginDTO.getUsername());
        ResLoginDTO res = new ResLoginDTO();
        if(account != null){
            ResLoginDTO.AccountLogin accountLogin = new ResLoginDTO.AccountLogin(account.getId(), account.getEmail(), account.getName());
            res.setAccountLogin(accountLogin);
        }
    
        // Create token when authentication is successful
        // String u = authentication.getName();
        String accessToken = this.securityUtil.createAccessToken(authentication.getName(), res);
        res.setAccessToken(accessToken);
        

        // Create refresh token 
        String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);
        this.accountService.updateRefreshToken(refresh_token, loginDTO.getUsername());
        ResponseCookie resCookies = ResponseCookie
                                                .from("refresh_token", refresh_token)
                                                .httpOnly(true)
                                                .secure(true)
                                                .path("/")
                                                .maxAge(refreshTokenExpiration)
                                                // .domain("example.com")
                                                .build();
        
        
        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, resCookies.toString()).body(res);
    }

    @GetMapping("/auth/account")
    @ApiMessage("fetch account")
    public ResponseEntity<ResLoginDTO.AccountLogin> getAccount() {
        String username = SecurityUtil.getCurrentLogin().isPresent()?
                            SecurityUtil.getCurrentLogin().get():"";
        Account currentAccount = this.accountService.handleGetAccountByUsername(username);
        ResLoginDTO.AccountLogin accountLogin = new ResLoginDTO.AccountLogin();
        if(currentAccount != null){
            accountLogin.setId(currentAccount.getId());
            accountLogin.setUsername(currentAccount.getEmail());
            accountLogin.setName(currentAccount.getName());
        }
        return ResponseEntity.status(HttpStatus.OK).body(accountLogin);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(@CookieValue(name ="refresh_token", defaultValue = "noRefreshTokenInCookie") String refreshToken) throws IdInValidException {
        if(refreshToken.equals("noRefreshTokenInCookie")){
            throw new IdInValidException("You don't have refresh token in Cookie");
        }
        //Check valid refresh token
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refreshToken);
        String username = decodedToken.getSubject();
        Account account = new Account();
        boolean isEmailCheck = this.accountService.isEmail(username);
        if(isEmailCheck) {
            account = this.accountService.fetchAccountByRefreshTokenAndEmail(refreshToken, username);
        } else{
            account = this.accountService.fetchAccountByRefreshTokenAndPhoneNumber(refreshToken, username);
        }
        ResLoginDTO res = new ResLoginDTO();
        if(account==null) {
            throw new IdInValidException("Refresh Token is invalid");
        }else{
            ResLoginDTO.AccountLogin accountLogin = new ResLoginDTO.AccountLogin(account.getId(), account.getEmail(), account.getName());
            res.setAccountLogin(accountLogin);
        }
  
        String accessToken = this.securityUtil.createAccessToken(username, res);
        res.setAccessToken(accessToken);
        

        // Create refresh token 
        String refresh_token = this.securityUtil.createRefreshToken(username, res);
        this.accountService.updateRefreshToken(refresh_token, username);
        ResponseCookie resCookies = ResponseCookie
                                                .from("refresh_token", refresh_token)
                                                .httpOnly(true)
                                                .secure(true)
                                                .path("/")
                                                .maxAge(refreshTokenExpiration)
                                                // .domain("example.com")
                                                .build();
        
        
        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, resCookies.toString()).body(res);
     
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout account")
    public ResponseEntity<Void> logout() throws IdInValidException{
        String username = SecurityUtil.getCurrentLogin().isPresent()?
                            SecurityUtil.getCurrentLogin().get() : "";
        if(username==null) {
            throw new IdInValidException("Access token is invalid");
        }
        this.accountService.updateRefreshToken(null, username);
        ResponseCookie resCookies = ResponseCookie
                                                .from("refresh_token", null)
                                                .httpOnly(true)
                                                .secure(true)
                                                .path("/")
                                                .maxAge(0)
                                                // .domain("example.com")
                                                .build();
        System.out.println(">>>>> Logout account username: " + username);
        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, resCookies.toString()).body(null);
    }

}
