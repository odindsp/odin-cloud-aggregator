package com.pxene.odin.cloud.exception;

import org.springframework.http.HttpStatus;


public class ServerFailureException extends BaseException
{
    private static final long serialVersionUID = -4406319417774075440L;

    public static final int ERROR_CODE = 0x01;
    public static final String ERROR_MSG = "server is failing";


    public ServerFailureException()
    {
        super(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_CODE, ERROR_MSG);
    }

    public ServerFailureException(String bizErrorMsg)
    {
        super(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_CODE, bizErrorMsg);
    }

    public ServerFailureException(int bizErrorCode, String bizErrorMsg)
    {
        super(HttpStatus.INTERNAL_SERVER_ERROR, bizErrorCode, bizErrorMsg);
    }
}
