package sn.travel.notification_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for the notification-service.
 * <p>
 * Listens on:
 * - notification.subscription.queue (SubscriptionCreatedEvent from travel-service)
 * - notification.payment.queue (PaymentCompletedEvent from payment-service)
 */
@Configuration
public class RabbitMQConfig {

    // ---- Exchanges (declared by other services, re-declared here for binding) ----
    public static final String SUBSCRIPTION_EXCHANGE = "subscription.exchange";
    public static final String PAYMENT_EXCHANGE = "payment.exchange";

    // ---- Queues (dedicated to notification-service, separate from payment-service queues) ----
    public static final String NOTIFICATION_SUBSCRIPTION_QUEUE = "notification.subscription.queue";
    public static final String NOTIFICATION_PAYMENT_QUEUE = "notification.payment.queue";

    // ---- Routing Keys ----
    public static final String SUBSCRIPTION_CREATED_KEY = "subscription.created";
    public static final String PAYMENT_COMPLETED_KEY = "payment.#";

    // ---- Exchange Beans ----

    @Bean
    public TopicExchange subscriptionExchange() {
        return new TopicExchange(SUBSCRIPTION_EXCHANGE);
    }

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(PAYMENT_EXCHANGE);
    }

    // ---- Queue Beans ----

    @Bean
    public Queue notificationSubscriptionQueue() {
        return QueueBuilder.durable(NOTIFICATION_SUBSCRIPTION_QUEUE).build();
    }

    @Bean
    public Queue notificationPaymentQueue() {
        return QueueBuilder.durable(NOTIFICATION_PAYMENT_QUEUE).build();
    }

    // ---- Bindings ----

    @Bean
    public Binding notificationSubscriptionBinding(Queue notificationSubscriptionQueue,
                                                    TopicExchange subscriptionExchange) {
        return BindingBuilder.bind(notificationSubscriptionQueue)
                .to(subscriptionExchange)
                .with(SUBSCRIPTION_CREATED_KEY);
    }

    @Bean
    public Binding notificationPaymentBinding(Queue notificationPaymentQueue,
                                               TopicExchange paymentExchange) {
        return BindingBuilder.bind(notificationPaymentQueue)
                .to(paymentExchange)
                .with(PAYMENT_COMPLETED_KEY);
    }

    // ---- Message Converter (JSON) ----

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
