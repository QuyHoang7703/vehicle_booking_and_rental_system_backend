package com.pbl6.VehicleBookingRental.user.repository.chat;

import com.pbl6.VehicleBookingRental.user.domain.chat.ConversationAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationAccountRepo extends JpaRepository<ConversationAccount,Integer> {

    @Query("SELECT ca " +
            "FROM ConversationAccount  ca " +
            "WHERE ca.account.id = :account_id AND ca.roleAccount = :role_account")
    public List<ConversationAccount> getConversationAccountByAccount(@Param("account_id") int account_id,
                                                                     @Param("role_account") String role_account);

    @Query("SELECT ca " +
            "FROM ConversationAccount ca " +
            "WHERE ca.conversation.id = :conversation_id" +
            " AND NOT  (ca.account.id = :account_id AND  ca.roleAccount = :role_account) ")
    public List<ConversationAccount> getConnectedAccountWithConversation
            (@Param("conversation_id") int conversation_id,
             @Param("account_id") int account_id,
             @Param("role_account") String role_account);
    @Query("""
        SELECT ca1.conversation.id 
        FROM ConversationAccount ca1
        JOIN ConversationAccount ca2 ON ca1.conversation.id = ca2.conversation.id
        WHERE ca1.account.id = :senderId AND ca1.roleAccount = :senderType
          AND ca2.account.id = :recipientId AND ca2.roleAccount = :recipientType
    """)
    Optional<Integer> findExistingConversation(
            @Param("senderId") int senderId,
            @Param("senderType") String senderType,
            @Param("recipientId") int recipientId,
            @Param("recipientType") String recipientType
    );
}