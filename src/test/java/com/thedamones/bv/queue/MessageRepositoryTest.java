package com.thedamones.bv.queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    private Message message1;

    @BeforeEach
    void clearDatabase() {
        messageRepository.deleteAll();
    }

    void saveMessages() {
        message1 = new Message("Message 1", 1000);
        message1.setTimestamp(Instant.now().minusSeconds(30)); // set timestamp a little earlier to ensure it is first
        Message message2 = new Message("Message 2", 2000);
        message1.setTimestamp(Instant.now().minusSeconds(15)); // set timestamp a little (less) earlier to ensure it is in the middle
        Message message3 = new Message("Message 3", 3000);

        messageRepository.save(message1);
        messageRepository.save(message2);
        messageRepository.save(message3);
    }

    @Test
    void findFirstByOrderByTimestampAsc_shouldReturnOldestMessage() {
        saveMessages();
        Optional<Message> oldestMessage = messageRepository.findFirstByOrderByTimestampAsc();
        assertTrue(oldestMessage.isPresent());
        assertEquals(message1, oldestMessage.get());
    }

    @Test
    void findFirstByOrderByTimestampAsc_whenEmpty_shouldReturnEmptyOptional() {
        Optional<Message> oldestMessage = messageRepository.findFirstByOrderByTimestampAsc();
        assertTrue(oldestMessage.isEmpty());
    }
}
