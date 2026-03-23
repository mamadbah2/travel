package sn.travel.search_service.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest5_client.Rest5ClientTransport;
import co.elastic.clients.transport.rest5_client.low_level.Rest5Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.core5.http.HttpHost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * Custom Elasticsearch client configuration.
 * <p>
 * Creates the client manually with a request interceptor that forces
 * {@code compatible-with=8} headers, fixing the incompatibility between
 * elasticsearch-java 9.x client (from Spring Boot 4.0.2) and ES server 8.17.
 * Without this, the client sends compatible-with=9 which ES 8.x rejects with HTTP 400.
 * <p>
 * Also handles HTTPS connections with self-signed certificates (dev/K8s environment).
 */
@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris:https://localhost:9200}")
    private String elasticsearchUri;

    @Value("${spring.elasticsearch.username:elastic}")
    private String username;

    @Value("${spring.elasticsearch.password}")
    private String password;

    @Bean
    public ElasticsearchClient elasticsearchClient() throws Exception {
        // 1. Parse URI
        String hostAndPort = elasticsearchUri.replaceFirst("https?://", "");
        String[] parts = hostAndPort.split(":");
        String host = parts[0];
        int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 9200;
        String scheme = elasticsearchUri.startsWith("https") ? "https" : "http";
        HttpHost httpHost = new HttpHost(scheme, host, port);

        // 2. Trust-all SSL context (dev/K8s self-signed certs only)
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{new TrustAllCertsManager()}, new SecureRandom());

        // 3. Basic auth credentials
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(httpHost),
                new UsernamePasswordCredentials(username, password.toCharArray())
        );

        // 4. Build Rest5Client with SSL + auth + ES 8.x compatibility interceptor
        Rest5Client rest5Client = Rest5Client.builder(httpHost)
                .setSSLContext(sslContext)
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    // elasticsearch-java 9.x sends Accept: ...compatible-with=9 by default.
                    // ES 8.17 rejects this with HTTP 400.
                    // Force compatible-with=8 for all requests via request interceptor.
                    httpClientBuilder.addRequestInterceptorLast((request, entity, context) -> {
                        request.setHeader("Accept",
                                "application/vnd.elasticsearch+json;compatible-with=8");
                        if (entity != null) {
                            request.setHeader("Content-Type",
                                    "application/vnd.elasticsearch+json;compatible-with=8");
                        }
                    });
                })
                .build();

        // 5. Transport + Client (with Jackson mapper that discovers JSR310 module)
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        Rest5ClientTransport transport = new Rest5ClientTransport(rest5Client, new JacksonJsonpMapper(mapper));
        return new ElasticsearchClient(transport);
    }

    @Bean(name = {"elasticsearchOperations", "elasticsearchTemplate"})
    public ElasticsearchOperations elasticsearchOperations(ElasticsearchClient client) {
        return new ElasticsearchTemplate(client);
    }

    /**
     * Trust-all certificate manager. DEV ONLY — do NOT use in production.
     * Uses X509ExtendedTrustManager to prevent JDK from wrapping and adding hostname checks.
     */
    private static class TrustAllCertsManager extends X509ExtendedTrustManager {
        @Override public void checkClientTrusted(X509Certificate[] chain, String authType) {}
        @Override public void checkServerTrusted(X509Certificate[] chain, String authType) {}
        @Override public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) {}
        @Override public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) {}
        @Override public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {}
        @Override public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {}
        @Override public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
    }
}
