package com.pxene.odin.cloud.exception;

import org.springframework.http.HttpStatus;

public class DuplicateEntityException extends BaseException {
	
	private static final long serialVersionUID = -1078308774980784735L;
    
    public static final int ERROR_CODE = 0x01;
    public static final String ERROR_MSG = "Entity already exists";
    
    public DuplicateEntityException()
    {
        super(HttpStatus.CONFLICT, ERROR_CODE, ERROR_MSG);
    }
    public DuplicateEntityException(String bizErrorMsg)
    {
        super(HttpStatus.CONFLICT, ERROR_CODE, bizErrorMsg);
    }
    public DuplicateEntityException(int bizErrorCode, String bizErrorMsg)
    {
        super(HttpStatus.CONFLICT, bizErrorCode, bizErrorMsg);
    }
}
