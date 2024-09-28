package com.pbl6.VehicleBookingRental.controller.user;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pbl6.VehicleBookingRental.domain.Account;
import com.pbl6.VehicleBookingRental.domain.dto.LoginDTO;
import com.pbl6.VehicleBookingRental.domain.dto.ResLoginDTO;
import com.pbl6.VehicleBookingRental.service.AccountService;
import com.pbl6.VehicleBookingRental.service.SecurityUtil;
import com.pbl6.VehicleBookingRental.util.annotation.ApiMessage;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final AccountService accountService;
    @Value("${pbl6.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;
    
    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil, AccountService accountService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.accountService = accountService;
    }


    @PostMapping("/auth/login")
    @ApiMessage("Login successfully")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        //Load username and password into Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        //User Authentication => overwrite LoadUserByUsername in UserDetailService
        Authentication authentication = this.authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
     
        Account account = this.accountService.handleGetAccountByUsername(loginDTO.getUsername());
        ResLoginDTO res = new ResLoginDTO();
        if(account != null){
            ResLoginDTO.AccountLogin accountLogin = new ResLoginDTO.AccountLogin(account.getId(), account.getPhoneNumber(), account.getName());
            res.setAccountLogin(accountLogin);
        }
    
        // Create token when authentication is successful
        String accessToken = this.securityUtil.createAccessToken(authentication, res);
        res.setAccessToken(accessToken);
        

        // Create refresh token 
        String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);
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
}
