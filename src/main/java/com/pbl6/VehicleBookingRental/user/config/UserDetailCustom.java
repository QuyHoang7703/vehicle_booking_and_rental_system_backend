package com.pbl6.VehicleBookingRental.user.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.service.AccountService;

import java.util.Collections;

@Component("userDetailsService")
public class UserDetailCustom implements UserDetailsService{
    private final AccountService accountService;

    public UserDetailCustom(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = this.accountService.handleGetAccountByUsername(username);
        if(account==null) {
            throw new UsernameNotFoundException("Username/Password is invalid");
        }
        return new User(
                account.getEmail(),
                account.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        
    }
    
}
