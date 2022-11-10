package com.example.kafkaavrotestsexample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaProducer {
    @Autowired
    private KafkaTemplate<String, MessageAvro> template;

    public void send() {
        var message = MessageAvro.newBuilder()
            .setContent("a message")
            .build();

        template.send("some-topic", message);
        log.info("sent message");
    }
}
