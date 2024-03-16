package com.seamfix.bioweb.microservices.syncjob.job.units;


import com.seamfix.bioweb.microservices.data.processor.dto.SyncContent;
import com.seamfix.bioweb.microservices.syncjob.enums.EncryptionType;
import com.seamfix.bioweb.microservices.syncjob.pojos.FileProcessorResponse;
import com.seamfix.bioweb.microservices.syncjob.services.FileProcessorService;
import com.seamfix.bioweb.microservices.syncjob.util.WorkerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
public class Worker implements Supplier<Boolean> {

    private final String folderLocation;

    private final FileProcessorService fileProcessorService;
    private final EncryptionType encryptionType;

    public Worker(String folderLocation, FileProcessorService service, EncryptionType type) {
        this.folderLocation = folderLocation;
        this.fileProcessorService = service;
        this.encryptionType = type;
    }

    public List<String> retrieveFileNames() {
        List<String> fileNames = new ArrayList<>();
        if (StringUtils.isBlank(folderLocation)) {
            return fileNames;
        }
        File folder = new File(folderLocation);
        if (!folder.isDirectory()) {
            return fileNames;
        }
        String[] list = folder.list();
        return list == null || list.length <= 0 ? fileNames : Arrays.asList(list);
    }

    public boolean process() {
        boolean processable = isProcessable();
        if(!processable){
            log.error("Sync folder Location is not processable: {}", folderLocation);
            return false;
        }
        List<String> fileNames = retrieveFileNames();
        if(fileNames == null || fileNames.isEmpty()){
            log.error("Sync file Directory is empty {}", folderLocation);
            return false;
        }
        for (String file : fileNames){
            processSyncFileUsingFileName(file);
        }

        return true;
    }
    
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public void processSyncFileUsingFileName(String syncFileName) {
        try {
            if (StringUtils.isBlank(syncFileName)) {
                log.error("Sync Filename is empty");
                return;
            }
            Path path = Paths.get(folderLocation, syncFileName);
            if (!path.toFile().exists()) {
                log.error("Sync file does not exist");
                return;
            }
            log.error("About to copy file");
            byte[] syncFileContents;
            try {
                syncFileContents = Files.readAllBytes(path);
            } catch (IOException e) {
                log.error("Error reading sync content with file name {}", syncFileName, e);
                return;
            }

            SyncContent syncContent = (SyncContent) WorkerUtil.convertFromBytes(syncFileContents);
            if (syncContent == null) {
                log.error("Could not convert sync byte contents with file name {}", syncFileName);
                return;
            }

            FileProcessorResponse response = fileProcessorService.processRecord(syncContent, folderLocation, syncFileName, encryptionType);

            log.debug("Successfully processed sync file {} with file name {}", response != null && response.isSuccess(), syncFileName);

        } catch (Exception ex) {
            log.error("Error processing sync file with file name {}", syncFileName);
        }
    }

    public boolean isProcessable() {
        if(StringUtils.isBlank(folderLocation)){
            return false;
        }
        File directory = new File(folderLocation);
        return directory.exists() && directory.canRead(); 
    }

    @Override
    public Boolean get() {
        return process();
    }
}
