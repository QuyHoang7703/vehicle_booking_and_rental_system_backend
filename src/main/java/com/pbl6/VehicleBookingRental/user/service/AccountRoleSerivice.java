package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.account.Role;

import java.util.List;

public interface AccountRoleSerivice {
    public List<Role> getAccountRolesByAccountID(int idAccount);
}
