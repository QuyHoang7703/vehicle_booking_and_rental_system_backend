package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.chat.ConversationAccount;
import com.pbl6.VehicleBookingRental.user.domain.notification.Notification;
import com.pbl6.VehicleBookingRental.user.domain.notification.NotificationAccount;
import com.pbl6.VehicleBookingRental.user.dto.chat_dto.NotificationDTO;
import com.pbl6.VehicleBookingRental.user.repository.chat.NotificationAccountRepo;
import com.pbl6.VehicleBookingRental.user.repository.chat.NotificationRepo;
import com.pbl6.VehicleBookingRental.user.service.NotificationService;
import com.pbl6.VehicleBookingRental.user.util.constant.AccountEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final SimpMessagingTemplate messagingTemplate ;
    private final NotificationAccountRepo notificationAccountRepo;

    @Override
    public void sendNotification(int recipientId, String recipientType, NotificationDTO notificationDTO) {
        messagingTemplate.convertAndSendToUser(
                String.valueOf(recipientId),
                String.format("/%s/notification",recipientType),
                notificationDTO
        );
    }

    @Override
    public List<NotificationDTO> getNotificationByAccountIdAndRoleAccount(int account_id, AccountEnum partnerTypeEnum) {
        List<NotificationDTO> notificationDTOList = new ArrayList<>();
        //Get conversation account by accountId and role account
        List<NotificationAccount> notificationAccountList = notificationAccountRepo.getNotificationAccountByAccount_IdAndPartnerType(account_id,partnerTypeEnum);
        return Optional.ofNullable(notificationAccountList)
                .orElse(Collections.emptyList())
                .stream().map(notificationAccount -> {
                    NotificationDTO notificationDTO = new NotificationDTO();
                    //
                    Notification notification = notificationAccount.getNotification();
                    notificationDTO.setId(notification.getId());
                    notificationDTO.setType(notification.getType());
                    notificationDTO.setTitle(notification.getTitle());
                    notificationDTO.setSeen(notification.isSeen());
                    notificationDTO.setMessage(notification.getMessage());
                    notificationDTO.setCreate_at(notification.getCreate_at() !=null ? notification.getCreate_at().toInstant():null);
                    return notificationDTO;
                }).collect(Collectors.toList());
    }
}
