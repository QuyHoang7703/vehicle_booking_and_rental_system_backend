package com.pbl6.VehicleBookingRental.user.dto.chat_dto;

import com.pbl6.VehicleBookingRental.user.domain.chat.Conversation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO {
    private int id;
    private String content;
    private Date sendAt;
    private boolean isSeen;
    private Date seen_at;
    private int conversation_id;
    private int senderId;
    private String sender_type;
    private int recipientId;
    private String recipient_type;
}
