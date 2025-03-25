package com.thedamones.bv.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final MessageRepository messageRepository;
    private final ConversionService conversionService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public MessageService(MessageRepository messageRepository, ConversionService conversionService, ApplicationEventPublisher eventPublisher) {
        this.messageRepository = messageRepository;
        this.conversionService = conversionService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Enqueues a new message.
     *
     * @param request The message request record.
     * @return The enqueued message record.
     */
    @Transactional
    public MessageRecord enqueueMessage(EnqueueMessageRecord request) {
        return createMessage(request)
                .map(saveMessage())
                .map(toMessageRecord())
                .map(publishEnqueueEvent())
                .orElseThrow(this::messageEnqueueException);
    }

    /**
     * Dequeues the oldest message from the queue.
     *
     * @return The dequeued message record.
     * @throws MessageNotFoundException If the queue is empty.
     */
    @Transactional
    public MessageRecord dequeueMessage() {
        return messageRepository.findFirstByOrderByTimestampAsc()
                .map(deleteMessage())
                .map(toMessageRecord())
                .orElseThrow(this::emptyQueueException);
    }

    @Transactional
    public void clearQueue() {
        messageRepository.deleteAll();
    }

    /**
     * Gets a message by its ID.
     *
     * @param id The ID of the message.
     * @return The message record.
     * @throws MessageNotFoundException if the message is not found.
     */
    public MessageRecord getMessageById(UUID id) {
        return messageRepository.findById(id)
                .map(toMessageRecord())
                .orElseThrow(messageNotFoundException(id));
    }

    /**
     * Gets the current size of the message queue.
     *
     * @return The queue size.
     */
    public long getQueueSize() {
        return messageRepository.count();
    }

    private Optional<Message> createMessage(EnqueueMessageRecord request) {
        return Optional.of(new Message(request.text(), request.dataSize()));
    }

    private Function<Message, Message> saveMessage() {
        return messageRepository::save;
    }

    private Function<MessageRecord, MessageRecord> publishEnqueueEvent() {
        return message -> {
            eventPublisher.publishEvent(new MessageEnqueuedEvent(message));
            return message;
        };
    }

    private Function<Message, Message> deleteMessage() {
        return message -> {
            messageRepository.delete(message);
            return message;
        };
    }

    private Function<Message, MessageRecord> toMessageRecord() {
        return message -> conversionService.convert(message, MessageRecord.class);
    }

    private MessageEnqueueException messageEnqueueException() {
        return new MessageEnqueueException("Failed to enqueue message");
    }

    private MessageNotFoundException emptyQueueException() {
        return new MessageNotFoundException("Queue is empty");
    }

    private Supplier<MessageNotFoundException> messageNotFoundException(UUID id) {
        return () -> new MessageNotFoundException("Message with ID " + id + " not found");
    }

}

