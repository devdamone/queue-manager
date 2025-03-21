package com.thedamones.bv.queue;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class QueueManager {

    private static final Logger logger = LoggerFactory.getLogger(QueueManager.class);

    private final MessageService messageService;
    private final QueueMessageProcessor messageProcessor;

    private volatile boolean running = true;
    private Thread processingThread;
    private final Object processingLock = new Object();


    @Autowired
    public QueueManager(MessageService messageService, QueueMessageProcessor messageProcessor) {
        this.messageService = messageService;
        this.messageProcessor = messageProcessor;
    }

    @PostConstruct
    public void startProcessing() {
        processingThread = new Thread(this::processingLoop);
        processingThread.start();
    }

    @PreDestroy
    public void stopProcessing() {
        running = false;
        processingThread.interrupt();
    }

    public void processingLoop() {
        while (running) {
            try {
                MessageRecord messageRecord = messageService.dequeueMessage();
                messageProcessor.process(messageRecord);
            } catch (MessageNotFoundException e) {
                logger.info("No messages to process. Waiting for notification.");
                awaitProcessing();
                logger.info("Processing has been notified.");
            } catch (Exception e) {
                logger.error("Error processing next message.", e);
            }
        }
    }

    private void awaitProcessing() {
        synchronized (processingLock) {
            try {
                logger.info("Awaiting processing");
                processingLock.wait();
            } catch (InterruptedException e) {
                // TODO should either just log this or let the queue manager handle the interrupted exception
                throw new RuntimeException(e);
            }
        }
    }

    private void notifyProcessing() {
        synchronized (processingLock) {
            logger.info("Notifying processing");
            processingLock.notify();
        }
    }

    @TransactionalEventListener
    public void handleMessageEnqueuedEvent(MessageEnqueuedEvent event) {
        logger.info("Message enqueued event received: {}", event.getMessageRecord().id());
        notifyProcessing();
    }

}
