package org.example.central_metric_service.service;

import org.example.central_metric_service.model.WarehouseMeasurement;

public interface AlertService {
    void monitor(WarehouseMeasurement measurement);
}
