

package com.pbl6.VehicleBookingRental.user.controller.user;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.dto.ResponseInfo;
import com.pbl6.VehicleBookingRental.user.dto.request.account.ReqAccountInfoDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.account.ReqChangePasswordDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.login.ReqLoginDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.register.ReqRegisterDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.register.ReqVerifyDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.account.ResAccountInfoDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.login.ResLoginDTO;

// import org.hibernate.mapping.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pbl6.VehicleBookingRental.user.service.AccountService;
import com.pbl6.VehicleBookingRental.user.service.CustomOAuth2UserService;
import com.pbl6.VehicleBookingRental.user.service.S3Service;
import com.pbl6.VehicleBookingRental.user.service.TokenService;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import com.pbl6.VehicleBookingRental.user.util.annotation.ApiMessage;
import com.pbl6.VehicleBookingRental.user.util.error.IdInValidException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final TokenService tokenService;
    @Value("${pbl6.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;
    @Value("${pbl6.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;
    


    @PostMapping("auth/register")
    @ApiMessage("Register a new user")
    public ResponseEntity<ResponseInfo<String>> createUser(@RequestBody ReqRegisterDTO registerDTO) throws IdInValidException{
        if(this.accountService.checkAvailableUsername(registerDTO.getEmail())){
            Account account = this.accountService.handleGetAccountByUsername(registerDTO.getEmail());
            if(!account.isVerified()) {
                throw new IdInValidException("Email này đã được đăng ký nhưng chưa xác nhận. Vui lòng xác nhận");
            }
            throw new IdInValidException("Email này đã được đăng ký rồi");
            
        }
        if(!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())){
            throw new IdInValidException("Mật khẩu và xác nhận mật khẩu không trùng khớp");
        }
        String hashPassword = this.passwordEncoder.encode(registerDTO.getPassword());
        registerDTO.setPassword(hashPassword);
        Account newAccount = this.accountService.handleRegisterUser(registerDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseInfo<>("Kiểm tra email để lấy OTP"));
        // return ResponseEntity.status(HttpStatus.CREATED).body(this.accountService.convertToResUserRegister(newAccount));
    }

    @PostMapping(value="auth/register-info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiMessage("Register a new user")
    public ResponseEntity<ResAccountInfoDTO> addInfoUser(@RequestParam(value="fileAvatar", required = false) MultipartFile file,
                                                        @RequestPart("account_info") ReqAccountInfoDTO accountInfoDTO) throws IdInValidException {
        Account account = this.accountService.handleGetAccountByUsername(accountInfoDTO.getUsername());
        if(account == null ){
            throw new IdInValidException("Email chưa được đăng ký");
        }

        account.setName(accountInfoDTO.getName());
        account.setBirthDay(accountInfoDTO.getBirthDay());
        account.setGender(accountInfoDTO.getGender());
        account.setPhoneNumber(accountInfoDTO.getPhoneNumber());
        if(file != null) {
            String urlAvatar = this.s3Service.uploadFile(file);
//            this.s3Service.deleteFile(account.getAvatar());
            account.setAvatar(urlAvatar);

        }
        this.accountService.handleUpdateAccount(account);

        return ResponseEntity.ok(this.accountService.convertToResAccountInfoDTO(account));
    }

    @PostMapping("/auth/verify")
    public ResponseEntity<ResponseInfo<String>> verify(@RequestBody ReqVerifyDTO verifyDTO) throws IdInValidException{
        if(!this.accountService.checkAvailableUsername(verifyDTO.getEmail())){
            throw new IdInValidException("Email không tồn tại");
        }
        this.accountService.verify(verifyDTO.getEmail(), verifyDTO.getOtp());
        
        return ResponseEntity.ok(new ResponseInfo<>("Xác thực thành công"));
    }

    @PostMapping("/auth/resend_otp")
    public ResponseEntity<ResponseInfo<String>> resendOTP(@RequestParam String email) throws IdInValidException{
        // if(this.userService.checkAvailableEmail(email)){
        //     throw new IdInValidException("Email not found");
        // }
        this.accountService.resendOtp(email);
        return ResponseEntity.ok(new ResponseInfo<>("Gửi lại OTP"));

    }


    @PostMapping("/auth/login")
    @ApiMessage("Login successfully")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) throws IdInValidException {
//        boolean check = this.accountService.handleGetAccountByUsername(loginDTO.getUsername()).isVerified();
//        if(!this.accountService.handleGetAccountByUsername(loginDTO.getUsername()).isVerified()){
//            throw new IdInValidException("Tài khoản không tồn tại hoặc chưa được xác thực");
//        }
//        if(!this.accountService.isActiveAccount(loginDTO.getUsername())){
//            throw new IdInValidException("Tài khoản này đã bị khóa");
//        }
        //Load username and password into Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        //User Authentication => overwrite LoadUserByUsername in UserDetailService
        Authentication authentication = this.authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("Username from authentication: " + authentication.getName());
        
        Account account = this.accountService.handleGetAccountByUsername(loginDTO.getUsername());
        if(account == null){
            throw new IdInValidException("Tài khoản không tồn tại");
        }
        ResLoginDTO res = this.accountService.convertToResLoginDTO(account);
        // Create token when authentication is successful
        String accessToken = this.securityUtil.createAccessToken(authentication.getName(), res);
        res.setAccessToken(accessToken);
        //Save access token into Cookie
        ResponseCookie accCookies = ResponseCookie
                                                .from("access_token", accessToken)
                                                // .httpOnly(true)
//                                                .secure(true)
                                                .path("/")
                                                .maxAge(accessTokenExpiration)
                                                .domain("vehicle-booking-and-rental-system.vercel.app")
                .sameSite("None")
                                                .build();
        // Create refresh token
        String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);
        this.accountService.updateRefreshToken(refresh_token, loginDTO.getUsername());
        ResponseCookie resCookies = ResponseCookie
                                                .from("refresh_token", refresh_token)
                                                .httpOnly(true)
//                                                .secure(true)
                                                .path("/")
                                                .maxAge(refreshTokenExpiration)
                                                .domain("vehicle-booking-and-rental-system.vercel.app")
                .sameSite("None")
                                                .build();
        
        
        return ResponseEntity
                            .status(HttpStatus.OK)
                            .header(HttpHeaders.SET_COOKIE, accCookies.toString())
                            .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                            .body(res);
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
        
        if(account==null) {
            throw new IdInValidException("Refresh Token is invalid");
        }

        ResLoginDTO res = this.accountService.convertToResLoginDTO(account);
        String accessToken = this.securityUtil.createAccessToken(username, res);
        res.setAccessToken(accessToken);

        //Save access token into Cookie
        ResponseCookie accCookies = ResponseCookie
                                                .from("access_token", accessToken)
                                                // .httpOnly(true)
                                                //.secure(true)
                                                .path("/")
                                                .maxAge(accessTokenExpiration)
                                                .domain("vehicle-booking-and-rental-system.vercel.app")
                                                .build();

        // Create refresh token 
        String refresh_token = this.securityUtil.createRefreshToken(username, res);
        this.accountService.updateRefreshToken(refresh_token, username);
        ResponseCookie resCookies = ResponseCookie
                                                .from("refresh_token", refresh_token)
                                                .httpOnly(true)
                                                //.secure(true)
                                                .path("/")
                                                .maxAge(refreshTokenExpiration)
                                                .domain("vehicle-booking-and-rental-system.vercel.app")
                                                .build();
        
        return ResponseEntity
                            .status(HttpStatus.OK)
                            .header(HttpHeaders.SET_COOKIE, accCookies.toString())
                            .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                            .body(res);
     
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout account")
    public ResponseEntity<Void> logout() throws IdInValidException{
        String username = SecurityUtil.getCurrentLogin().isPresent()?
                            SecurityUtil.getCurrentLogin().get() : "";
        if(username.isEmpty()) {
            throw new IdInValidException("Access token is invalid");
        }
        this.accountService.updateRefreshToken(null, username);
        ResponseCookie resCookies = ResponseCookie
                                                .from("refresh_token", null)
                                                // .httpOnly(true)
                                                .secure(true)
                                                .path("/")
                                                .maxAge(0)
                                                .domain("vehicle-booking-and-rental-system.vercel.app")
                                                .build();
        ResponseCookie accCookies = ResponseCookie
                                                .from("access_token", null)
                                                .httpOnly(true)
                                                .secure(true)
                                                .path("/")
                                                .maxAge(0)
                                                .domain("vehicle-booking-and-rental-system.vercel.app")
                                                .build();                                       
        System.out.println(">>>>> Logout account username: " + username);
        return ResponseEntity.status(HttpStatus.OK)
                            .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                            .header(HttpHeaders.SET_COOKIE, accCookies.toString())
                            .body(null);
    }

    @GetMapping("/auth/forgot-password")
    @ApiMessage("Send request restore password")
    public ResponseEntity<ResponseInfo<String>> sendRequestForgotPassword(@RequestParam("email") String email) throws IdInValidException {
        this.tokenService.createToken(email);
       
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Yêu cầu của bạn đã được gửi tới email"));
    }

    @GetMapping("/auth/verify-token")
    @ApiMessage("Send request restore password")
    public ResponseEntity<ResponseInfo<Boolean>> checkValidToken(@RequestParam("token") String token) {
        
       
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>(this.tokenService.isValidToken(token)));
    }

    @PostMapping("/auth/change-password")
    public ResponseEntity<ResponseInfo<String>> changePassword(@RequestBody ReqChangePasswordDTO changePasswordDTO) throws IdInValidException{
        this.accountService.handleChangePassword(changePasswordDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Đã thay đổi mật khẩu"));
    }



}
