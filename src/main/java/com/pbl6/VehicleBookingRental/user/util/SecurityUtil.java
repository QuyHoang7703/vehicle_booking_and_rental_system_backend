package com.pbl6.VehicleBookingRental.user.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.service.AccountService;
import com.pbl6.VehicleBookingRental.user.service.RoleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.util.Base64;
import com.pbl6.VehicleBookingRental.user.dto.response.login.ResLoginDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Service
public class SecurityUtil {
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;
    private final JwtEncoder jwtEncoder;
    private final RoleService roleService;
    private final AccountService accountService;
    
    public SecurityUtil(JwtEncoder jwtEncoder, RoleService roleService, AccountService accountService) {
        this.jwtEncoder = jwtEncoder;
        this.roleService = roleService;
        this.accountService = accountService;
    }

    @Value("${pbl6.jwt.base64-secret}")
    private String jwtKey;

    @Value("${pbl6.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;

    @Value("${pbl6.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, SecurityUtil.JWT_ALGORITHM.getName());
    }

    public Jwt checkValidRefreshToken(String token) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
        try {
            return jwtDecoder.decode(token);
        } catch (Exception e) {
            System.out.println(">>> JWT error: " + e.getMessage());
            throw e;
        }

    }


    public String createAccessToken(String username, ResLoginDTO loginDTO) {
        Instant now = Instant.now();
        Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);

        //Create header
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        Account account = this.accountService.handleGetAccountByUsername(username);
        List<String> roles = this.roleService.getNameRolesByAccountID(account.getId());
        // Create payload
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(validity)
            .subject(username)
            .claim("user", loginDTO.getAccountLogin())
//                .claim("authorizes", roles.stream().map(role -> "" + role).collect(Collectors.toList()))
            .build();   

       
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();

    }

    public String createRefreshToken(String username, ResLoginDTO loginDTO) {
        Instant now = Instant.now();
        Instant validity = now.plus(this.refreshTokenExpiration, ChronoUnit.SECONDS);

        //Create header
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

        // Create payload
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(validity)
            .subject(username)
            .claim("user", loginDTO.getAccountLogin())
            .build();   

       
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();

    }
   /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    public static Optional<String> getCurrentLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

     private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }

    public ResponseCookie createAccessCookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
                //.httpOnly(true)   // HTTP-only for security
                .secure(true)     // Secure flag
                .path("/")        // Cookie valid for entire site
                .maxAge(maxAge)   // Expiration time
                .build();

    }

    public ResponseCookie createRefreshCookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)   // HTTP-only for security
                .secure(true)     // Secure flag
                .path("/")        // Cookie valid for entire site
                .maxAge(maxAge)   // Expiration time
                .build();

    }
}
