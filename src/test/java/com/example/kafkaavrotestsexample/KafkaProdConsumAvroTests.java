package com.example.kafkaavrotestsexample;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@EmbeddedKafka(
    partitions = 1,
    topics = "some-topic",
    brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"}
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class KafkaProdConsumAvroTests {
    @Autowired
    KafkaProducer producer;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    KafkaProperties kafkaProperties;

    @Test
    void whenProducerSendMessage_shouldConsumerReceiveMessage() {
        DefaultKafkaConsumerFactory<String, MessageAvro> consumerFactory = new DefaultKafkaConsumerFactory<>
            (kafkaProperties.buildConsumerProperties());
        Consumer<String, MessageAvro> consumer = consumerFactory.createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "some-topic");

        producer.send();

        var consumerRecords = KafkaTestUtils.getRecords(consumer);

        assertThat(consumerRecords.records("some-topic"))
            .filteredOn(consumerRecord -> consumerRecord.value().getContent().equals("a message"))
            .hasSize(1);
    }

    @Test
    void givenConsumerPropsCustomized_whenProducerSendMessage_shouldConsumerReceiveMessage() {
        var consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);

        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://no-action");
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CustomKafkaAvroDeserializer.class);
        consumerProps.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        DefaultKafkaConsumerFactory<String, MessageAvro> consumerFactory = new DefaultKafkaConsumerFactory<>
            (consumerProps);

        Consumer<String, MessageAvro> consumer = consumerFactory.createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "some-topic");

        producer.send();

        var consumerRecords = KafkaTestUtils.getRecords(consumer);

        assertThat(consumerRecords.records("some-topic"))
            .filteredOn(consumerRecord -> consumerRecord.value().getContent().equals("a message"))
            .hasSize(1);
    }
}
