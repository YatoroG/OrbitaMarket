package sys.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {
    private static final String TOPIC = "order-payment-requests";
    private final OrderPaymentProcessor paymentProcessor;

    @KafkaListener(topics = TOPIC, groupId = "payments-backend-group")
    public void handleEvent(String message) {
        log.info("Получено событие: {}", message);
        paymentProcessor.process(message);
    }
}
