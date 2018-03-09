package com.pxene.odin.cloud.common.constant;

public class PhrasesConstant {

    // 通用
    public static final String NAME_NOT_NULL = "名称不能为空";
    public static final String LENGTH_ERROR_NAME = "名称长度超过限定";
    public static final String NAME_IS_EXIST = "名称已存在";
    public static final String CODE_IS_EXIST = "编号已存在";
    public static final String LACK_NECESSARY_PARAM = "缺少必要参数";
    public static final String PARAM_RULE_ERROR = "字段填写错误";
    public static final String START_DATE_ERROR_CODE = "开始时间不能为空";
    public static final String END_DATE_ERROR_CODE = "结束时间时间不能为空";
    public static final String BUDGEGET_NOT_MINUS = "成本不能为负";
    public static final String IMPRESSION_NOT_MINUS = "展现量不能为负";
    public static final String CLICK_NOT_MINUS = "点击量不能为负";
    public static final String TARGET_NOT_NULL = "定向信息不能为空";
    public static final String ADX_NOT_NULL = "渠道定向不能为空";
    public static final String PARAM_ERROR = "参数错误";


    // 广告主    
    public static final String ADVERTISER_NAME_IS_EXIST = "客户名称已存在";
    public static final String ADVERTISER_NOT_FOUND = "该客户不存在";
    public static final String COMPANY_NAME_IS_EXIST = "公司名称已存在";
    public static final String COMPANY_NAME_NOT_NULL = "公司名称不能为空";
    public static final String LENGTH_ERROR_COMPANY_NAME = "公司名称长度超过限定";
    public static final String LENGTH_ERROR_ADDRESS = "公司地址长度超过限定";
    public static final String CONTACT_NUM_NOT_NULL = "联系电话不能为空";
    public static final String LENGTH_ERROR_CONTACTNUM = "联系电话长度超过限定";
    public static final String CONTACTS_NOT_NULL = "联系人不能为空";
    public static final String LENGTH_ERROR_CONTACTS = "联系人长度超过限定";
    public static final String EMAIL_NOT_NULL = "常用邮箱不能为空";
    public static final String LENGTH_ERROR_EMAIL = "常用邮箱长度超过限定";
    public static final String FORMAT_ERROR_EMAIL = "邮箱格式不正确";
    public static final String INDUSTRY_ID_NOT_NULL = "行业不能为空";
    public static final String IS_PROTECTED_NOT_NULL = "是否保护客户不能为空";
    public static final String WEBSITE_NAME_NOT_NULL = "公司官网名称不能为空";
    public static final String LENGTH_ERROR_WEBSITE_NAME = "公司官网名称长度超过限定";
    public static final String WEBSITE_URL_NOT_NULL = "	公司官网地址不能为空";
    public static final String LENGTH_ERROR_WEBSITE_URL = "公司官网地址长度超过限定";
    public static final String LENGTH_ERROR_SALEMAN = "销售长度超过限定";
    public static final String ADVERTISER_ID_NOT_NULL = "广告主不能为空";

    // 项目
    public static final String PROJECT_NOT_FOUND = "该项目不存在";
    public static final String CAPITAL_NOT_FOUND = "总预算不能为空";
    public static final String PROJECT_ID_NOT_NULL = "广告项目不能为空";
    public static final String CODE_NOT_NULL = "编号不能为空";
    public static final String LENGTH_ERROR_CODE = "编号长度为14位";

    //活动
    public static final String CAMPAIGN_NOT_FOUND = "该活动不存在";
    public static final String CAMPAIGN_NAME_NOT_NULL = "活动名称不能为空";
    public static final String LENGTH_ERROR_CAMPAIGN_NAME = "活动名称长度超过限定";
    public static final String BID_NOT_MINUS = "活动单价不能为负";
    public static final String TOTAL_KPI_NOT_IDENTICAL = "日KPI之和与总KPI不相等";
    public static final String TOTAL_BUDGET_NOT_IDENTICAL = "日成本之和与总成本不相等";
    public static final String PERIOD_EMPTY_KPI_EXIST = "未设置投放时段，但填写了KPI";
    public static final String PERIOD_EXIST_KPI_EMPTY = "已设置投放时段，但未填写KPI";
    public static final String TOTAL_IMPRESSION_CLICK_NOT_NULL = "总展现量、总点击量必须填写一个";
    public static final String START_DATE_NOT_BEFORE = "投放开始日期不能早于今天";
    public static final String SCENE_NO_PATH = "请上传坐标";
    public static final String SCENE_NO_RADIUS = "请选择精度范围";
    

    // 物料包
    public static final String PACKAGE_IS_NOT_EXIST = "该物料包不存在";
    public static final String IMPRESSION_URL_NOT_NULL = "展现监测地址不能为空";
    public static final String LENGTH_ERROR_IMPRESSION_URL = "展现监测地址长度超过限定";
    public static final String CLICK_URL_NOT_NULL = "点击监测地址不能为空";
    public static final String LENGTH_ERROR_CLICK_URL = "点击监测地址长度超过限定";
    public static final String LANDPAGE_URL_NOT_NULL = "落地页地址不能为空";
    public static final String LENGTH_ERROR_LANDPAGE_URL = "落地页地址长度超过限定";
    public static final String NEED_MONITOR_CODE_NOT_NULL = "是否需要监控代码不能为空";
    public static final String CAMPAIGN_ID_NOT_NULL = "物料包在活动下，活动不能为空";
    public static final String LENGTH_ERROR_DEEPLINK_URL = "Deeplink地址长度超过限定";

    //文件上传
    public static final String FILE_IS_NOT_IMAGE = "仅支持文件类型为图片";
    public static final String FILE_READ_ERROR = "文件读取错误";
    public static final String FILE_UPLOAD_FAILED = "文件上传失败";
    public static final String GET_IMAGE_SIZE_FAILED = "获取图片尺寸失败";
    public static final String GET_IMAGE_SIZE_NULL = "获取图片尺寸为空";
    public static final String IMAGE_SIZE_NOT_STANDARD = "图片尺寸不符合标准";
    public static final String FILE_IS_NOT_VIDEO = "仅支持文件类型为视频";
    public static final String FILE_NOT_FOUND = "找不到文件";
    public static final String IMAGE_VOLUME_NOT_STANDARD = "图片大小不符合标准";
    public static final String IMAGE_TYPE_NOT_STANDARD = "图片类型不符合标准";

    //策略
    public static final String POLICY_NOT_FOUND = "该策略不存在";
    public static final String POLICY_TOTAL_KPI_MORE_THEN_CAMPAIGN = "总展现量或总点击量不能超过推广活动设置的值";
    public static final String POLICY_TOTAL_BUDGET_MORE_THEN_CAMPAIGN = "总成本不能超过推广活动设置的值";
    public static final String POLICY_NOT_ADVERTISE = "不能设置投放";
    public static final String POLICY_PERIOD_MORE_THEN_CAMPAIGN = "投放时段超过可设置范围";
    public static final String POLICY_KPI_MORE_THEN_CAMPAIGN = "KPI超过可设置范围";
    public static final String POLICY_BUDGET_MORE_THEN_CAMPAIGN = "成本超过可设置范围";
    public static final String POLICY_START_DATE_NOT_UPDATE = "保存失败，投放开始日期不能修改";
    public static final String POLICY_END_DATE_NOT_BEFORE_TODAY = "保存失败，投放结束日期不能早于今天";
    public static final String POLICY_DATE_MORE_THAN_CAMPAIGN = "保存失败，投放日期不能超过推广活动设置的范围";
    public static final String POLICY_BID_ERROR = "策略价格错误";
    
    //策略物料包关联
    public static final String POLICY_CREATIVE_NOT_FOUND = "该策略物料包关联不存在";

    // 创意
    public static final String CREATIVE_TYPE_NOT_NULL = "创意类型不能为空";
    public static final String CREATIVE_PACKAGE_ID_NOT_NULL = "创意的物料包不能为空";
    public static final String CREATIVE_MATERIAL_NOT_NULL = "创意素材不能为空";
    public static final String CREATIVE_INFOFLOW_POS_NOT_NULL = "原生模板不能为空";
    public static final String CREATIVE_INFOFLOW_IMAGE_NOT_NULL = "原生模板图片不能为空";
    public static final String CREATIVE_INFOFLOW_TITLE_NOT_NULL = "原生标题不能为空";
    public static final String CREATIVE_INFOFLOW_TITLE_TOO_LONG = "原生标题长度超过限定";
    public static final String CREATIVE_INFOFLOW_DESC_NOT_NULL = "原生描述不能为空";
    public static final String CREATIVE_INFOFLOW_DESC_TOO_LONG = "原生描述长度超过限定";
    public static final String CREATIVE_INFOFLOW_CTADESC_NOT_NULL = "原生行为按钮不能为空";
    public static final String CREATIVE_INFOFLOW_CTADESC_TOO_LONG = "原生行为按钮长度超过限定";
    public static final String CREATIVE_INFOFLOW_GOODSSTAR_NOT_NULL = "原生评分不能为空";
    public static final String CREATIVE_INFOFLOW_ORIGINALPRICE_NOT_NULL = "原生原价不能为空";
    public static final String CREATIVE_INFOFLOW_DISCOUNTPRICE_NOT_NULL = "原生折后价不能为空";
    public static final String CREATIVE_INFOFLOW_SSLESVOLUME_NOT_NULL = "原生销量不能为空";
    public static final String CREATIVE_INFOFLOW_SIZE_NOT_DIFF = "原生素材与模板尺寸不符";
    public static final String CREATIVE_INFOFLOW_VOLUME_TOO_BIG = "原生素材大小超出模板范围";
    public static final String CREATIVE_INFOFLOW_FORMAT_ERROR = "原生素材格式与模板不符";
    public static final String CREATIVE_VIDEO_POS_NOT_NULL = "视频模板不能为空";
    public static final String CREATIVE_VIDEO_NAME_NOT_NULL = "视频名称不能为空";
    public static final String CREATIVE_VIDEO_NAME_TO_LONG = "视频名称长度超过限定";
    public static final String CREATIVE_VIDEO_SIZE_NOT_DIFF = "视频素材与模板尺寸不符";
    public static final String CREATIVE_VIDEO_VOLUME_TOO_BIG = "视频素材大小超出模板范围";
    public static final String CREATIVE_VIDEO_TIMELENGTH_TOO_LONG = "视频素材时长超出模板范围";
    public static final String CREATIVE_VIDEO_FORMAT_ERROR = "视频素材格式与模板不符";
    public static final String CREATIVE_BID_ERROR = "创意价格错误";

    // 渠道信息
    public static final String GET_ADX_INFO_FAILED = "获取渠道信息失败";
    public static final String GET_ADX_INFO_NULL= "获取渠道信息为空";

    public static final String AUDIT_STATUS_FAILED = "推广活动审核失败";
    
    // 地域
    public static final String GET_REGION_INFO_FAILED = "获取地域信息失败";
    public static final String GET_REGION_INFO_NULL= "获取地域信息为空";
}
