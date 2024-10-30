package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.account.AccountRole;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;

public interface AccountRoleService {
    AccountRole getAccountRole(String email, String roleName) throws ApplicationException;

}
