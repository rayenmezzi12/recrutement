package com.recrutement.messaging.repository;

import com.recrutement.messaging.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("""
            SELECT m FROM Message m WHERE
            (m.senderUsername = :user1 AND m.recipientUsername = :user2) OR
            (m.senderUsername = :user2 AND m.recipientUsername = :user1)
            ORDER BY m.sentAt ASC
            """)
    List<Message> findConversation(@Param("user1") String user1, @Param("user2") String user2);

    long countByRecipientUsernameAndIsReadFalse(String recipientUsername);
}
