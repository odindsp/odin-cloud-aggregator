package com.pxene.odin.cloud.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BaseException extends RuntimeException
{
    private static final long serialVersionUID = -3532021335843319321L;
    private HttpStatus httpStatus;
    private int code;
    private String message;
}
