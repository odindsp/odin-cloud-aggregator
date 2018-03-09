package com.pxene.odin.cloud.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(Include.NON_EMPTY)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse
{
    private Integer code;

    private String message;

    private Object data;


    public BaseResponse(Integer code, String message)
    {
        super();
        this.code = code;
        this.message = message;
    }
}
