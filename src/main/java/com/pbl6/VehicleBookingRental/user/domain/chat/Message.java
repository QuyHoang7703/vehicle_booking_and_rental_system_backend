package com.pbl6.VehicleBookingRental.user.domain.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "message")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "send_at")
    private Date sendAt;

    @Column(name = "is_seen", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isSeen;

    @Column(name = "seen_at")
    private Date seen_at;

    // Nhiều Message thuộc một Conversation
    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    // Nhiều Message có thể thuộc một Sender (Account) - ánh xạ theo sender_id
    @Column(name = "sender_id")
    private int senderId;
    @Column(name = "sender_type")
    private String sender_type;
    @Column(name = "recipient_id")
    private int recipientId;
    @Column(name = "recipient_type")
    private String recipient_type;
}
