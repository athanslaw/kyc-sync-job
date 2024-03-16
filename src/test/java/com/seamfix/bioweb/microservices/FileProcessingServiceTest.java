package com.seamfix.bioweb.microservices;


import com.seamfix.bioweb.microservices.data.processor.dto.SyncContent;
import com.seamfix.bioweb.microservices.syncjob.config.AppConfig;
import com.seamfix.bioweb.microservices.syncjob.enums.EncryptionType;
import com.seamfix.bioweb.microservices.syncjob.jms.SyncFileBroadcast;
import com.seamfix.bioweb.microservices.syncjob.job.units.Worker;
import com.seamfix.bioweb.microservices.syncjob.services.FileProcessorService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.jms.JMSException;

import static org.mockito.ArgumentMatchers.isA;


@Slf4j
@SpringBootTest
public class FileProcessingServiceTest {

    @Autowired
    private FileProcessorService service;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private SyncFileBroadcast syncFileBroadcast;

    private Worker worker;


//    @Before
    public void setUp(){
        service.setSyncFileBroadcast(syncFileBroadcast);
        appConfig.setFtpSettings("src/test/resources/sent/");
        appConfig.setSentLocation("src/test/resources/sent/");
        service.setConfig(appConfig);
        worker = new Worker(appConfig.getFtpSettings(), service, EncryptionType.SFX_ENCRYPTION);
        try {
            Mockito.lenient().doNothing().when(syncFileBroadcast).sendTopic(isA(SyncContent.class), EncryptionType.SFX_ENCRYPTION);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testQueue(){
        try {
            syncFileBroadcast.sendTopic(new SyncContent(), EncryptionType.SFX_ENCRYPTION);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test_processRecord_in_a_folder(){
        Boolean processed = worker.get();
        Assertions.assertNotNull(processed);
        Assertions.assertTrue(processed);
        appConfig.setSentLocation("src/test/resources/sent/");
        appConfig.setFtpSettings("src/test/resources/sent/");

        processed = worker.get();
        Assertions.assertNotNull(processed);
    }



}
