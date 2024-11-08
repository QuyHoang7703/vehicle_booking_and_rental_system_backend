package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.dto.chat_dto.NotificationDTO;
import com.pbl6.VehicleBookingRental.user.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final SimpMessagingTemplate messagingTemplate ;
    @Override
    public void sendNotification(int recipientId, String recipientType, NotificationDTO notificationDTO) {
        messagingTemplate.convertAndSendToUser(
                String.valueOf(recipientId),
                String.format("/%s/notification",recipientType),
                notificationDTO
        );
    }
}
