package org.example.warehouse_service.service;

import org.example.warehouse_service.model.SensorMeasurement;
import org.example.warehouse_service.model.WarehouseMeasurement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaPublisherServiceTest {
    @Mock
    KafkaTemplate<String, WarehouseMeasurement> kafkaTemplate;
    @InjectMocks
    KafkaPublisherService kafkaPublisherService;

    @Test
    void happyPath() {
        var sensorMeasurement = new SensorMeasurement("t1", 10L);
        var warehouseMeasurement = new WarehouseMeasurement(sensorMeasurement, "W1");
        kafkaPublisherService.publish(warehouseMeasurement);

        var captor = ArgumentCaptor.forClass(WarehouseMeasurement.class);
        verify(kafkaTemplate, times(1))
                .send(any(), captor.capture());

        assertEquals(warehouseMeasurement, captor.getValue());
    }

}