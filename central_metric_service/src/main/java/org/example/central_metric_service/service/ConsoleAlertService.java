package org.example.central_metric_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.central_metric_service.model.SensorProps;
import org.example.central_metric_service.model.WarehouseMeasurement;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsoleAlertService implements AlertService {
    private final SensorProps sensorProps;

    @Override
    public void monitor(WarehouseMeasurement measurement) {
        val sensorType = measurement.getSensorId().substring(0, 1);
        val sensorValue = measurement.getValue();
        val thresholdValue = sensorProps.getThreshold()
                .get(sensorType);

        if (thresholdValue != null && thresholdValue.compareTo(sensorValue) > 0) {
            log.error("Threshold crossed!. Measurement: {}, threshold: {}", measurement, thresholdValue);
        } else {
            log.info("Measurement ok: {}, threshold: {}", measurement, thresholdValue);
        }
    }
}
