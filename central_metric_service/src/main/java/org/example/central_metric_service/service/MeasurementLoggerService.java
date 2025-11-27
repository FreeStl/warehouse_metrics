package org.example.central_metric_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MeasurementLoggerService {
    public void printError(String error) {
        log.error(error);
    }

    public void printMessage(String message) {
        log.info(message);
    }
}
