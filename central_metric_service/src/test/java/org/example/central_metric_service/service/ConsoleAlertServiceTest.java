package org.example.central_metric_service.service;

import org.example.central_metric_service.model.SensorProps;
import org.example.central_metric_service.model.WarehouseMeasurement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsoleAlertServiceTest {
    @Mock
    private SensorProps sensorProps;
    @Mock
    private MeasurementLoggerService measurementLoggerService;
    @InjectMocks
    ConsoleAlertService consoleAlertService;

    @Test
    void thresholdExceed () {
        var warehouseMeasurement = new WarehouseMeasurement("W1", "t1", 40L);

        when(sensorProps.getThreshold())
                .thenReturn(Map.of("t", 35L, "h", 50L));

        consoleAlertService.monitor(warehouseMeasurement);

        verify(measurementLoggerService).printError(anyString());
    }

    @Test
    void thresholdNotExceed () {
        var warehouseMeasurement = new WarehouseMeasurement("W1", "t1", 30L);

        when(sensorProps.getThreshold())
                .thenReturn(Map.of("t", 35L, "h", 50L));

        consoleAlertService.monitor(warehouseMeasurement);

        verify(measurementLoggerService).printMessage(anyString());
    }


}