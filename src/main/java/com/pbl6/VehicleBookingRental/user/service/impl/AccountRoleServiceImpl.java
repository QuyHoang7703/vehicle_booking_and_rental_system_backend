package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.account.AccountRole;
import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRoleRepository;
import com.pbl6.VehicleBookingRental.user.service.AccountRoleSerivice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class AccountRoleServiceImpl implements AccountRoleSerivice {


    private final AccountRoleRepository accountRoleRepository;
    @Override
    public List<Role> getAccountRolesByAccountID(int idAccount) {
        List<AccountRole> accountRoles = this.accountRoleRepository.findByAccountId(idAccount);
        List<Role> roles = accountRoles.stream().map(accountRole -> accountRole.getRole()).collect(Collectors.toList());
        return roles;
    }
}
