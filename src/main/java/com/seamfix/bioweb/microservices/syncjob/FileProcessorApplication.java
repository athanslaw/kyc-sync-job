package com.seamfix.bioweb.microservices.syncjob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;


@EnableJms
@EnableAsync
@EnableAutoConfiguration
@SpringBootApplication
@EnableScheduling
public class FileProcessorApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileProcessorApplication.class, args);
    }
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }


}