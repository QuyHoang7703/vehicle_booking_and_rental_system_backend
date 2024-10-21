package com.pbl6.VehicleBookingRental.user.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.account.Permission;
import com.pbl6.VehicleBookingRental.user.service.AccountService;
import com.pbl6.VehicleBookingRental.user.service.CustomOAuth2User;
import com.pbl6.VehicleBookingRental.user.service.CustomOAuth2UserService;
import com.pbl6.VehicleBookingRental.user.service.RoleService;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
@EnableWebSecurity
@Slf4j
public class SecurityConfiguration {

    private final RoleService roleService;

    private final AccountService accountService;
    @Value("${pbl6.jwt.base64-secret}")
    private String jwtKey;

    public SecurityConfiguration(RoleService roleService,@Lazy AccountService accountService) {
        this.roleService = roleService;
        this.accountService = accountService;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

   
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {
        http
                .csrf(c->c.disable())
                .cors(Customizer.withDefaults())
                // .antMatcher("/secured/**")
                .authorizeHttpRequests(
                        authz -> authz
                                .requestMatchers("/", "/api/v1/auth/**", "/identity/auth/outbound/authentication").permitAll()
                                .requestMatchers("/api/v1/auth/logout").authenticated()
                                .anyRequest().authenticated()
                                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                    
                .formLogin(f -> f.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    

    //Create signature 
    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, SecurityUtil.JWT_ALGORITHM.getName());
    }


    //Decode jwt from request's client which has jwt in its header
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
        return token -> {
            try {
                Jwt decodedJwt = jwtDecoder.decode(token);

                // Lấy role từ claims
                Map<String, Object> claims = decodedJwt.getClaims();
//                List<String> role = (List<String>) claims.get("authorizes");
//
//                // Bạn có thể thêm logic ở đây để xử lý role nếu cần thiết
//                System.out.println(">>> Role from JWT: " + role);
                // In ra toàn bộ claims để kiểm tra
                System.out.println(">>> Decoded JWT claims: " + claims);

                return decodedJwt; // Trả về token đã decode
            } catch (Exception e) {
                System.out.println(">>> JWT error: " + e.getMessage());
                throw e;
            }
        };
    }

//    @Bean
//    public JwtAuthenticationConverter jwtAuthenticationConverter() {
//        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_"); //QUyền hạn được lấy ra ko cần có tiền tố gì trước nó
//        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorizes"); //Lấy quyền hạn bên trong claim có tên là "permission"
//
//        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
//        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
//        return jwtAuthenticationConverter;
//    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // Lấy roles từ JWT
            Map<String, Object> userClaims = jwt.getClaimAsMap("user"); // Lấy claims của "user" dưới dạng Map
            List<String> roleNamesFromJWT = userClaims != null && userClaims.containsKey("roles") ?
                    (List<String>) userClaims.get("roles") : Collections.emptyList();
            log.info("ROLE FROM JWT: " + roleNamesFromJWT);
            // Lấy username từ JWT
//            String username = jwt.getClaimAsString("sub"); // Hoặc claim bạn đã sử dụng để lưu username
//            log.info("USERNAME: " + username);
//            Account account = this.accountService.handleGetAccountByUsername(username);
//            List<String> roleNames = this.roleService.getNameRolesByAccountID(account.getId());
            List<GrantedAuthority> authorities = new ArrayList<>();
            for(String roleName: roleNamesFromJWT){
                authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
                List<GrantedAuthority> permissionsOfRoleName = this.roleService.getAuthoritiesByRoleName(roleName);
                authorities.addAll(permissionsOfRoleName);

            }

            log.info("AUTHORITIES: " + authorities);
            return authorities;
        });
        return jwtAuthenticationConverter;
    }

   


}

