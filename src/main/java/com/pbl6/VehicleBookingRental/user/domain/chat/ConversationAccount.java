package com.pbl6.VehicleBookingRental.user.domain.chat;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
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


    @Column(name = "role_account", length = 50)
    private String roleAccount;

    // Many-to-One relationship với Conversation
    @ManyToOne
    @JoinColumn(name = "conversation_id",nullable = false)
    private Conversation conversation;
    @ManyToOne
    @JoinColumn (name = "account_id", nullable = false)
    private Account account;
}
