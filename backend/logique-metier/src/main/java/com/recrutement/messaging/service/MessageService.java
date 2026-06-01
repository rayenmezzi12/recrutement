package com.recrutement.messaging.service;

import com.recrutement.messaging.model.Message;
import com.recrutement.messaging.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public Message send(String sender, String recipient, String content) {
        return messageRepository.save(Message.builder()
                .senderUsername(sender)
                .recipientUsername(recipient)
                .content(content)
                .isRead(false)
                .sentAt(LocalDateTime.now())
                .build());
    }

    public List<Message> getConversation(String user1, String user2) {
        List<Message> messages = messageRepository.findConversation(user1, user2);
        messages.stream()
                .filter(m -> m.getRecipientUsername().equals(user1))
                .forEach(m -> {
                    m.setIsRead(true);
                    messageRepository.save(m);
                });
        return messages;
    }

    public long countUnread(String username) {
        return messageRepository.countByRecipientUsernameAndIsReadFalse(username);
    }
}
