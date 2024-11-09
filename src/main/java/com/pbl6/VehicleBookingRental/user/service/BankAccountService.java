package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.BankAccount;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.dto.request.bankAccount.ReqBankAccount;
import com.pbl6.VehicleBookingRental.user.dto.response.bankAccount.ResBankAccountDTO;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;

public interface BankAccountService {
    void createBankAccount(ReqBankAccount reqBankAccount, Account account) throws Exception;
    ResBankAccountDTO convertoResBankAccountDTO(int accountId, PartnerTypeEnum partnerType) throws Exception;
    void deleteBankAccount(int accountId, PartnerTypeEnum partnerType) throws IdInvalidException;
}
