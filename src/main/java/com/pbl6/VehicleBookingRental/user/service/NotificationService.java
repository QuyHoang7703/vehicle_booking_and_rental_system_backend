package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.dto.chat_dto.NotificationDTO;

public interface NotificationService {
    public void sendNotification(int recipientId, String recipientType, NotificationDTO notificationDTO);
}
