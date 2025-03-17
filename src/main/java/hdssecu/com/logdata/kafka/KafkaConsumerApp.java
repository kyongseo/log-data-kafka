package hdssecu.com.logdata.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class KafkaConsumerApp {
    private static final String KAFKA_BROKER = "localhost:9092";
    private static final String[] TOPICS = {"topic1", "topic2", "topic3"};
    private static final String[] LOG_FILES = {"consum1.log", "consum2.log", "consum3.log"};

    public static void main(String[] args) {
        List<Thread> consumerThreads = new ArrayList<>();

        // 각 토픽에 대한 Consumer 실행
        for (int i = 0; i < TOPICS.length; i++) {
            String topic = TOPICS[i];
            String logFile = LOG_FILES[i];
            Thread thread = new Thread(() -> runConsumer(topic, logFile));
            thread.start();
            consumerThreads.add(thread);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down all consumers...");
            for (Thread thread : consumerThreads) {
                thread.interrupt();
            }
        }));
    }

    // Kafka Consumer 실행 함수
    private static void runConsumer(String topic, String logFile) {
        Properties props = new Properties();
        props.put("bootstrap.servers", KAFKA_BROKER);
        props.put("group.id", "consumer-group-" + topic);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));

        try (FileWriter writer = new FileWriter(logFile, true)) {
            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    String logLine = String.format("Received from %s: %s\n", topic, record.value());
                    writer.write(logLine);
                    writer.flush();
                    System.out.println(logLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }
}