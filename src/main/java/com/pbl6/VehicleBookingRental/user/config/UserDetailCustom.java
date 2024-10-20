package com.pbl6.VehicleBookingRental.user.config;

import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import com.pbl6.VehicleBookingRental.user.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.service.AccountService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailsService")
@RequiredArgsConstructor
@Slf4j
public class UserDetailCustom implements UserDetailsService{
    private final AccountService accountService;
    private final RoleService roleService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = this.accountService.handleGetAccountByUsername(username);
        if(account==null) {
            throw new UsernameNotFoundException("Username/Password is invalid");
        }
        List<String> roles = this.roleService.getNameRolesByAccountID(account.getId());
        log.info("Roles: " + roles);
        List<GrantedAuthority> grantedAuthorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        log.info("grantedAuthorities: " + grantedAuthorities);
        return new User(
                account.getEmail(),
                account.getPassword(),
                grantedAuthorities
//                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        
    }
    
}
