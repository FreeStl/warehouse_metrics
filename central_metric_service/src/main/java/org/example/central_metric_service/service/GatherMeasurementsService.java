package org.example.central_metric_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.central_metric_service.model.WarehouseMeasurement;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GatherMeasurementsService {

    private final AlertService alertService;

    @KafkaListener(topics = "${kafka.topic}", groupId = "${kafka.groupId}")
    public void updateTaskStateResponse(@Payload WarehouseMeasurement message) {
        log.info("Received measurement: {}", message);
        alertService.monitor(message);
    }


}
