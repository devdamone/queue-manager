package com.thedamones.bv.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/v2/queue")
public class QueueController {

    private final MessageService messageService;

    @Autowired
    public QueueController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Gets information about the queue.
     *
     * @return The queue information.
     */
    @GetMapping
    public EntityModel<QueueRecord> getQueueInfo() {
        long queueSize = messageService.getQueueSize();
        QueueRecord queueRecord = new QueueRecord(queueSize);

        return EntityModel.of(queueRecord,
                linkTo(methodOn(QueueController.class).getQueueInfo()).withSelfRel(),
                linkTo(methodOn(MessageController.class).enqueueMessage(null)).withRel("enqueue"),
                linkTo(methodOn(MessageController.class).dequeueMessage()).withRel("dequeue"),
                linkTo(methodOn(MessageController.class).getClass()).withRel("messages"));
    }
}
