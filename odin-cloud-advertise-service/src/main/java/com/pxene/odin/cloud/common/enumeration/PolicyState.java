package com.pxene.odin.cloud.common.enumeration;

import static com.pxene.odin.cloud.common.constant.StatusConstant.*;


public enum PolicyState
{
    /**
     * 新建
     */
    NEW(POLICY_NEW, "新建"),

    /**
     * 未到投放周期
     */
    OUT_OF_CYCLE(POLICY_OUT_OF_CYCLE, "未到投放周期"),

    /**
     * 手动暂停
     */
    MANUAL_SUSPEND(POLICY_SUSPENDED, "已暂停"),

    /**
     * 不在投放时段
     */
    OUT_OF_PHASE(POLICY_OUT_OF_PHASE, "不在投放时段"),

    /**
     * 所属推广活动已暂停
     */
    CAMPAIGN_HAS_SUSPEND(POLICY_BELONGTO_CAMPAIGN_SUSPENDED, "推广活动已暂停"),

    /**
     * 已到日KPI
     */
    OUT_OF_KPI(POLICY_OUT_OF_KPI, "已到日KPI"),

    /**
     * 已到日成本
     */
    OUT_OF_COST(POLICY_OUT_OF_COST, "已到日成本"),

    /**
     * 投放中
     */
    LAUNCHING(POLICY_LAUNCHING, "投放中"),

    /**
     * 投放结束
     */
    FINISH(POLICY_FINISHED, "已结束");


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


    private PolicyState(String code, String message)
    {
        this.code = code;
        this.message = message;
    }
}
