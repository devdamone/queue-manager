package com.thedamones.bv.queue;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a message entity stored in the queue.
 * This entity contains the text and a data size, used for simulating processing delays.
 */
@Entity
public class Message {

    /**
     * Unique identifier for the message.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The textual content of the message.
     */
    private String text;

    /**
     * The size of the message data, used to simulate processing delays.
     */
    private Integer dataSize;

    /**
     * Timestamp indicating when the message was added to the queue.
     */
    private Instant timestamp;

    /**
     * Default constructor for JPA.
     */
    public Message() {
    }

    /**
     * Constructs a new Message with the specified text and data size.
     *
     * @param text     The message text.
     * @param dataSize The size of the message data.
     */
    public Message(String text, Integer dataSize) {
        this.text = text;
        this.dataSize = dataSize;
        this.timestamp = Instant.now();
    }

    /**
     * Gets the message ID.
     *
     * @return The message ID.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets the message ID.
     *
     * @param id The message ID.
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Gets the message text.
     *
     * @return The message text.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the message text.
     *
     * @param text The message text.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Gets the message data size.
     *
     * @return The message data size.
     */
    public Integer getDataSize() {
        return dataSize;
    }

    /**
     * Sets the message data size.
     *
     * @param dataSize The message data size.
     */
    public void setDataSize(Integer dataSize) {
        this.dataSize = dataSize;
    }

    /**
     * Gets the message timestamp.
     *
     * @return The message timestamp.
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the message timestamp.
     *
     * @param timestamp The message timestamp.
     */
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

}