package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.dto.chat_dto.MessageDTO;
import com.pbl6.VehicleBookingRental.user.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ChatMessageService chatMessageService;
    @MessageMapping("/chat/send-message")
    public void processMessage(@Payload MessageDTO messageDTO){
        MessageDTO storedMessage = chatMessageService.saveMessage(messageDTO);
        messagingTemplate.convertAndSendToUser(
                String.valueOf(storedMessage.getRecipientId()),
                String.format("/%s/queue/messages",storedMessage.getRecipient_type()),
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
    @MessageMapping("/chat/update-message")
    public void updateMessage(@Payload MessageDTO messageDTO){
        MessageDTO storedMessage = chatMessageService.updateMessage(messageDTO);
        messagingTemplate.convertAndSendToUser(
                String.format("%s",storedMessage.getRecipientId()),
                String.format("/%s/queue/messages",storedMessage.getRecipientId(),storedMessage.getRecipient_type()),
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
    @GetMapping("/chat/get-connected-account")
    public ResponseEntity<?> getConnectedUser
            (@RequestParam("conversation_id") int conversation_id,
             @RequestParam("account_id") int account_id,
             @RequestParam("role_account") String role_account){
        return ResponseEntity.status(HttpStatus.OK).
                body(chatMessageService.getAccountConnected(conversation_id,account_id,role_account));
    }

}
