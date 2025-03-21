package com.thedamones.bv.queue;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestMessages {

    private static final UUID TEST_ID = UUID.randomUUID();
    private static final String TEST_MESSAGE_TEXT = "TEST_MESSAGE_TEXT";
    private static final int TEST_DATA_SIZE = 123;
    private static final Instant TEST_TIMESTAMP = Instant.now();

    public static Message createTestMessage() {
        Message message = new Message(TEST_MESSAGE_TEXT, TEST_DATA_SIZE);
        message.setId(TEST_ID);
        return message;
    }

    public static void assertTestMessageRecord(MessageRecord result) {
        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertEquals(TEST_MESSAGE_TEXT, result.text());
        assertEquals(TEST_DATA_SIZE, result.dataSize());
        assertNotNull(result.timestamp());
    }

    public static EnqueueMessageRecord createTestEnqueueMessageRecord() {
        return new EnqueueMessageRecord(TEST_MESSAGE_TEXT, TEST_DATA_SIZE);
    }

    public static MessageRecord createTestMessageRecord() {
        return new MessageRecord(TEST_ID, TEST_MESSAGE_TEXT, TEST_DATA_SIZE, TEST_TIMESTAMP);
    }

}
