package sn.travel.search_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import java.net.Socket;
import java.security.cert.X509Certificate;

/**
 * Custom Elasticsearch client configuration.
 * Handles HTTPS connections with self-signed certificates (dev/K8s environment).
 */
@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUri;

    @Value("${spring.elasticsearch.username}")
    private String username;

    @Value("${spring.elasticsearch.password}")
    private String password;

    @Override
    public ClientConfiguration clientConfiguration() {
        try {
            // Trust all certificates + skip hostname verification (dev only)
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new TrustAllCertsManager()}, new java.security.SecureRandom());

            return ClientConfiguration.builder()
                    .connectedTo(extractHostAndPort(elasticsearchUri))
                    .usingSsl(sslContext)
                    .withBasicAuth(username, password)
                    .withSocketTimeout(10000)
                    .withConnectTimeout(5000)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to configure Elasticsearch SSL context", e);
        }
    }

    private String extractHostAndPort(String uri) {
        return uri.replaceFirst("https?://", "");
    }

    /**
     * Extended TrustManager that trusts all certificates AND skips hostname verification.
     * Using X509ExtendedTrustManager prevents the JDK from wrapping and adding hostname checks.
     * DEV ONLY — do NOT use in production.
     */
    private static class TrustAllCertsManager extends X509ExtendedTrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {}

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {}

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) {}

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) {}

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {}

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
