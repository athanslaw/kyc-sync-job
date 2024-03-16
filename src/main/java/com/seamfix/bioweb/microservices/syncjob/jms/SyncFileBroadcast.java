package com.seamfix.bioweb.microservices.syncjob.jms;

import com.google.gson.Gson;
import com.seamfix.bioweb.microservices.data.processor.dto.SyncContent;
import com.seamfix.bioweb.microservices.syncjob.config.AppConfig;
import com.seamfix.bioweb.microservices.syncjob.enums.EncryptionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;

@Component
@Slf4j
public class SyncFileBroadcast {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private AppConfig config;

    private Gson gson;

    @PostConstruct
    public void init() {
        gson = new Gson();
    }

    public void sendTopic(SyncContent syncContent, EncryptionType encryptionType) throws JMSException {
        String nimcClientRequestEncryptString = gson.toJson(syncContent);
        log.info("About to send file");
        String destination;
        if(EncryptionType.NIMC_ENCRYPTION.equals(encryptionType)){
            destination = config.getEncryptedDestinationTopic();
        }else {
         destination = config.getDestinationTopic();
        }
        jmsTemplate.convertAndSend(destination, nimcClientRequestEncryptString);
    }

}
