package com.seamfix.bioweb.microservices.data.processor.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyncContent implements Serializable {

	private static final long serialVersionUID = 6094229373125028864L;

	private String fileName;

	private String contents;

	private String registrationType;

	private Timestamp payloadTimeStamp;

	private String syncSourcePath;

	private String appId;

}
