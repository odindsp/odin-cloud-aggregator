package com.pxene.odin.cloud.exception;

import org.springframework.http.HttpStatus;

public class IllegalArgumentException extends BaseException
{
    private static final long serialVersionUID = -1389539463693690654L;

    public static final int ERROR_CODE = 0x01;
    public static final String ERROR_MSG = "Request parameter incorrect";


    public IllegalArgumentException()
    {
        super(HttpStatus.BAD_REQUEST, ERROR_CODE, ERROR_MSG);
    }
    public IllegalArgumentException(String bizErrorMsg)
    {
        super(HttpStatus.BAD_REQUEST, ERROR_CODE, bizErrorMsg);
    }
    public IllegalArgumentException(int bizErrorCode, String bizErrorMsg)
    {
        super(HttpStatus.BAD_REQUEST, bizErrorCode, bizErrorMsg);
    }
}
