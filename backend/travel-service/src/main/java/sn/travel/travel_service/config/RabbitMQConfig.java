// package sn.travel.travel_service.config;
package sn.travel.travel_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for event-driven communication.
 * Handles subscription events, payment events, and travel lifecycle events.
 */
@Configuration
public class RabbitMQConfig {

    // Exchanges
    public static final String SUBSCRIPTION_EXCHANGE = "subscription.exchange";
    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    public static final String TRAVEL_EXCHANGE = "travel.exchange";

    // Queues
    public static final String SUBSCRIPTION_CREATED_QUEUE = "subscription.created.queue";
    public static final String PAYMENT_RESULT_QUEUE = "payment.result.queue";

    // Routing Keys
    public static final String SUBSCRIPTION_CREATED_KEY = "subscription.created";
    public static final String PAYMENT_SUCCESS_KEY = "payment.success";
    public static final String PAYMENT_FAILED_KEY = "payment.failed";

    // Travel Event Routing Keys (published for search-service indexing)
    public static final String TRAVEL_CREATED_KEY = "travel.created";
    public static final String TRAVEL_UPDATED_KEY = "travel.updated";
    public static final String TRAVEL_DELETED_KEY = "travel.deleted";

    // ---- Exchanges ----

    @Bean
    public TopicExchange subscriptionExchange() {
        return new TopicExchange(SUBSCRIPTION_EXCHANGE);
    }

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(PAYMENT_EXCHANGE);
    }

    @Bean
    public TopicExchange travelExchange() {
        return new TopicExchange(TRAVEL_EXCHANGE);
    }

    // ---- Queues ----

    @Bean
    public Queue subscriptionCreatedQueue() {
        return QueueBuilder.durable(SUBSCRIPTION_CREATED_QUEUE).build();
    }

    @Bean
    public Queue paymentResultQueue() {
        return QueueBuilder.durable(PAYMENT_RESULT_QUEUE).build();
    }

    // ---- Bindings ----

    @Bean
    public Binding subscriptionCreatedBinding(Queue subscriptionCreatedQueue, TopicExchange subscriptionExchange) {
        return BindingBuilder.bind(subscriptionCreatedQueue).to(subscriptionExchange).with(SUBSCRIPTION_CREATED_KEY);
    }

    @Bean
    public Binding paymentSuccessBinding(Queue paymentResultQueue, TopicExchange paymentExchange) {
        return BindingBuilder.bind(paymentResultQueue).to(paymentExchange).with(PAYMENT_SUCCESS_KEY);
    }

    @Bean
    public Binding paymentFailedBinding(Queue paymentResultQueue, TopicExchange paymentExchange) {
        return BindingBuilder.bind(paymentResultQueue).to(paymentExchange).with(PAYMENT_FAILED_KEY);
    }

    // ---- Message Converter ----

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
