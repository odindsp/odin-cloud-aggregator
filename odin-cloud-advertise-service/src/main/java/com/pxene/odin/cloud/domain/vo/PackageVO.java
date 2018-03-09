package com.pxene.odin.cloud.domain.vo;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import static com.pxene.odin.cloud.common.constant.PhrasesConstant.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageVO {	
	/**
	 * 物料包ID
	 */
	private Integer id;
	/**
	 * 物料包名称
	 */
	@NotNull(message = NAME_NOT_NULL)
	@Length(max = 20, message = LENGTH_ERROR_NAME)
	private String name;
	/**
	 * 推广活动id
	 */
	@NotNull(message = CAMPAIGN_ID_NOT_NULL)
	private Integer campaignId;
	/**
	 * 广告主名称
	 */
	private String advertiserName;
	/**
	 * 广告主id
	 */
	private Integer advertiserId;
	/**
	 * 推广活动名称
	 */
	private String campaignName;
	/**
	 * 	点击监测地址
	 */
	@Length(max = 2000, message = LENGTH_ERROR_CLICK_URL)
	private String clickUrl;
	/**
	 * 创意数量
	 */
//	private Integer creativeNum;
	/**
	 * deeplink地址
	 */
	@Length(max = 2000, message = LENGTH_ERROR_DEEPLINK_URL)
	private String deeplinkUrl;
	/**
	 * 	展现监测地址1
	 */
	@Length(max = 2000, message = LENGTH_ERROR_IMPRESSION_URL)
	private String  impressionUrl1;
	/**
	 * 	展现监测地址2
	 */
	@Length(max = 2000, message = LENGTH_ERROR_IMPRESSION_URL)
	private String  impressionUrl2;
	/**
	 * 落地页地址
	 */
	@NotNull(message = LANDPAGE_URL_NOT_NULL)
	@Length(max = 2000, message = LENGTH_ERROR_LANDPAGE_URL)
	private String  landpageUrl;
	/**
	 * 是否需要落地页数据监测
	 */
	@NotNull(message = NEED_MONITOR_CODE_NOT_NULL)
	private String needMonitorCode;
	/**
	 * 广告项目名称
	 */
	private String projectName;
	/**
	 * 图片创意
	 */
	private ImageCreatives[] imageCreatives;
	@Data
	public static class ImageCreatives {
		/**
		 * 创意的id
		 */
		private Integer id;
		/**
		 * 创意即图片的名称
		 */
		private String name;
		/**
		 * 图片的存放路径
		 */
		private String path;
	}
	/**
	 * 信息流创意
	 */
	private InfoflowCreatives[] infoflowCreatives;
	@Data
	public static class InfoflowCreatives {
		/**
		 * 创意id
		 */
		private Integer id;
		/**
		 * 信息流名称
		 */
		private String title;
		/**
		 * 描述
		 */
		private String description;
		/**
		 * 小图的路径
		 */
		private String iconPath;
		/**
		 * 图片的路径
		 */
		private String[] imagePaths;
	}
	/**
	 * 视频创意
	 */
	private VideoCreatives[] videoCreatives;
	@Data
	public static class VideoCreatives {
		/**
		 * 创意id
		 */
		private Integer id;
		/**
		 * 创意即视频的名称
		 */
		private String name;
		/**
		 * 视频的存放路径
		 */
		private String path;
	}
}
