package com.pbl6.VehicleBookingRental.user.service.impl;

import com.nimbusds.jose.util.Pair;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.chat.Conversation;
import com.pbl6.VehicleBookingRental.user.domain.chat.ConversationAccount;
import com.pbl6.VehicleBookingRental.user.domain.chat.Message;
import com.pbl6.VehicleBookingRental.user.dto.chat_dto.MessageDTO;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRepository;
import com.pbl6.VehicleBookingRental.user.repository.chat.ConversationAccountRepo;
import com.pbl6.VehicleBookingRental.user.repository.chat.ConversationRepo;
import com.pbl6.VehicleBookingRental.user.repository.chat.MessageRepo;
import com.pbl6.VehicleBookingRental.user.service.ChatMessageService;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {
    @Autowired
    private ConversationRepo conversationRepo;
    @Autowired
    private  MessageRepo messageRepo;
    @Autowired
    private ConversationAccountRepo conversationAccountRepo;
    @Autowired
    private AccountRepository accountRepository;
    @Override
    public List<MessageDTO> getMessageByConservationAndSender(int conversation_id, int sender_id,String sender_type) {
        List<Message> messages = messageRepo.findMessagesByConversationAndSender(conversation_id,sender_id,sender_type);

        List<MessageDTO> messageDTOS = new ArrayList<>();
        if(messages !=null){
            for(Message message: messages){
                MessageDTO messageDTO = new MessageDTO();
                messageDTO.setId(message.getId());
                messageDTO.setSenderId(message.getSenderId());
                messageDTO.setSender_type(message.getSender_type());
                messageDTO.setContent(message.getContent());
                messageDTO.setSeen(message.isSeen());
                messageDTO.setSeen_at(message.getSeen_at());
                messageDTO.setSendAt(message.getSendAt());
                messageDTO.setConversation_id(message.getConversation().getId());

                messageDTOS.add(messageDTO);
            }
        }

        return messageDTOS;
    }

    @Override
    public List<Pair<Integer, String>> getAccountConnected(int account_id, String role_account) {
        List<ConversationAccount> connectedAccount = new ArrayList<>();
        // Find conversation relate account_id
        List<ConversationAccount> conversationByAccountId = conversationAccountRepo.getConversationAccountByAccount(account_id,role_account);

        // Each conversation , get connected account
        if(conversationByAccountId != null){
            for(ConversationAccount i : conversationByAccountId){
                List<ConversationAccount> conversationAccountList =
                        conversationAccountRepo.getConnectedAccountWithConversation
                                (i.getConversation().getId(),account_id,role_account);
                connectedAccount.addAll(conversationAccountList);
            }
    }

        return Optional.ofNullable(connectedAccount)
                .orElse(Collections.emptyList())
                .stream().map(conversationAccount -> Pair.of(conversationAccount.getAccount().getId(),conversationAccount.getRoleAccount()) )
                .collect(Collectors.toList());
    }
    @Override
    public MessageDTO lastMessageOfConversation(int conversation_id) {
        MessageDTO messageDTO = new MessageDTO();
        Message lastMessage = messageRepo.findLastMessageOfSenderId(conversation_id);
        if(lastMessage !=null){
            messageDTO.setId(lastMessage.getId());
            messageDTO.setSenderId(lastMessage.getSenderId());
            messageDTO.setSender_type(lastMessage.getSender_type());
            messageDTO.setContent(lastMessage.getContent());
            messageDTO.setSeen(lastMessage.isSeen());
            messageDTO.setSeen_at(lastMessage.getSeen_at());
            messageDTO.setSendAt(lastMessage.getSendAt());
            messageDTO.setConversation_id(lastMessage.getConversation().getId());
        }
        return messageDTO;
    }

    @Override
    public MessageDTO saveMessage(MessageDTO messageDTO) {
        Message message = new Message();
        message.setContent(messageDTO.getContent());
        message.setSendAt(messageDTO.getSendAt());
        message.setSeen_at(messageDTO.getSeen_at());
        message.setSeen(messageDTO.isSeen());
        message.setSender_type(messageDTO.getSender_type());
        message.setSenderId(messageDTO.getSenderId());

        Optional<Conversation> conversation = conversationRepo.findById(messageDTO.getConversation_id());
        if(conversation.isPresent()){
            message.setConversation(conversation.get());
        }

        try{
            messageRepo.save(message);
        }catch (Exception e){
            System.out.println(e.getLocalizedMessage());
        }
        return messageDTO;
    }

    @Override
    public MessageDTO updateMessage(MessageDTO messageDTO) {
        Optional<Message> message = messageRepo.findById(messageDTO.getId());
        if(message.isPresent()){
            try{
                message.get().setContent(messageDTO.getContent());
                message.get().setSendAt(messageDTO.getSendAt());
                message.get().setSeen_at(messageDTO.getSeen_at());
                message.get().setSeen(messageDTO.isSeen());
                message.get().setSender_type(messageDTO.getSender_type());
                message.get().setSenderId(messageDTO.getSenderId());

                Optional<Conversation> conversation = conversationRepo.findById(messageDTO.getConversation_id());
                if(conversation.isPresent()){
                    message.get().setConversation(conversation.get());
                }
                messageRepo.save(message.get());
            }catch (Exception e){
                System.out.println(e.getLocalizedMessage());
            }
        }
        return messageDTO;
    }

    @Override
    @Transactional
    public Integer createConversation(int senderId, String senderType, int recipientId, String recipientType) {
        try {
            Optional<Integer> conversationId = conversationAccountRepo.findExistingConversation(senderId,senderType,recipientId,recipientType);
            if(conversationId.isPresent()){
                return conversationId.get();
            }
            // Create conversation
            Conversation conversation = new Conversation();
            conversation.setCreateAt(new Date());
            Conversation savedConversation = conversationRepo.save(conversation);

            // Find accounts
            List<Account> accounts = accountRepository.findAllById(Arrays.asList(senderId, recipientId));
            if (accounts.size() != 2) {
                throw new ApplicationException("One or both accounts not found!");
            }

            Account accountSender = accounts.stream().filter(acc -> acc.getId() == senderId).findFirst().orElseThrow(() -> new ApplicationException("Sender not found!"));
            Account accountRecipient = accounts.stream().filter(acc -> acc.getId() == recipientId).findFirst().orElseThrow(() -> new ApplicationException("Recipient not found!"));

            // Create conversation accounts
            ConversationAccount conversationAccount1 = new ConversationAccount();
            conversationAccount1.setAccount(accountSender);
            conversationAccount1.setRoleAccount(senderType);
            conversationAccount1.setConversation(savedConversation);

            ConversationAccount conversationAccount2 = new ConversationAccount();
            conversationAccount2.setAccount(accountRecipient);
            conversationAccount2.setRoleAccount(recipientType);
            conversationAccount2.setConversation(savedConversation);

            // Save conversation accounts
            conversationAccountRepo.saveAll(Arrays.asList(conversationAccount1, conversationAccount2));

            return savedConversation.getId();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }
}
