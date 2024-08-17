package laz.dimboba.articles;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.auth")
public record ApplicationProperties (
    String secret
) {}
