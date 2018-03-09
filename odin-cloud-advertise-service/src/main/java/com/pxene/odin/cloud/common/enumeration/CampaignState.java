package com.pxene.odin.cloud.common.enumeration;

import static com.pxene.odin.cloud.common.constant.StatusConstant.*;

public enum CampaignState
{
    /**
     * 新建
     */
    NEW(CAMPAIGN_NEW, "新建"),

    /**
     * 待审核
     */
    WAIT_FOR_AUDIT(CAMPAIGN_WAIT_FOR_AUDIT, "待审核"),

    /**
     * 审核通过
     */
    AUDIT_APPROVED(CAMPAIGN_AUDIT_APPROVED, "审核通过"),

    /**
     * 未到投放周期
     */
    OUT_OF_CYCLE(CAMPAIGN_OUT_OF_CYCLE, "未到投放周期"),

    /**
     * 手动暂停
     */
    MANUAL_SUSPEND(CAMPAIGN_SUSPENDED, "已暂停"),

    /**
     * 不在投放时段
     */
    OUT_OF_PHASE(CAMPAIGN_OUT_OF_PHASE, "不在投放时段"),

    /**
     * 已到日KPI
     */
    OUT_OF_KPI(CAMPAIGN_OUT_OF_KPI, "已到日KPI"),

    /**
     * 已到日成本
     */
    OUT_OF_COST(CAMPAIGN_OUT_OF_COST, "已到日成本"),

    /**
     * 投放中
     */
    LAUNCHING(CAMPAIGN_LAUNCHING, "投放中"),

    /**
     * 投放结束
     */
    FINISH(CAMPAIGN_FINISHED, "已结束");


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


    private CampaignState(String code, String message)
    {
        this.code = code;
        this.message = message;
    }
}
