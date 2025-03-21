package com.thedamones.bv.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.thedamones.bv.queue.TestMessages.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(V1QueueManagerController.class)
public class V1QueueManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getQueueSize_shouldReturnQueueSize() throws Exception {
        when(messageService.getQueueSize()).thenReturn(5L);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/queue-size"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void enqueueMessage_shouldReturnCreatedMessage() throws Exception {
        EnqueueMessageRecord request = createTestEnqueueMessageRecord();
        MessageRecord messageRecord = createTestMessageRecord();

        when(messageService.enqueueMessage(any(EnqueueMessageRecord.class))).thenReturn(messageRecord);

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/enqueue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(messageRecord)));
    }

    @Test
    void dequeueMessage_shouldReturnDequeuedMessage() throws Exception {
        MessageRecord messageRecord = createTestMessageRecord();
        when(messageService.dequeueMessage()).thenReturn(messageRecord);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/dequeue"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(messageRecord)));
    }
}
