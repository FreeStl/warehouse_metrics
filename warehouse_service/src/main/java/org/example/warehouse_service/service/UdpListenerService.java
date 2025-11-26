package org.example.warehouse_service.service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.warehouse_service.model.SensorMeasurement;
import org.example.warehouse_service.model.WarehouseMeasurement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.*;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;
import static java.util.concurrent.TimeUnit.SECONDS;


@Service
@Slf4j
@RequiredArgsConstructor
public class UdpListenerService implements SmartLifecycle {

    private volatile boolean running = false;

    private final KafkaTemplate<String, WarehouseMeasurement> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Value(value = "${kafka.topic}")
    private String topic;

    private ExecutorService temperatureListener;

    private ExecutorService humidityListener;

    private ExecutorService workerExecutor;

    private final BlockingQueue<byte[]> queue = new LinkedBlockingQueue<>(1000);

    private String warehouseId ;


    @Override
    public void start() {
        running = true;
        warehouseId = Integer.toString(ThreadLocalRandom.current().nextInt(100));

        temperatureListener = newSingleThreadExecutor(Thread.ofVirtual().factory());
        temperatureListener.submit(() -> portListener(3344));

        humidityListener = newSingleThreadExecutor(Thread.ofVirtual().factory());
        humidityListener.submit(() -> portListener(3355));

        workerExecutor = newVirtualThreadPerTaskExecutor();
        workerExecutor.submit(this::workerTask);
    }

    @Override
    public void stop() {
        running = false;

        shutdownExecutor(temperatureListener);
        shutdownExecutor(humidityListener);
        shutdownExecutor(workerExecutor);

        System.out.println("UdpKafkaBridge stopped.");
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    private void portListener(int port){
        try (val channel = DatagramChannel.open()) {
            channel.bind(new InetSocketAddress(port));
            channel.configureBlocking(true);

            val buffer = ByteBuffer.allocate(2048);

            while (running) {
                buffer.clear();
                channel.receive(buffer);
                buffer.flip();

                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);

                queue.put(data);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void workerTask() {
        while (true) {
            try {
                val rawData = queue.take();
                val sensorData = objectMapper.readValue(rawData, SensorMeasurement.class);
                val warehouseData = new WarehouseMeasurement(sensorData, warehouseId);
                log.info(warehouseData.toString());
                kafkaTemplate.send(topic, warehouseData);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

    public static void shutdownExecutor(ExecutorService ex) {
        ex.shutdown();
        try {
            if (!ex.awaitTermination(3, SECONDS)) {
                ex.shutdownNow();
            }
        } catch (InterruptedException ignored) {
            ex.shutdownNow();
        }
    }



}

