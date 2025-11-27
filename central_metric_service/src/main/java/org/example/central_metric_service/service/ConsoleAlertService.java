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
    private final MeasurementLoggerService measurementLoggerService;

    @Override
    public void monitor(WarehouseMeasurement measurement) {
        val sensorType = measurement.getSensorId().substring(0, 1);
        val sensorValue = measurement.getValue();
        val thresholdValue = sensorProps.getThreshold()
                .get(sensorType);

        if (thresholdValue != null && thresholdValue.compareTo(sensorValue) < 0) {
            measurementLoggerService.printError(String.format("Threshold crossed!. Measurement: %s, threshold: %d", measurement, thresholdValue));
        } else {
            measurementLoggerService.printMessage(String.format("Measurement ok: %s, threshold: %d", measurement, thresholdValue));
        }
    }
}
