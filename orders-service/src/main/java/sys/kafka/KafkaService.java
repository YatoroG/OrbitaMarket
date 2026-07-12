package sys.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaService {
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void sentToKafkaOrders(String topic, OrderEvent event) {
        kafkaTemplate.send(topic, event.orderId().toString(), event);
        log.info("[Kafka] Отправлено событие: {}", event);
    }
}
