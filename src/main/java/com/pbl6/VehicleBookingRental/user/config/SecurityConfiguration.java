package com.pbl6.VehicleBookingRental.user.config;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.pbl6.VehicleBookingRental.user.service.CustomOAuth2User;
import com.pbl6.VehicleBookingRental.user.service.CustomOAuth2UserService;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
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
    @Value("${pbl6.jwt.base64-secret}")
    private String jwtKey;

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
//                     .oauth2Login(oauth2 -> oauth2
//                         .loginPage("/login")  // Chỉ định trang login cho OAuth2
//                         .authorizationEndpoint(authorization -> authorization
//                             .baseUri("/api/v1/auth/google-login")  // Đường dẫn cho OAuth2 login
//                         )
//                     .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)) // Xử lý thông tin người dùng
//                     .successHandler(customAuthenticationSuccessHandler)  // Xử lý khi đăng nhập thành công
//                 )
                                
                                
//                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
//                        );


                    
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
                List<String> role = (List<String>) claims.get("authorizes");

                // Bạn có thể thêm logic ở đây để xử lý role nếu cần thiết
                System.out.println(">>> Role from JWT: " + role);
                // In ra toàn bộ claims để kiểm tra
                System.out.println(">>> Decoded JWT claims: " + claims);

                return decodedJwt; // Trả về token đã decode
            } catch (Exception e) {
                System.out.println(">>> JWT error: " + e.getMessage());
                throw e;
            }
        };
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_"); //QUyền hạn được lấy ra ko cần có tiền tố gì trước nó
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorizes"); //Lấy quyền hạn bên trong claim có tên là "permission"

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

   


}

