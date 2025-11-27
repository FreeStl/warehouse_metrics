package org.example.warehouse_service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.warehouse_service.model.SensorMeasurement;
import org.example.warehouse_service.model.WarehouseMeasurement;
import org.example.warehouse_service.service.KafkaPublisherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.BlockingQueue;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdMessageProcessorTest {
    private KafkaPublisherService kafkaPublisherService;
    private BlockingQueue<byte[]> queue;
    private ObjectMapper objectMapper;

    private UpdMessageProcessor processor;

    @BeforeEach
    void setUp() {
        kafkaPublisherService = mock(KafkaPublisherService.class);
        queue = mock(BlockingQueue.class);
        objectMapper = new ObjectMapper();
        processor = new UpdMessageProcessor(kafkaPublisherService, queue, objectMapper, "W1");
    }

    @Test
    void happyPath() throws JsonProcessingException, InterruptedException {
        var measurement = new SensorMeasurement("t1", 10L);
        byte[] rawData = objectMapper.writeValueAsBytes(measurement);

        when(queue.take())
                .thenReturn(rawData)
                // throw exception on second call to break while(true) loop
                .thenThrow(InterruptedException.class);

        assertThrows(RuntimeException.class, processor::process);

        var captor = ArgumentCaptor.forClass(WarehouseMeasurement.class);
        verify(kafkaPublisherService, times(1))
                .publish(captor.capture());

        WarehouseMeasurement result = captor.getValue();

        assertEquals("W1", result.getWarehouseId());
        assertEquals("t1", result.getSensorId());
        assertEquals(10L, result.getValue());
    }

    @Test
    void invalidInput() throws JsonProcessingException, InterruptedException {
        byte[] rawData = objectMapper.writeValueAsBytes("invalid_data");

        when(queue.take())
                .thenReturn(rawData);

        assertThrows(RuntimeException.class, processor::process);
    }
}