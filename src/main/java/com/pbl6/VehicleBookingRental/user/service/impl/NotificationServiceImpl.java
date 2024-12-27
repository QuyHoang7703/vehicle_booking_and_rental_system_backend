package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.chat.ConversationAccount;
import com.pbl6.VehicleBookingRental.user.domain.notification.Notification;
import com.pbl6.VehicleBookingRental.user.domain.notification.NotificationAccount;
import com.pbl6.VehicleBookingRental.user.dto.chat_dto.NotificationDTO;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRepository;
import com.pbl6.VehicleBookingRental.user.repository.chat.NotificationAccountRepo;
import com.pbl6.VehicleBookingRental.user.repository.chat.NotificationRepo;
import com.pbl6.VehicleBookingRental.user.service.NotificationService;
import com.pbl6.VehicleBookingRental.user.util.constant.AccountEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final SimpMessagingTemplate messagingTemplate ;
    private final NotificationAccountRepo notificationAccountRepo;
    private final NotificationRepo notificationRepo;
    private final AccountRepository accountRepository;
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
                    notificationDTO.setMetadata(notification.getMetadata());
                    notificationDTO.setCreate_at(notification.getCreate_at() !=null ? notification.getCreate_at().toInstant():null);
                    return notificationDTO;
                }).collect(Collectors.toList());
    }

    @Override
    public boolean updateUnseenNotification(int account_id, AccountEnum role_account, int notification_id) {
        try{
            NotificationAccount notificationAccount = notificationAccountRepo.findNotificationAccountByAccountIdAndPartnerTypeAndNotificationId(account_id,role_account,notification_id);
            if(notificationAccount != null){
                Notification notification = notificationAccount.getNotification();
                notification.setSeen(true);
                notificationAccountRepo.save(notificationAccount);
                return true;
            }
            return false;
        }catch (Exception exception){
            System.out.println(exception.getMessage());
            return false;
        }
    }
    @Override
    public void createNotificationToAccount(int accountId, AccountEnum accountTypeEnum,NotificationDTO notificationDTO) {
        Notification notification = new Notification();
        notification.setCreate_at(
                Optional.ofNullable(notificationDTO.getCreate_at())
                        .map(instant -> new Date(instant.toEpochMilli()))
                        .orElse(null)
        );
        notification.setType(notificationDTO.getType());
        notification.setTitle(notificationDTO.getTitle());
        notification.setMessage(notificationDTO.getMessage());
        notification.setSeen(notificationDTO.isSeen());
        notification.setMetadata(notificationDTO.getMetadata());
        notificationRepo.save(notification);

        NotificationAccount notificationAccount = new NotificationAccount();
        notificationAccount.setNotification(notification);
        Optional<Account> partnerAccount = accountRepository.findById(accountId);
        notificationAccount.setAccount(partnerAccount.get());
        notificationAccount.setPartnerType(accountTypeEnum);
        notificationAccountRepo.save(notificationAccount);

        sendNotification(accountId
                        ,String.valueOf(accountTypeEnum)
                        , NotificationDTO.builder()
                                .id(notification.getId())
                                .type(notification.getType())
                                .title(notification.getTitle())
                                .message(notification.getMessage())
                                .create_at(notification.getCreate_at()!=null ? notification.getCreate_at().toInstant():null )
                                .isSeen(notification.isSeen())
                                .metadata(notification.getMetadata())
                                .build());

    }
}
