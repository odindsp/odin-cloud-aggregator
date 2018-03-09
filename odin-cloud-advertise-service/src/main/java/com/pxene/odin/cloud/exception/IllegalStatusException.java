package com.pxene.odin.cloud.exception;

import org.springframework.http.HttpStatus;

public class IllegalStatusException extends BaseException{
	
	private static final long serialVersionUID = -4610285929051676052L;
    
    public static final int ERROR_CODE = 0x02;
    public static final String ERROR_MSG = "Illegal state";
    
    public IllegalStatusException()
    {
        super(HttpStatus.BAD_REQUEST, ERROR_CODE, ERROR_MSG);
    }
    public IllegalStatusException(String bizErrorMsg)
    {
        super(HttpStatus.BAD_REQUEST, ERROR_CODE, bizErrorMsg);
    }
    public IllegalStatusException(int bizErrorCode, String bizErrorMsg)
    {
        super(HttpStatus.BAD_REQUEST, bizErrorCode, bizErrorMsg);
    }
}
