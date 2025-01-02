package com.pbl6.VehicleBookingRental.user.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    private final SecurityUtil securityUtil;
    @Autowired
    private final JacksonConfig jacksonConfig;
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // Lấy token từ headers
                    String authToken = accessor.getNativeHeader("Authorization").get(0);

                    // Xác thực token (có thể sử dụng JWT hoặc một phương pháp nào khác)
                    Authentication user = authenticateUser(authToken);

                    if (user != null) {
                        accessor.setUser(user);
                        System.out.println("Authorized successfully");// Thiết lập người dùng nếu xác thực thành công
                    } else {
                        throw new RuntimeException("Unauthorized"); // Hoặc một ngoại lệ phù hợp
                    }
                }
                return message;
            }

            private Authentication authenticateUser(String token) {
                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7); // Lấy token thực tế sau "Bearer "
                } else {
                    return null; // Nếu không có token, trả về null
                }

                try {
                    Jwt decodedJwt = securityUtil.checkValidRefreshToken(token); // Gọi hàm giải mã
                    String username = decodedJwt.getClaimAsString("sub"); // Lấy thông tin người dùng từ JWT
                    System.out.println(">>> JWT : " + username);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, Collections.singleton((GrantedAuthority) () -> "ROLE_USER"));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    // Tạo đối tượng Authentication
                    return authentication;
                } catch (Exception e) {
                    System.out.println(">>> JWT error: " + e.getMessage());
                    return null; // Trả về null nếu token không hợp lệ
                }
            }
        });
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins(
                        "https://150.95.110.230:443",
                        "https://safelytravel",
                        "http://safelytravel",
                        "http://150.95.110.230:80",
                        "http://150.95.110.230:3000",
                        "http://safelytravel:3000"
                )
                .withSockJS();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/user");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        // Đăng ký CustomInstantSerializer chỉ cho Instant
        module.addSerializer( Instant.class, new CustomInstantSerializer());
        objectMapper.registerModule(new JavaTimeModule()); // Đăng ký module cho Java 8 Time API
        objectMapper.registerModule(module);

        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);

        return false;
    }


}
