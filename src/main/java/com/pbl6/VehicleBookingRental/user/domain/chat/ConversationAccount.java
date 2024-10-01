package com.pbl6.VehicleBookingRental.user.domain.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "conversation_account")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "account_id", nullable = false)
    private int accountId;

    @Column(name = "conservation_id", nullable = false)
    private int conservationId;

    @Column(name = "role_account", length = 50)
    private String roleAccount;

    // Many-to-One relationship với Conversation
    @ManyToOne
    @JoinColumn(name = "conservation_id", insertable = false, updatable = false)
    private Conversation conversation;
}
