package laz.dimboba.articles;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {


    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        var network = Network.newNetwork();
        return new PostgreSQLContainer<>("postgres:16.4-alpine")
            .withReuse(true)
            .withDatabaseName("test_db")
            .withNetwork(network)
            .withNetworkAliases("postgres");
    }

}
