package com.pxene.odin.cloud.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO
{
    private String id;

    private String username;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private Boolean status;

    @JsonIgnore
    private Date passwordLastUpdatetime;
}
