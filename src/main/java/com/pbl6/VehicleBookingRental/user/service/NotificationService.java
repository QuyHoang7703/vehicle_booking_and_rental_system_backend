package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.dto.chat_dto.NotificationDTO;
import com.pbl6.VehicleBookingRental.user.util.constant.AccountEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;

import java.util.List;

public interface NotificationService {
    public void sendNotification(int recipientId, String recipientType, NotificationDTO notificationDTO);
    public List<NotificationDTO> getNotificationByAccountIdAndRoleAccount(int account_id, AccountEnum role_account);
}
