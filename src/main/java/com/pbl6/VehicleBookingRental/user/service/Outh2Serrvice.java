package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.account.AccountRole;
import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import com.pbl6.VehicleBookingRental.user.dto.request.oauth2.ExchangeTokenRequest;
import com.pbl6.VehicleBookingRental.user.dto.response.login.ResLoginDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.oauth2.ExchangeTokenResponse;
import com.pbl6.VehicleBookingRental.user.dto.response.oauth2.OutboundUserResponse;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRepository;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.account.RoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.httpclient.OutboundIdentityClient;
import com.pbl6.VehicleBookingRental.user.repository.httpclient.OutboundUserClient;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class Outh2Serrvice {
    private final OutboundIdentityClient outboundIdentityClient;
    private final OutboundUserClient outboundUserClient;
    private final AccountRepository accountRepository;
    private final SecurityUtil securityUtil;
    private final AccountService accountService;
    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;

    // @NonFinal
     @Value("${outbound.identity.client-id}")
    protected String CLIENT_ID ;

    // @NonFinal
     @Value("${outbound.identity.client-secret}")
    protected String CLIENT_SECRET ;

    // @NonFinal
     @Value("${outbound.identity.redirect-uri}")
    protected String REDIRECT_URI;

    // @NonFinal
    protected final String GRANT_TYPE = "authorization_code";

    @Value("${pbl6.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;
    @Value("${pbl6.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public Account getToken(String code) throws ApplicationException {
        ExchangeTokenResponse exchangeTokenResponse = outboundIdentityClient.exchangeToken(ExchangeTokenRequest.builder()
                .code(code)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .redirectUri(REDIRECT_URI)
                .grantType(GRANT_TYPE)
                .build());
        log.info("TOKEN RESPONSE {}", exchangeTokenResponse);

        OutboundUserResponse userInfo = this.outboundUserClient.getUserInfo("json", exchangeTokenResponse.getAccessToken());
        log.info("USER INFO {}", userInfo);
        Optional<Account> optionalAccount = accountRepository.findByEmail(userInfo.getEmail());
        if(optionalAccount.isEmpty()) {
            Account account = new Account();
            account.setEmail(userInfo.getEmail());
            account.setName(userInfo.getName());
            account.setAvatar(userInfo.getPicture());
            account.setVerified(true);
            account.setActive(true);
            Account newAccount = this.accountRepository.save(account);
            Role role = this.roleRepository.findByName("USER")
                    .orElseThrow(() -> new ApplicationException("Role not found"));
            AccountRole accountRole = new AccountRole();
            accountRole.setRole(role);
            accountRole.setAccount(newAccount);
            this.accountRoleRepository.save(accountRole);
            return newAccount;

        }
        Account account = this.accountService.handleGetAccountByUsername(userInfo.getEmail());

        return account;
    }

    private ResLoginDTO createLoginResponse(Account account) {
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        ResLoginDTO.AccountLogin accountLogin = new ResLoginDTO.AccountLogin();
        accountLogin.setId(account.getId());
        accountLogin.setEmail(account.getEmail());
        accountLogin.setName(account.getName());
        accountLogin.setAvatar(account.getAvatar());
        resLoginDTO.setAccountLogin(accountLogin);
        return resLoginDTO;
    }

    private ResponseCookie createCookie(String name, String value, long maxAge) {
        return ResponseCookie
                .from(name, value)
//                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(maxAge)
                .build();
    }

}
