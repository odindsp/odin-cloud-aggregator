package com.pxene.odin.cloud.domain.vo;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import static com.pxene.odin.cloud.common.constant.PhrasesConstant.*;

/**
 * 广告主与页面交互的VO
 * @author lizhuoling
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvertiserVO {
	/**
	 * 广告主id
	 */
	private Integer id;
	/**
	 * 广告主名称
	 */
	@NotNull(message = NAME_NOT_NULL)
	@Length(max = 20, message = LENGTH_ERROR_NAME)
	private String name;
	/**
	 * 公司名称
	 */
	@NotNull(message = COMPANY_NAME_NOT_NULL)
	@Length(max = 50, message = LENGTH_ERROR_COMPANY_NAME)
	private String companyName;
	/**
	 * 是否保护客户
	 */
	@NotNull(message = IS_PROTECTED_NOT_NULL)
	private String isProtected;
	/**
	 * 联系人
	 */
	@NotNull(message = CONTACTS_NOT_NULL)
	@Length(max = 20, message = LENGTH_ERROR_CONTACTS)
	private String contacts;
	/**
	 * 联系电话
	 */
	@NotNull(message = CONTACT_NUM_NOT_NULL)
	@Length(max = 12, message = LENGTH_ERROR_CONTACTNUM)
	private String contactNum;
	/**
	 * 邮箱
	 */
	@NotNull(message = EMAIL_NOT_NULL)
	@Length(max = 100, message = LENGTH_ERROR_EMAIL)
	@Email(message = FORMAT_ERROR_EMAIL)
	private String email;
	/**
	 * qq
	 */
	private String qq;
	/**
	 * 行业id
	 */
	@NotNull(message = INDUSTRY_ID_NOT_NULL)
	private Integer industryId;
	/**
	 * 品牌名称
	 */
	private String brand;
	/**
	 * logo图片路径
	 */
	private String logoPath;
	/**
	 * 开户许可证图片路径
	 */
	private String accountLicencePath;
	/**
	 * 营业执照图片路径
	 */
	private String businessLicencePath;
	/**
	 * 组织机构代码证图片路径
	 */
	private String organizationCodePath;
	/**
	 * 网站ICP图片路径
	 */
	private String icpPath;
	/**
	 * 营业执照编号
	 */
	private String licenceNo;
	/**
	 * 营业执照截止日期
	 */
	private String licenceDeadline;
	/**
	 * 组织机构代码
	 */
	private String organizationCode;
	/**
	 * 固定电话
	 */
	private String telephone;
	/**
	 * 地址
	 */
	@Length(max = 100, message = LENGTH_ERROR_ADDRESS)
	private String address;
	/**
	 * 邮编
	 */
	private String zip;
	/**
	 * 网址
	 */
	@NotNull(message = WEBSITE_URL_NOT_NULL)
	@Length(max = 216, message = LENGTH_ERROR_WEBSITE_URL)
	private String websiteUrl;
	/**
	 * 网站名称
	 */
	@NotNull(message = WEBSITE_NAME_NOT_NULL)
	@Length(max = 100, message = LENGTH_ERROR_WEBSITE_NAME)
	private String websiteName;
	/**
	 * 用户数量
	 */
	private Integer userNum;
	/**
	 * 销售
	 */
	@Length(max = 20, message = LENGTH_ERROR_SALEMAN)
	private String saleman;
	/**
	 * 创建人
	 */
	private Integer createUser;
	/**
	 * 更新人
	 */
	private Integer updateUser;
	
	private Qualification[] qualifications;
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Qualification {
		/**
		 * 广告主id
		 */
		private Integer advertiserId;
		/**
		 * 类型
		 */
		private String type;
		/**
		 * 资质路径
		 */
		private String path;
		/**
		 * 创建人
		 */
		private Integer createUser;
		/**
		 * 更新人
		 */
		private Integer updateUser;
	}
}
