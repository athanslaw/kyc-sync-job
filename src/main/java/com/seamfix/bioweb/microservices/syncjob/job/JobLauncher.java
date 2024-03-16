package com.seamfix.bioweb.microservices.syncjob.job;
import com.seamfix.bioweb.microservices.syncjob.config.AppConfig;
import com.seamfix.bioweb.microservices.syncjob.enums.EncryptionType;
import com.seamfix.bioweb.microservices.syncjob.job.units.Worker;
import lombok.extern.slf4j.Slf4j;
import com.seamfix.bioweb.microservices.syncjob.services.FileProcessorService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Application module Job launcher
 *
 */
@Slf4j
@Component
@Qualifier(value = "syncjob")
@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
public class JobLauncher {

    @Autowired
    private AppConfig config;

    @Autowired
    private FileProcessorService fileProcessorService;


	@Scheduled(fixedRateString = "${fixedRate.in.milliseconds}")
    protected void launch() {
        String ftpSettings = config.getFtpSettings();
        //	we now read the sftp server locations setting name from property file. The default being the previous name
        log.debug("sftpSettingName value : {}", ftpSettings);
        processFile(ftpSettings, EncryptionType.SFX_ENCRYPTION);
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
	@Scheduled(fixedRateString = "${sync.job.rate.in.milliseconds}")
    protected void launchForEncryptedFilesUsingNimcKeys() {
        String ftpSettings = config.getFtpEncryptedSettings();
        //	we now read the sftp server locations setting name from property file. The default being the previous name
        log.debug("sftpSettingName value : {}", ftpSettings);
        processFile(ftpSettings, EncryptionType.NIMC_ENCRYPTION);


    }

    protected void processFile(String ftpSettings, EncryptionType type){
        //CompletableFuture<List<Worker>> workerCompletableFuture = new CompletableFuture<>();
        List<CompletableFuture> workers = new ArrayList();
        if (StringUtils.isNotBlank(ftpSettings)) {
            String[] servers = ftpSettings.split(",");

            for (String server : servers) {
                log.info("server : {}", server);
                CompletableFuture<Boolean>  bundleRunner = CompletableFuture.supplyAsync(new Worker(server, fileProcessorService, type)).handle(
                        (result, exception) -> result);
                workers.add(bundleRunner);

            }
            CompletableFuture[] futures = workers.toArray(new CompletableFuture[0]);
            CompletableFuture.allOf(futures).join();


        }

    }

}
