package org.example.warehouse_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.warehouse_service.model.WarehouseMeasurement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaPublisherService {

    private final KafkaTemplate<String, WarehouseMeasurement> kafkaTemplate;

    @Value(value = "${kafka.topic}")
    private String topic;

    public void publish(WarehouseMeasurement measurement) {
        kafkaTemplate.send(topic, measurement);
        log.info("Measurement sent to Kafka: {}", measurement);
    }
}
