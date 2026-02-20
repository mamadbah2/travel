package sn.travel.search_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for consuming travel events.
 * The search-service is a pure consumer — it listens for travel lifecycle events
 * published by the travel-service and indexes/removes documents in Elasticsearch.
 */
@Configuration
public class RabbitMQConfig {

    // ---- Exchange (declared by travel-service, re-declared here for safety) ----
    public static final String TRAVEL_EXCHANGE = "travel.exchange";

    // ---- Queues (owned by search-service) ----
    public static final String TRAVEL_CREATED_QUEUE = "search.travel.created.queue";
    public static final String TRAVEL_UPDATED_QUEUE = "search.travel.updated.queue";
    public static final String TRAVEL_DELETED_QUEUE = "search.travel.deleted.queue";

    // ---- Routing Keys (must match travel-service publisher) ----
    public static final String TRAVEL_CREATED_KEY = "travel.created";
    public static final String TRAVEL_UPDATED_KEY = "travel.updated";
    public static final String TRAVEL_DELETED_KEY = "travel.deleted";

    // ---- Exchange ----

    @Bean
    public TopicExchange travelExchange() {
        return new TopicExchange(TRAVEL_EXCHANGE);
    }

    // ---- Queues ----

    @Bean
    public Queue travelCreatedQueue() {
        return QueueBuilder.durable(TRAVEL_CREATED_QUEUE).build();
    }

    @Bean
    public Queue travelUpdatedQueue() {
        return QueueBuilder.durable(TRAVEL_UPDATED_QUEUE).build();
    }

    @Bean
    public Queue travelDeletedQueue() {
        return QueueBuilder.durable(TRAVEL_DELETED_QUEUE).build();
    }

    // ---- Bindings ----

    @Bean
    public Binding travelCreatedBinding(Queue travelCreatedQueue, TopicExchange travelExchange) {
        return BindingBuilder.bind(travelCreatedQueue).to(travelExchange).with(TRAVEL_CREATED_KEY);
    }

    @Bean
    public Binding travelUpdatedBinding(Queue travelUpdatedQueue, TopicExchange travelExchange) {
        return BindingBuilder.bind(travelUpdatedQueue).to(travelExchange).with(TRAVEL_UPDATED_KEY);
    }

    @Bean
    public Binding travelDeletedBinding(Queue travelDeletedQueue, TopicExchange travelExchange) {
        return BindingBuilder.bind(travelDeletedQueue).to(travelExchange).with(TRAVEL_DELETED_KEY);
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
