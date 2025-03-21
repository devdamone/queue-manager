package com.thedamones.bv.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
public class V1QueueManagerController {

    private final MessageService messageService;

    @Autowired
    public V1QueueManagerController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/enqueue")
    public ResponseEntity<MessageRecord> enqueueMessage(@RequestBody EnqueueMessageRecord request) {
        MessageRecord enqueuedMessage = messageService.enqueueMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(enqueuedMessage);
    }

    @GetMapping("/dequeue")
    public ResponseEntity<MessageRecord> dequeueMessage() {
        MessageRecord dequeuedMessage = messageService.dequeueMessage();
        return ResponseEntity.ok(dequeuedMessage);
    }

    @GetMapping("/queue-size")
    public ResponseEntity<Long> getQueueSize() {
        long queueSize = messageService.getQueueSize();
        return ResponseEntity.ok(queueSize);
    }
}
