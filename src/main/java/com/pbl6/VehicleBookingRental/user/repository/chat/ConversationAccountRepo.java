package com.pbl6.VehicleBookingRental.user.repository.chat;

import com.pbl6.VehicleBookingRental.user.domain.chat.ConversationAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConversationAccountRepo extends JpaRepository<ConversationAccount,Integer> {
    @Query("SELECT ca " +
            "FROM ConversationAccount ca " +
            "WHERE ca.conversation.id = :conversation_id" +
            " AND NOT  (ca.account.id = :account_id AND  ca.roleAccount = :role_account) ")
    public List<ConversationAccount> getConnectedAccount
            (@Param("conversation_id") int conversation_id,
             @Param("account_id")int account_id,
             @Param("role_account")String role_account);
    public List<ConversationAccount> findConversationAccountByAccountIdAndRoleAccount(int account_id,String role_account);

}
