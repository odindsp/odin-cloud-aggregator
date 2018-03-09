package com.pxene.odin.cloud.common.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * redis的key
 * @author lizhuoling
 *
 */
public class RedisKeyConstant {
	/**
	 * 正在投放的活动id
	 */
	public static final String CAMPAIGN_IDS = "dsp_campaignids";
	/**
	 * 活动的投放控制策略
	 */
	public static final	String CAMPAIGN_CONTROL = "dsp_campaign_control_";
	/**
	 * 正在投放的策略id
	 */
	public static final String POLICY_IDS = "dsp_policyids";
	/**
	 * 策略的基本信息
	 */
	public static final	String POLICY_INFO = "dsp_policy_info_";
	/**
	 * 策略的定向信息
	 */
	public static final	String POLICY_TARGET = "dsp_policyid_target_";
	/**
	 * 策略引用的人群包
	 */
	public static final	String POLICY_AUDIENCEID = "dsp_policy_audienceid_";
	/**
	 * 策略的投放控制策略
	 */
	public static final	String POLICY_CONTROL = "dsp_policy_control_";
	/**
	 * 策略下所有的创意创意id
	 */
	public static final	String POLICY_CREATIVEIDS = "dsp_policy_mapids_";
	/**
	 * 创意的详细信息
	 */
	public static final String CREATIVE_INFO = "dsp_mapid_"; 
	/**
	 * 创意的出价信息
	 */
	public static final String CREATIVE_BID = "dsp_mapid_bid_"; 
	/**
	 * ADX定价合同价格信息
	 */
	public static final String FIX_PRICE = "dsp_fixprice_";
	/**
	 * 广告信息设置更新时间标识
	 */
	public static final String ADSETTING_WORKING = "dsp_adsetting_working";
	
	/**
	 * 监测地址模版
	 */
	public static final String[] MONITOR_TEMPLATES = {"var imgdc{index} = new Image();imgdc{index}.src = '{imonitorurl}';"};
	
	/**
	 * 定向标志位
	 */
	public static final Map<String, Integer[]> TARGET_CODES = new HashMap<String, Integer[]>();
	static {
		TARGET_CODES.put("region", new Integer[]{0x0, 0x1, 0x2, 0x3});
		TARGET_CODES.put("network", new Integer[]{0x0, 0x4, 0x8, 0xc});
		TARGET_CODES.put("os", new Integer[]{0x0, 0x10, 0x20, 0x30});
		TARGET_CODES.put("operator", new Integer[]{0x0, 0x40, 0x80, 0xc0});
		TARGET_CODES.put("device", new Integer[]{0x0, 0x100, 0x200, 0x300});
		TARGET_CODES.put("brand", new Integer[]{0x0, 0x400, 0x800, 0xc00});
	}
	
}
