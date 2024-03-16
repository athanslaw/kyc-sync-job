package com.seamfix.bioweb.microservices.syncjob.services;


import com.seamfix.bioweb.microservices.data.processor.dto.SyncContent;
import com.seamfix.bioweb.microservices.syncjob.config.AppConfig;
import com.seamfix.bioweb.microservices.syncjob.enums.EncryptionType;
import com.seamfix.bioweb.microservices.syncjob.jms.SyncFileBroadcast;
import com.seamfix.bioweb.microservices.syncjob.pojos.FileProcessorResponse;
import com.seamfix.bioweb.microservices.syncjob.pojos.ResponseCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;

@Service
@Slf4j
public class FileProcessorService {

    @Autowired
    private AppConfig config;

    @Autowired
    private SyncFileBroadcast syncFileBroadcast;

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public FileProcessorResponse processRecord(SyncContent syncContent, String currentFileLocation, String fileName, EncryptionType encryptionType) {
        FileProcessorResponse fileProcessorResponse = new FileProcessorResponse(ResponseCodeEnum.ERROR);
        if (syncContent == null) {
            log.error("sync contents is null");
            return fileProcessorResponse;
        }
        File syncFile = new File(currentFileLocation + File.separator + fileName);

        Timestamp timeStamp = new Timestamp(syncFile.lastModified());

        fileProcessorResponse.setTimeStamp(timeStamp);

        syncContent.setPayloadTimeStamp(timeStamp);

        /*This was added to cater for legacy system where the
        client is sending filename without appending the tracking id
         */
        if(!fileName.equalsIgnoreCase(syncContent.getFileName())){
            log.debug("File name is different from sync content");
            syncContent.setFileName(fileName);
        }

        String syncLocation;
        if(EncryptionType.NIMC_ENCRYPTION.equals(encryptionType)){
           syncLocation = config.getSentEncryptedLocation();
        }else {
            syncLocation = config.getSentLocation();
        }
        syncContent.setSyncSourcePath(syncLocation);
        log.error("Sync file location {}", syncLocation);

        boolean isSent = false;
        try {
            syncFileBroadcast.sendTopic(syncContent, encryptionType);
            isSent = true;
        } catch (Exception ex) {
            log.error("Unable to send payload to artemis with file name {}", fileName, ex);
        }
        log.debug("successfully sent file to queue {}", isSent);
        if(!isSent){
            log.debug("Failed to send file to queue {}", isSent);
            fileProcessorResponse.setMessage("Failed to send message with file name");
            return fileProcessorResponse;
        }

        Path destinationPath = Paths.get(syncLocation, fileName);
        Path sourcePath = Paths.get(currentFileLocation, fileName);

        if(moveFile(sourcePath,destinationPath, fileName)){
            fileProcessorResponse.setSuccess(true);
            fileProcessorResponse.setResponseCodeEnum(ResponseCodeEnum.SUCCESS);
        }
        log.info("Response {}", fileProcessorResponse);
        return fileProcessorResponse;
    }

    private boolean moveFile(Path sourceLocation, Path destinationPath, String fileName) {
        try {
            Files.move(sourceLocation, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            log.debug("successfully moved file");
            return true;
        } catch (IOException e) {
            log.error("Failed to move file {} from {} to  {}", fileName, sourceLocation.toFile().getAbsolutePath(), destinationPath.toFile().getAbsolutePath(), e);
        }
        return false;
    }

    public void setSyncFileBroadcast(SyncFileBroadcast syncFileBroadcast) {
        this.syncFileBroadcast = syncFileBroadcast;
    }

    public void setConfig(AppConfig config) {
        this.config = config;
    }
}
