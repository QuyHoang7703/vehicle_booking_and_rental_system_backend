package com.pbl6.VehicleBookingRental.user.controller.user;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.dto.response.login.ResLoginDTO;
import com.pbl6.VehicleBookingRental.user.service.AccountService;
import com.pbl6.VehicleBookingRental.user.service.Outh2Serrvice;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("identity/auth")
public class Outh2Controller {
    private final Outh2Serrvice outh2Serrvice;
    private final SecurityUtil securityUtil;
    private final AccountService accountService;
    @Value("${pbl6.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;

    @Value("${pbl6.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;
    @PostMapping("/outbound/authentication")

    public ResponseEntity<ResLoginDTO> outBoundAuthenticate(@RequestParam("code") String code){
        Account account =this.outh2Serrvice.getToken(code);
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        ResLoginDTO.AccountLogin accountLogin = new ResLoginDTO.AccountLogin();
        accountLogin.setId(account.getId());
        accountLogin.setUsername(account.getEmail());
        accountLogin.setName(account.getName());
        accountLogin.setAvatar(account.getAvatar());

        resLoginDTO.setAccountLogin(accountLogin);
        String accessToken = this.securityUtil.createAccessToken(account.getEmail(), resLoginDTO);
        resLoginDTO.setAccessToken(accessToken);

        //Save access token into Cookie
        ResponseCookie accCookies = ResponseCookie
                .from("access_token", accessToken)
                // .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(accessTokenExpiration)
                // .domain("example.com")
                .build();


        // Create refresh token
        String refresh_token = this.securityUtil.createRefreshToken(account.getEmail(), resLoginDTO);
        this.accountService.updateRefreshToken(refresh_token, account.getEmail());
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                // .domain("example.com")
                .build();

        log.info("Access token: " + accessToken);
        log.info("Refresh token: " + refresh_token);
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, accCookies.toString())
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(resLoginDTO);



    }
}
