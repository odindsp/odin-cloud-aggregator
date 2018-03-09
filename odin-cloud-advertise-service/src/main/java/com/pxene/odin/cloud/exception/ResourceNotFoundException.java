package com.pxene.odin.cloud.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException{
	
	private static final long serialVersionUID = 6223257748356184009L;
    
    public static final int ERROR_CODE = 0x01;
    public static final String ERROR_MSG = "Can't find resource";
    
    public ResourceNotFoundException()
    {
        super(HttpStatus.NOT_FOUND, ERROR_CODE, ERROR_MSG);
    }
    public ResourceNotFoundException(String bizErrorMsg)
    {
        super(HttpStatus.NOT_FOUND, ERROR_CODE, bizErrorMsg);
    }
    public ResourceNotFoundException(int bizErrorCode, String bizErrorMsg)
    {
        super(HttpStatus.NOT_FOUND, bizErrorCode, bizErrorMsg);
    }
}
