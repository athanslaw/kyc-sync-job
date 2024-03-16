/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seamfix.bioweb.microservices.syncjob.pojos;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 *
 */
@Setter
@Getter
@ToString
public class FileProcessorResponse implements Serializable {


	/**
	 *
	 */
	private static final long serialVersionUID = 891086436480049203L;
	private Integer status;
    private String message;
    private Timestamp timeStamp;
    private boolean success;


    public FileProcessorResponse() {
    	this.timeStamp = new Timestamp(new java.util.Date().getTime());
    }

    public FileProcessorResponse(ResponseCodeEnum responseCodeEnum) {
        this.status = responseCodeEnum.getCode();
        this.message = responseCodeEnum.getDescription();
    }

	public void setResponseCodeEnum(ResponseCodeEnum responseCodeEnum) {
		this.status = responseCodeEnum.getCode();
        this.message = responseCodeEnum.getDescription();
	}
    
}
