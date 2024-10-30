package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.account.AccountRole;
import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRepository;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.account.RoleRepository;
import com.pbl6.VehicleBookingRental.user.service.AccountRoleService;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountRoleServiceImpl implements AccountRoleService {
    private final AccountRoleRepository accountRoleRepository;
    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    @Override
    public AccountRole getAccountRole(String email, String roleName) throws ApplicationException {
        Account account = this.accountRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException("Account not found"));
        if(account== null) {
            throw new ApplicationException("Account not found");
        }

        Role role = this.roleRepository.findByName("USER")
                .orElseThrow(()-> new ApplicationException("Role not found"));

        AccountRole accountRole = this.accountRoleRepository.findByAccount_IdAndRole_Id(account.getId(), role.getId())
                .orElseThrow(()-> new ApplicationException("Role not found"));

        return accountRole;
    }
}
