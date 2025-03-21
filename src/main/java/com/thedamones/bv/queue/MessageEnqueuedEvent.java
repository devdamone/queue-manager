package com.thedamones.bv.queue;

/**
 * Event published after a message is successfully enqueued.
 */
public class MessageEnqueuedEvent {

    private final MessageRecord messageRecord;

    public MessageEnqueuedEvent(MessageRecord messageRecord) {
        this.messageRecord = messageRecord;
    }

    public MessageRecord getMessageRecord() {
        return messageRecord;
    }
}
