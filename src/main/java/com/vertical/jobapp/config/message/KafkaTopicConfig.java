package com.vertical.jobapp.config.message;

import org.apache.kafka.clients.admin.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.kafka.core.*;

import java.util.*;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value(value = "${spring.kafka.job-request-topic}")
    private String jobRequestTopic;
    @Value(value = "${spring.kafka.job-listing-topic}")
    private String jobListingTopic;
    @Value(value = "${spring.kafka.user-topic}")
    private String userTopic;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic jobRequestTopic() {
        return new NewTopic(jobRequestTopic, 1, (short) 1);
    }
    @Bean
    public NewTopic jobListingTopic() {
        return new NewTopic(jobListingTopic, 1, (short) 1);
    }
    @Bean
    public NewTopic userTopic() {
        return new NewTopic(userTopic, 1, (short) 1);
    }
}
