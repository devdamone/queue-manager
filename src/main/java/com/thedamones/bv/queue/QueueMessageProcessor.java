package com.thedamones.bv.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
public class QueueMessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(QueueMessageProcessor.class);

    public void process(MessageRecord message) {
        logger.info("Processing message: {}", message.id());
        logger.info("Message received at: {} ({} milliseconds ago)", message.timestamp(), Duration.between(message.timestamp(), Instant.now()).toMillis());

        try {
            logger.info("Message data size: {}", message.dataSize());
            TimeUnit.MILLISECONDS.sleep(message.dataSize());
            logger.info("Message processed: {}", message.id());
        } catch (InterruptedException e) {
            logger.error("Message processing interrupted: {}", message.id());
        }
    }
}