package sn.travel.search_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Search Service — CQRS Read Side.
 * Provides Elasticsearch-powered search for the Travel platform.
 * Indexes travel data via RabbitMQ events, exposes fast search APIs.
 */
@SpringBootApplication
public class SearchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchServiceApplication.class, args);
    }
}
