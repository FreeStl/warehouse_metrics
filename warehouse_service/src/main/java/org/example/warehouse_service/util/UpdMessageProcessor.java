package org.example.warehouse_service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.warehouse_service.model.SensorMeasurement;
import org.example.warehouse_service.model.WarehouseMeasurement;
import org.example.warehouse_service.service.KafkaPublisherService;

import java.util.concurrent.BlockingQueue;

@Slf4j
@AllArgsConstructor
@Setter
public class UpdMessageProcessor {
    private final KafkaPublisherService kafkaPublisherService;
    private final BlockingQueue<byte[]> queue;
    private final ObjectMapper objectMapper;
    private final String warehouseId;

    public void process() {
        while (true) {
            try {
                val rawData = queue.take();
                val sensorData = objectMapper.readValue(rawData, SensorMeasurement.class);
                val warehouseData = new WarehouseMeasurement(sensorData, warehouseId);
                kafkaPublisherService.publish(warehouseData);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }
}
