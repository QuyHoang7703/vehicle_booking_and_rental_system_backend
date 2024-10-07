package com.pbl6.VehicleBookingRental.user.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
// @Service
// public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

//     public CustomOAuth2UserService() {
//         System.out.println("CustomOAuth2UserService initialized");
//     }
//     @Override
//     public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//         System.out.println(">>>> CAL loadUser");
//         OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
//         OAuth2User oAuth2User = delegate.loadUser(userRequest);
//         String email = oAuth2User.getAttribute("email");
//         String name = oAuth2User.getAttribute("name");

//         return oAuth2User;
//     }
    
    
// }

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService  {
 
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user =  super.loadUser(userRequest);
        return new CustomOAuth2User(user);
    }
 
}
