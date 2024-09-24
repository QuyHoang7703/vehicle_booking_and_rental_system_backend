package com.pbl6.VehicleBookingRental.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.pbl6.VehicleBookingRental.domain.Account;
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
            throw new UsernameNotFoundException("Username/password khong hop le");
        }
        return new User(
                account.getEmail(),
                account.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        
    }
    
}
