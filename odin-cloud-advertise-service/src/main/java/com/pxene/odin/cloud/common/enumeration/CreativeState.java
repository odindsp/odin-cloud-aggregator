package com.pxene.odin.cloud.common.enumeration;

import static com.pxene.odin.cloud.common.constant.StatusConstant.*;

public enum CreativeState
{
    /**
     * 所属策略已结束
     */
    POLICY_HAS_FINISHED(CREATIVE_BELONGTO_POLICY_FINISHED, "已结束"),

    /**
     * 无可投放渠道
     */
    NO_AVAILABLE_CHANNEL(CREATIVE_NO_AVAIABLE_CHANNEL, "无可投放渠道"),

    /**
     * 手动暂停
     */
    MANUAL_SUSPEND(CREATIVE_SUSPENDED, "已暂停"),

    /**
     * 所属投放策已暂停
     */
    POLICY_HAS_SUSPEND(CREATIVE_BELONGTO_POLICY_SUSPENDED, "投放策略已暂停"),

    /**
     * 创意在物料包下已暂停
     */
    SUSPEND_IN_MATERIAL(CREATIVE_SUSPEND_IN_MATERIAL, "创意在物料包下不可用"),

    /**
     * 投放中
     */
    LAUNCHING(CREATIVE_LAUNCHING, "投放中");


    private String code;
    private String message;


    public String getCode()
    {
        return code;
    }
    public void setCode(String code)
    {
        this.code = code;
    }
    public String getMessage()
    {
        return message;
    }
    public void setMessage(String message)
    {
        this.message = message;
    }


    private CreativeState(String code, String message)
    {
        this.code = code;
        this.message = message;
    }
}
