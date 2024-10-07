package com.pbl6.VehicleBookingRental.user.controller.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
public class OAuth2Controller {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.registration.google.scope}")
    private String scope;
    
    @GetMapping("/auth/google-login")
    public void redirectToGoogleOAuth(HttpServletResponse response) throws IOException {
        // Xây dựng URL OAuth2 động dựa trên các giá trị từ application.properties
        String googleOAuthUrl = "https://accounts.google.com/o/oauth2/auth?"
                                + "client_id=" + clientId
                                + "&redirect_uri=" + redirectUri
                                + "&response_type=code"
                                + "&scope=" + scope.replace(", ", "%20"); // Thay dấu phẩy thành khoảng trắng (URL encoded)

        // Chuyển hướng người dùng tới URL Google OAuth2
        response.sendRedirect(googleOAuthUrl);
    }

    // @GetMapping("/oauth2/callback")
    // public ResponseEntity<?> handleOAuth2Callback(@RequestParam("code") String code) {
    //     // Xử lý mã xác thực tại đây
    //     // Gọi Google để lấy token truy cập và thông tin người dùng

    //     // Trả về phản hồi JSON
    //     return ResponseEntity.ok("{\"message\": \"Login successful!\"}");
    // }
}
