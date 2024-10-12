package com.pbl6.VehicleBookingRental.user.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pbl6.VehicleBookingRental.user.domain.RestResponse;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.dto.ResponseInfo;
import com.pbl6.VehicleBookingRental.user.dto.response.login.ResLoginDTO;
import com.pbl6.VehicleBookingRental.user.service.AccountService;
import com.pbl6.VehicleBookingRental.user.service.CustomOAuth2User;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import com.pbl6.VehicleBookingRental.user.util.error.IdInValidException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import java.util.Optional;
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler{
    private final AccountService accountService;

    private final SecurityUtil securityUtil;

    @Value("${pbl6.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;
    

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException{
        Object principal = authentication.getPrincipal();
        if(principal instanceof CustomOAuth2User) {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) principal;
            boolean check = this.accountService.checkAvailableUsername(oAuth2User.getEmail());
            if(check==false){
                Account account = new Account();
                account.setEmail(oAuth2User.getEmail());
                account.setName(oAuth2User.getName());
                account.setVerified(true);
                this.accountService.handleLoginWithGoogle(account);
            }
          

            Account currentAccount = this.accountService.handleGetAccountByUsername(oAuth2User.getEmail());
            ResLoginDTO res = new ResLoginDTO();
            if(currentAccount != null){
                ResLoginDTO.AccountLogin accountLogin = new ResLoginDTO.AccountLogin(currentAccount.getId(), currentAccount.getEmail(), currentAccount.getName());
                res.setAccountLogin(accountLogin);
            }
        
            // Create token when authentication is successful
            String accessToken = this.securityUtil.createAccessToken(currentAccount.getEmail(), res);
            res.setAccessToken(accessToken);
            

            // Create refresh token 
            String refresh_token = this.securityUtil.createRefreshToken(currentAccount.getEmail(), res);
            this.accountService.updateRefreshToken(refresh_token, currentAccount.getEmail());
            ResponseCookie resCookies = ResponseCookie
                                                    .from("refresh_token", refresh_token)
                                                    .httpOnly(true)
                                                    .secure(true)
                                                    .path("/")
                                                    .maxAge(refreshTokenExpiration)
                                                    // .domain("example.com")
                                                    .build();
            // Đặt cookie
            response.addHeader(HttpHeaders.SET_COOKIE, resCookies.toString());

            // Trả về JSON response với status 200 OK
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(res));
          
        }
    }

    
    
}
