package com.pxene.odin.cloud.common.constant;

public class StatusConstant {

	/**
	 * 开关关闭状态
	 */
	public static final String OFF_STATUS="0";
	/**
	 * 开关开启状态
	 */
	public static final String ON_STATUS="1";

	/**
	 * 排序类型：正序
     */
	public static final String SORT_TYPE_ASC = "01";
	/**
	 * 排序类型：逆序
     */
	public static final String SORT_TYPE_DESC = "02";
	/**
	 * 选择类型
	 */
	public static final String INCLUDE_TYPE_VALUE = "1";
	/**
	 * 排除类型
	 */
	public static final String EXCLUDE_TYPE_VALUE = "0";

	// 创意奥丁审核状态
	public static final String CREATIVE_STATUS_AUDIT = "01"; // 审核中
	public static final String CREATIVE_STATUS_APPROVED = "02"; // 审核通过
	public static final String CREATIVE_STATUS_NO_PASS = "03"; // 审核不通过

	/**
	 * 开始时间在当前时间之前
	 */
	public static final String START_DATE_BEFORE = "01";
	/**
	 * 开始时间在当前时间之后
	 */
	public static final String START_DATE_AFTER = "02";

	// 信息流模板
	public static final String CREATIVE_INFOFLOW_EMPTY = "1"; // 不填
	public static final String CREATIVE_INFOFLOW_OPTIONAL = "2"; // 选填
	public static final String CREATIVE_INFOFLOW_REQUIRED = "3"; // 必填

	// 推广活动状态机状态
	public static final String CAMPAIGN_NEW = "00";
	public static final String CAMPAIGN_SUSPENDED = "01";
	public static final String CAMPAIGN_LAUNCHING = "02";
	public static final String CAMPAIGN_FINISHED = "03";
	public static final String CAMPAIGN_OUT_OF_CYCLE = "04";
	public static final String CAMPAIGN_OUT_OF_PHASE = "05";
	public static final String CAMPAIGN_OUT_OF_KPI = "06";
	public static final String CAMPAIGN_OUT_OF_COST = "07";
	public static final String CAMPAIGN_WAIT_FOR_AUDIT = "08";
	public static final String CAMPAIGN_AUDIT_APPROVED = "09";

	// 投放策略状态机状态
	public static final String POLICY_NEW = "00";
	public static final String POLICY_SUSPENDED = "01";
	public static final String POLICY_LAUNCHING = "02";
	public static final String POLICY_FINISHED = "03";
	public static final String POLICY_OUT_OF_CYCLE = "04";
	public static final String POLICY_OUT_OF_PHASE = "05";
	public static final String POLICY_OUT_OF_KPI = "06";
	public static final String POLICY_OUT_OF_COST = "07";
	public static final String POLICY_BELONGTO_CAMPAIGN_SUSPENDED = "08";

	// 策略下创意投放状态
	public static final String CREATIVE_SUSPENDED = "01";
	public static final String CREATIVE_LAUNCHING = "02";
	public static final String CREATIVE_BELONGTO_POLICY_FINISHED = "03";
	public static final String CREATIVE_NO_AVAIABLE_CHANNEL = "04";
	public static final String CREATIVE_BELONGTO_POLICY_SUSPENDED = "05";
	public static final String CREATIVE_SUSPEND_IN_MATERIAL = "06";

	//推广活动审核状态
	/**
	 * 未审核
	 */
	public static final String AUDIT_CAMPAIGN_WAIT_FOR_AUDIT = "01";
	/**
	 * 已审核
	 */
	public static final String AUDIT_CAMPAIGN_AUDIT_APPROVED = "02";
	
	//策略匀速投放状态
	public static final String POLICY_ISUNIFORM_TRUE = "1";//是
	public static final String POLICY_ISUNIFORM_FALSE = "0";//否

}
