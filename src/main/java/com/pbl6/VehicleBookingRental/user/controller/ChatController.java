package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.domain.RestResponse;
import com.pbl6.VehicleBookingRental.user.dto.chat_dto.MessageDTO;
import com.pbl6.VehicleBookingRental.user.dto.chat_dto.NotificationDTO;
import com.pbl6.VehicleBookingRental.user.service.ChatMessageService;
import com.pbl6.VehicleBookingRental.user.service.NotificationService;
import com.pbl6.VehicleBookingRental.user.util.constant.AccountEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ChatMessageService chatMessageService;
    @Autowired
    private final NotificationService notificationService;
    @MessageMapping("/chat/send-message")
    public void processMessage(@Payload MessageDTO messageReq){
        MessageDTO storedMessage = chatMessageService.saveMessage(messageReq);
        messagingTemplate.convertAndSendToUser(
                String.valueOf(messageReq.getRecipientId()),
                String.format("/%s/queue/messages",messageReq.getRecipient_type()),
                MessageDTO.builder()
                        .id(storedMessage.getId())
                        .senderId(storedMessage.getSenderId())
                        .sender_type(storedMessage.getSender_type())
                        .seen_at(storedMessage.getSeen_at())
                        .sendAt(storedMessage.getSendAt())
                        .content(storedMessage.getContent())
                        .conversation_id(storedMessage.getConversation_id())
                        .isSeen(storedMessage.isSeen())
                        .build()
        );
    }
    @MessageMapping("/chat/update-message")
    public void updateMessage(@Payload MessageDTO messageReq){
        MessageDTO storedMessage = chatMessageService.updateMessage(messageReq);
        messagingTemplate.convertAndSendToUser(
                String.format("%s",messageReq.getRecipientId()),
                String.format("/%s/queue/messages",messageReq.getRecipient_type()),
                MessageDTO.builder()
                        .id(storedMessage.getId())
                        .recipient_type(storedMessage.getRecipient_type())
                        .recipientId(storedMessage.getRecipientId())
                        .senderId(storedMessage.getSenderId())
                        .sender_type(storedMessage.getSender_type())
                        .seen_at(storedMessage.getSeen_at())
                        .sendAt(storedMessage.getSendAt())
                        .content(storedMessage.getContent())
                        .conversation_id(storedMessage.getConversation_id())
                        .isSeen(storedMessage.isSeen())
                        .build()
        );
    }
    @GetMapping("/chat/get-connected-account")
    public ResponseEntity<?> getConnectedUser
            (@RequestParam("account_id") int account_id,
             @RequestParam("role_account") String role_account){
        return ResponseEntity.status(HttpStatus.OK).
                body(chatMessageService.getAccountConnected(account_id,role_account));
    }
    @GetMapping("/chat/last-message")
    public ResponseEntity<?> getLastMessage(@RequestParam("conversation_id") int conversation_id){
        return ResponseEntity.status(HttpStatus.OK).body(chatMessageService.lastMessageOfConversation(conversation_id));
    }
    @GetMapping("/chat/message-by-conversation-sender")
    public ResponseEntity<?> getMessageByConservationAndSender
            (@RequestParam("sender_id") int sender_id,
             @RequestParam("sender_type") String sender_type,
             @RequestParam("conversation_id") int conversation_id){
        return ResponseEntity.status(HttpStatus.OK).body(chatMessageService.
                getMessageByConservationAndSender(conversation_id,sender_id,sender_type));
    }
    @PostMapping("/chat/create-conversation")
    public ResponseEntity<?> createConversation(@RequestParam("sender_id") int sender_id,
                                                @RequestParam("sender_type") String sender_type,
                                                @RequestParam("recipient_id") int recipient_id,
                                                @RequestParam("recipient_type") String recipient_type)
    {
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setStatusCode(200);
        int conversationId = chatMessageService.createConversation(sender_id,sender_type,recipient_id,recipient_type);
        restResponse.setMessage("Conversation Id " + String.valueOf(conversationId));
        restResponse.setData(String.valueOf(conversationId));
        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
    }
    @GetMapping("/notification/get-notification-by-userId")
    ResponseEntity<?> getNotifications(@RequestParam("account_id")int account_id,@RequestParam("account_type") AccountEnum role_account){
        return ResponseEntity.status(HttpStatus.OK).body(notificationService.getNotificationByAccountIdAndRoleAccount(account_id,role_account));
    }
    @GetMapping("/chat/get-message-by-conversation-id")
    ResponseEntity<?> getMessageByConversationId(@RequestParam("conversation_id") int conversation_id){
        return ResponseEntity.status(HttpStatus.OK).body(chatMessageService.getMessagesByConversationId(conversation_id));
    }
}
