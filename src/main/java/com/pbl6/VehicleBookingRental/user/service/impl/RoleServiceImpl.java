package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.account.AccountRole;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRoleRepository;
import com.pbl6.VehicleBookingRental.user.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {


    private final AccountRoleRepository accountRoleRepository;
    @Override
    public List<String> getNameRolesByAccountID(int idAccount) {
        List<AccountRole> accountRoles = this.accountRoleRepository.findByAccountId(idAccount);
        List<String> roles = accountRoles.stream().map(accountRole -> accountRole.getRole().getName())
                                                    .collect(Collectors.toList());
        return roles;
    }
}
