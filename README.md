# Warehouse Metrics System
This system consists of two independent Spring Boot services communicating through Kafka.  
It collects warehouse sensor data over UDP and validate if it exceeds thresholds.

## Structure

### warehouse_service
This service listens for sensor metrics over UDP and forwards them to central_metric_service through Kafka.

To achive high availability and perfomance, it uses a producer–consumer pattern.
One dedicated listener thread receives UDP packets and pushes them into a concurrent queue. Worker threads consume messages from the queue and send them to a Kafka broker.

### central_metric_service
This service consumes warehouse sensor messages from Kafka and checks whether they exceed thresholds.

If a value crosses a threshold, it triggers alerts.

Thresholds can be configured using environment variables.

## How to run
1) run 'compose.yaml' to start kafka and both metric services:

>  docker compose up --build

**Attention: sometimes Kafka starts to soon and cannot connect to Zookeper. In that case please stop Zookeper and Kafka. 
Then first start Zookeper. After 10 seconds start Kafka.**

2) mock sencor request by sending commands to warehouse_service. Command example:
> '{"sensor_id": "t1", "value": 10}' | nc -u -w1 localhost 3344'

