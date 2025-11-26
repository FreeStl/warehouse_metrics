package org.example.central_metric_service.config;

import lombok.val;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.example.central_metric_service.model.WarehouseMeasurement;
import org.example.central_metric_service.model.WarehouseMeasurement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;

@Configuration
public class KafkaConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value(value = "${kafka.groupId}")
    private String groupId;
    @Value(value = "${kafka.topic}")
    private String topic;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        val props = new HashMap<String, Object>();
        props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(props);
    }

    @Bean
    public ConsumerFactory<String, WarehouseMeasurement> consumerFactory() {
        val props = new HashMap<String, Object>();
        props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(GROUP_ID_CONFIG, groupId);
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        val jsonDeserializer = new JsonDeserializer<>(WarehouseMeasurement.class);
        jsonDeserializer.ignoreTypeHeaders();

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, WarehouseMeasurement> kafkaListenerContainerFactory() {
        val factory = new ConcurrentKafkaListenerContainerFactory<String, WarehouseMeasurement>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(1);
        //factory.getContainerProperties().setObservationEnabled(true);
        //factory.getContainerProperties().setObservationConvention(getListenerObservationConvention());
        return factory;
    }

    @Bean
    public NewTopic topic1() {
        return new NewTopic(topic, 1, (short) 1);
    }
}
