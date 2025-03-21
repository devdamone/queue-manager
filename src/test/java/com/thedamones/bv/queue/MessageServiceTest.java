package com.thedamones.bv.queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;

import java.util.Optional;

import static com.thedamones.bv.queue.TestMessages.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Spy
    private GenericConversionService conversionService = new DefaultConversionService();

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        conversionService.addConverter(new MessageToMessageRecordConverter());
    }

    @Test
    void enqueueMessage_shouldSaveMessageAndPublishEvent() {
        Message message = createTestMessage();
        EnqueueMessageRecord enqueueMessageRecord = createTestEnqueueMessageRecord();
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        MessageRecord result = messageService.enqueueMessage(enqueueMessageRecord);

        assertTestMessageRecord(result);
        verify(messageRepository).save(any(Message.class));
        verify(eventPublisher).publishEvent(any(MessageEnqueuedEvent.class));
    }

    @Test
    void dequeueMessage_shouldReturnAndRemoveOldestMessage() {
        Message message = createTestMessage();
        when(messageRepository.findFirstByOrderByTimestampAsc()).thenReturn(Optional.of(message));

        MessageRecord result = messageService.dequeueMessage();

        assertTestMessageRecord(result);
        verify(messageRepository).delete(message);
    }

    @Test
    void dequeueMessage_whenEmpty_shouldThrowException() {
        when(messageRepository.findFirstByOrderByTimestampAsc()).thenReturn(Optional.empty());
        assertThrows(MessageNotFoundException.class, () -> messageService.dequeueMessage());
    }

    @Test
    void getMessageById_shouldReturnMessageRecord() {
        Message message = createTestMessage();
        when(messageRepository.findById(message.getId())).thenReturn(Optional.of(message));

        MessageRecord result = messageService.getMessageById(message.getId());

        assertTestMessageRecord(result);
    }

    @Test
    void getMessageById_whenNotFound_shouldThrowException() {
        Message message = createTestMessage();
        when(messageRepository.findById(message.getId())).thenReturn(Optional.empty());
        assertThrows(MessageNotFoundException.class, () -> messageService.getMessageById(message.getId()));
    }

    @Test
    void getQueueSize_shouldReturnQueueSize() {
        when(messageRepository.count()).thenReturn(1L);
        assertEquals(1, messageService.getQueueSize());
    }

}