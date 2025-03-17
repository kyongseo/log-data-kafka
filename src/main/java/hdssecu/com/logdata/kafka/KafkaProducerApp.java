package hdssecu.com.logdata.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class KafkaProducerApp {
    private static final String LOG_FILE = "/home/ks/work/log_maker/log_data.log";
    private static final String KAFKA_BROKER = "localhost:9092";

    public static void main(String[] args) {

        // config 파일 작성
        Properties props = new Properties();
        props.put("bootstrap.servers", KAFKA_BROKER);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("acks", "all");

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        Path logFilePath = Paths.get(LOG_FILE);

        try (BufferedReader reader = Files.newBufferedReader(logFilePath)) {
            String line;
            while (true) {
                line = reader.readLine();
                if (line == null) {
                    Thread.sleep(100);
                    continue;
                }

                String[] parts = line.split(", ");
                if (parts.length != 3) continue;

                String topic = parts[0].split(": ")[1].trim();
                String message = parts[1].split(": ")[1].trim();
                String createdAt = parts[2].split(": ")[1].trim();

                String kafkaMessage = message + "," + createdAt;

                ProducerRecord<String, String> record = new ProducerRecord<>(topic, kafkaMessage);
                producer.send(record);

                System.out.println("Sent to Kafka: [" + topic + "] " + kafkaMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }
}