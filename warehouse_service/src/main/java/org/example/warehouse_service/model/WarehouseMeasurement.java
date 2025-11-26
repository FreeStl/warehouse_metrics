package org.example.warehouse_service.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WarehouseMeasurement extends SensorMeasurement {
    private String warehouseId;

    public WarehouseMeasurement() {
        super();
    }

    public WarehouseMeasurement(SensorMeasurement sensorMeasurement, String warehouseId) {
        super(sensorMeasurement.getSensorId(), sensorMeasurement.getValue());
        this.warehouseId = warehouseId;
    }


}
