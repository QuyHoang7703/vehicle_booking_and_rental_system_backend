package com.pbl6.VehicleBookingRental.user.repository.account;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.account.AccountRole;
import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRole, Integer> {
    List<AccountRole> findByAccountId(int id);
    void deleteAccountRolesByAccountAndRole(Account account, Role role);
}
