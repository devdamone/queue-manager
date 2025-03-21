package com.thedamones.bv.queue;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class MessageModelAssembler implements RepresentationModelAssembler<MessageRecord, EntityModel<MessageRecord>> {

    @Override
    public EntityModel<MessageRecord> toModel(MessageRecord message) {
        return EntityModel.of(message,
                linkTo(methodOn(MessageController.class).getMessage(message.id())).withSelfRel(),
                linkTo(methodOn(QueueController.class).getQueueInfo()).withRel("queue"));
    }
}
