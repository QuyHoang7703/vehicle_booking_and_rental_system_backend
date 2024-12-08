package com.pbl6.VehicleBookingRental.user.repository.chat;

import com.pbl6.VehicleBookingRental.user.domain.notification.NotificationAccount;
import com.pbl6.VehicleBookingRental.user.util.constant.AccountEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationAccountRepo extends JpaRepository<NotificationAccount,Integer> {
    public List<NotificationAccount> getNotificationAccountByAccount_IdAndPartnerType(int account_id, AccountEnum partnerTypeEnum);
    public NotificationAccount findNotificationAccountByAccountIdAndAccountRoleAndNotificationId(int account_id,AccountEnum accountEnum,int notification_id);
}
