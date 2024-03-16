package com.seamfix.bioweb.microservices.syncjob.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@RefreshScope
@Configuration
public class AppConfig {
    @Value("${nimc.sent.sync.location}")
    private String sentLocation;

    @Value("${sftp.server.locations}")
    private String ftpSettings;

    @Value("${activemq.topic-name}")
    private String destinationTopic;

    @Value("${sftp.server.encrypted.locations}")
    private String ftpEncryptedSettings;

    @Value("${nimc.sent.sync.encrypted.location}")
    private String sentEncryptedLocation;

    @Value("${activemq.encrypted.topic-name}")
    private String encryptedDestinationTopic;
}
