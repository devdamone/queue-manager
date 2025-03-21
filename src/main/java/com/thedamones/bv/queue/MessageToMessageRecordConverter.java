package com.thedamones.bv.queue;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MessageToMessageRecordConverter implements Converter<Message, MessageRecord> {

    @Override
    public MessageRecord convert(Message source) {
        return new MessageRecord(
                source.getId(),
                source.getText(),
                source.getDataSize(),
                source.getTimestamp()
        );
    }
}
