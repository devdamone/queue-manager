package com.thedamones.bv.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/v2/queue/messages")
public class MessageController {

    private final MessageService messageService;
    private final MessageModelAssembler messageModelAssembler;

    @Autowired
    public MessageController(MessageService messageService, MessageModelAssembler messageModelAssembler) {
        this.messageService = messageService;
        this.messageModelAssembler = messageModelAssembler;
    }

    /**
     * Adds a message to the queue.
     *
     * @param request The message to enqueue.
     * @return The enqueued message.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<MessageRecord> enqueueMessage(@RequestBody EnqueueMessageRecord request) {
        MessageRecord enqueuedMessage = messageService.enqueueMessage(request);
        return messageModelAssembler.toModel(enqueuedMessage);
    }

    /**
     * Gets and removes a message from the queue.  This message will be deleted without being processed.
     *
     * @return The dequeued message.
     */
    @DeleteMapping("/first")
    public EntityModel<MessageRecord> dequeueMessage() {
        MessageRecord dequeuedMessage = messageService.dequeueMessage();
        return messageModelAssembler.toModel(dequeuedMessage);
    }

    @DeleteMapping
    public EntityModel<QueueRecord> clearQueue() {
        messageService.clearQueue();

        QueueRecord queueRecord = new QueueRecord(0);

        return EntityModel.of(queueRecord,
                linkTo(methodOn(QueueController.class).getQueueInfo()).withSelfRel(),
                linkTo(methodOn(MessageController.class).enqueueMessage(null)).withRel("enqueue"),
                linkTo(methodOn(MessageController.class).dequeueMessage()).withRel("dequeue"),
                linkTo(methodOn(MessageController.class).getClass()).withRel("messages"));
    }

    /**
     * Gets a message by its ID.
     *
     * @param id The ID of the message.
     * @return The message.
     */
    @GetMapping("/{id}")
    public EntityModel<MessageRecord> getMessage(@PathVariable UUID id) {
        MessageRecord message = messageService.getMessageById(id);
        return messageModelAssembler.toModel(message);
    }

}
