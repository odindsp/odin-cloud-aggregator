package com.pxene.odin.cloud.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseArgumentValidation
{
    private String field;
    private Object rejectedValue;
    private String defaultMessage;
}
