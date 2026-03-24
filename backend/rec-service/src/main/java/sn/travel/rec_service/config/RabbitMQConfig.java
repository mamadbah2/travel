package sn.travel.rec_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration RabbitMQ pour la consommation des evenements voyage et souscription.
 * Le rec-service est un pur consommateur — il ecoute les evenements publies par
 * le travel-service pour alimenter le graphe Neo4j.
 */
@Configuration
public class RabbitMQConfig {

    // ---- Exchanges (declares par travel-service, re-declares ici par securite) ----
    public static final String TRAVEL_EXCHANGE = "travel.exchange";
    public static final String SUBSCRIPTION_EXCHANGE = "subscription.exchange";

    // ---- Queues (propres au rec-service) ----
    public static final String TRAVEL_CREATED_QUEUE = "rec.travel.created.queue";
    public static final String TRAVEL_UPDATED_QUEUE = "rec.travel.updated.queue";
    public static final String TRAVEL_DELETED_QUEUE = "rec.travel.deleted.queue";
    public static final String SUBSCRIPTION_CREATED_QUEUE = "rec.subscription.created.queue";

    // ---- Routing Keys ----
    public static final String TRAVEL_CREATED_KEY = "travel.created";
    public static final String TRAVEL_UPDATED_KEY = "travel.updated";
    public static final String TRAVEL_DELETED_KEY = "travel.deleted";
    public static final String SUBSCRIPTION_CREATED_KEY = "subscription.created";

    // ---- Exchanges ----

    @Bean
    public TopicExchange travelExchange() {
        return new TopicExchange(TRAVEL_EXCHANGE);
    }

    @Bean
    public TopicExchange subscriptionExchange() {
        return new TopicExchange(SUBSCRIPTION_EXCHANGE);
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

    @Bean
    public Queue subscriptionCreatedQueue() {
        return QueueBuilder.durable(SUBSCRIPTION_CREATED_QUEUE).build();
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

    @Bean
    public Binding subscriptionCreatedBinding(Queue subscriptionCreatedQueue, TopicExchange subscriptionExchange) {
        return BindingBuilder.bind(subscriptionCreatedQueue).to(subscriptionExchange).with(SUBSCRIPTION_CREATED_KEY);
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
