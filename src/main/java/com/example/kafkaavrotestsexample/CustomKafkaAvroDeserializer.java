package com.example.kafkaavrotestsexample;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.Schema;

public class CustomKafkaAvroDeserializer extends KafkaAvroDeserializer {
    @Override
    public Object deserialize(String topic, byte[] bytes) {
        this.schemaRegistry = getMockClient();

        return super.deserialize(topic, bytes);
    }

    private static SchemaRegistryClient getMockClient() {
        return new MockSchemaRegistryClient() {
            @Override
            public synchronized Schema getById(int id) {
                return MessageAvro.SCHEMA$;
            }
        };
    }
}