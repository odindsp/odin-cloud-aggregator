package com.pxene.odin.cloud.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.pxene.odin.cloud.common.constant.ConfKeyConstant;
import com.pxene.odin.cloud.common.constant.PhrasesConstant;
import com.pxene.odin.cloud.common.util.FileUtil;
import com.pxene.odin.cloud.common.util.FtpUtil;
import com.pxene.odin.cloud.domain.model.AdvertiserModel;
import com.pxene.odin.cloud.domain.model.QualificationModel;
import com.pxene.odin.cloud.domain.vo.AdvertiserVO;
import com.pxene.odin.cloud.domain.vo.AdvertiserVO.Qualification;
import com.pxene.odin.cloud.exception.DuplicateEntityException;
import com.pxene.odin.cloud.exception.IllegalStatusException;
import com.pxene.odin.cloud.repository.mapper.basic.AdvertiserMapper;
import com.pxene.odin.cloud.repository.mapper.basic.QualificationMapper;
import com.pxene.odin.cloud.web.controller.AdvertiserController;

import lombok.extern.slf4j.Slf4j;

/**
 * 广告主操作的具体实现
 * @author lizhuoling
 *
 */
@Service
@Transactional
@Slf4j
public class AdvertiserService extends BaseService{
	@Autowired
	AdvertiserMapper advertiserMapper;
	
	@Autowired
	QualificationMapper qualificationMapper;		
	
	private static final String TEMP_DIR = "temp/";
	
	private static final String FORMAL_DIR = "qualification/";
	
	private String host;
	
	private int port;
	
	private String username;
	
	private String password;
			
	private String uploadDir;
	
	private String prefix;
	
	private String localPath;
	
	@Autowired
	public AdvertiserService(Environment env) {		
		host = env.getProperty(ConfKeyConstant.FILESERVER_REMOTE_HOST);
		port = Integer.parseInt(env.getProperty(ConfKeyConstant.FILESERVER_REMOTE_PORT, "21"));
		username = env.getProperty(ConfKeyConstant.FILESERVER_REMOTE_USERNAME);
		password = env.getProperty(ConfKeyConstant.FILESERVER_REMOTE_PASSWORD);
		uploadDir = env.getProperty(ConfKeyConstant.FILESERVER_REMOTE_UPLOAD_DIR);
		prefix = env.getProperty(ConfKeyConstant.FILESERVER_REMOTE_URL_PREFIX);
		localPath = env.getProperty(ConfKeyConstant.FILESERVER_LOCAL_IMAGE_PATH);
	}
	
	/**
	 * 创建广告主
	 * @param advertiser
	 * @throws Exception 
	 */
	public void createAdvertiser(AdvertiserVO advertiser) throws Exception {
		// 上传图片到ftp
		uploadToFtp(advertiser);
		// 将vo的数据复制到model中
		AdvertiserModel advertiserModel = modelMapper.map(advertiser, AdvertiserModel.class);		
		// 创建广告主
		advertiserMapper.insert(advertiserModel);		
		// 特殊资质
		advertiser.setId(advertiserModel.getId());
		addQualification(advertiser);
	}
	
	/**
	 * 编辑广告主
	 * @param advertiser
	 */
	public void updateAdvertiser(Integer id, AdvertiserVO advertiser) throws Exception {		
		advertiser.setId(id);
		// 处理图片路径
		String icpPath = advertiser.getIcpPath();
		if (icpPath != null && !icpPath.isEmpty() && icpPath.contains(prefix)) {
			advertiser.setIcpPath(icpPath.replace(prefix, ""));
		}	
		String logoPath = advertiser.getLogoPath();
		if (logoPath != null && !logoPath.isEmpty() && logoPath.contains(prefix)) {
			advertiser.setLogoPath(logoPath.replace(prefix, ""));
		}
		String accountLicencePath = advertiser.getAccountLicencePath();
		if (accountLicencePath != null && !accountLicencePath.isEmpty() && accountLicencePath.contains(prefix)) {
			advertiser.setAccountLicencePath(accountLicencePath.replace(prefix, ""));
		}
		String businessLicencePath = advertiser.getBusinessLicencePath();
		if (businessLicencePath != null && !businessLicencePath.isEmpty() && businessLicencePath.contains(prefix)) {
			advertiser.setBusinessLicencePath(businessLicencePath.replace(prefix, ""));
		}
		String organizationCodePath = advertiser.getOrganizationCodePath();
		if (organizationCodePath != null && !organizationCodePath.isEmpty() && organizationCodePath.contains(prefix)) {
			advertiser.setOrganizationCodePath(organizationCodePath.replace(prefix, ""));
		}			
		// 上传图片到ftp
		uploadToFtp(advertiser);
		// 将vo的数据复制到model中
		AdvertiserModel advertiserModel = modelMapper.map(advertiser, AdvertiserModel.class);				
		// 更新广告主基本信息
		advertiserMapper.updateByPrimaryKey(advertiserModel);
		// 删除特殊资质
		deleteQualification(id);
		// 添加特殊资质		
		addQualification(advertiser);
	}
	
	/**
	 * 根据广告主id查询广告主信息
	 * @param id
	 * @return
	 */
	public AdvertiserVO getAdvertiser(Integer id) {
		// 查询广告主信息
		AdvertiserModel advertiserModel = advertiserMapper.selectByPrimaryKey(id);
		// 查询特殊资质
		List<QualificationModel> qualificationModels = qualificationMapper.selectQualificationByAdvertiserId(id);
		// 将model信息复制到vo中
		AdvertiserVO advertiser = null;
		if (advertiserModel != null) {			
			advertiser = modelMapper.map(advertiserModel, AdvertiserVO.class);
			String icpPath = advertiserModel.getIcpPath();
			if (icpPath != null && !icpPath.isEmpty()) {
				advertiser.setIcpPath(prefix + icpPath);
			}	
			String logoPath = advertiserModel.getLogoPath();
			if (logoPath != null && !logoPath.isEmpty()) {
				advertiser.setLogoPath(prefix + logoPath);
			}
			String accountLicencePath = advertiserModel.getAccountLicencePath();
			if (accountLicencePath != null && !accountLicencePath.isEmpty()) {
				advertiser.setAccountLicencePath(prefix + accountLicencePath);
			}
			String businessLicencePath = advertiserModel.getBusinessLicencePath();
			if (businessLicencePath != null && !businessLicencePath.isEmpty()) {
				advertiser.setBusinessLicencePath(prefix + businessLicencePath);
			}
			String organizationCodePath = advertiserModel.getOrganizationCodePath();
			if (organizationCodePath != null && !organizationCodePath.isEmpty()) {
				advertiser.setOrganizationCodePath(prefix + organizationCodePath);
			}			
			advertiser.setUserNum(0);
		}		
		Qualification[] qualifications = new Qualification[qualificationModels.size()];
		if (qualificationModels != null && qualificationModels.size() > 0) {
			// 如果特殊资质信息不为空			
			for (int i = 0; i < qualifications.length; i++) {
				QualificationModel qualification = qualificationModels.get(i);
				qualifications[i] = modelMapper.map(qualification, Qualification.class);
				String path = qualification.getPath();
				if (path != null && !path.isEmpty()) {
					qualifications[i].setPath(prefix + path);
				}				
			}
			advertiser.setQualifications(qualifications);
		}				
		// 返回广告主信息
		return advertiser;
	}
	
	/**
	 * 批量查询广告主
	 * @param name 广告主名称
	 * @return 
	 */
	public List<AdvertiserVO> listAdvertisers(String name, String contacts, String companyName) {
		// 查询广告主
		Map<String,Object> map=new HashMap<>();
		map.put("name", name);
		map.put("contacts", contacts);
		map.put("companyName", companyName);
		List<AdvertiserModel> advertiserModels = advertiserMapper.selectAdvertiser(map);
		// 将model信息放到VO中
		List<AdvertiserVO> advertisers = new ArrayList<AdvertiserVO>();
		if (advertiserModels != null && !advertiserModels.isEmpty()) {
			for (AdvertiserModel advertiserModel : advertiserModels) {
				AdvertiserVO advertiser = modelMapper.map(advertiserModel, AdvertiserVO.class);
				String icpPath = advertiserModel.getIcpPath();
				if (icpPath != null && !icpPath.isEmpty()) {
					advertiser.setIcpPath(prefix + icpPath);
				}	
				String logoPath = advertiserModel.getLogoPath();
				if (logoPath != null && !logoPath.isEmpty()) {
					advertiser.setLogoPath(prefix + logoPath);
				}
				String accountLicencePath = advertiserModel.getAccountLicencePath();
				if (accountLicencePath != null && !accountLicencePath.isEmpty()) {
					advertiser.setAccountLicencePath(prefix + accountLicencePath);
				}
				String businessLicencePath = advertiserModel.getBusinessLicencePath();
				if (businessLicencePath != null && !businessLicencePath.isEmpty()) {
					advertiser.setBusinessLicencePath(prefix + businessLicencePath);
				}
				String organizationCodePath = advertiserModel.getOrganizationCodePath();
				if (organizationCodePath != null && !organizationCodePath.isEmpty()) {
					advertiser.setOrganizationCodePath(prefix + organizationCodePath);
				}			
				advertiser.setUserNum(0);
				advertisers.add(advertiser);
			}			
		}
		return advertisers;		
	}
	
	/**
	 * 上传资质到本地
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public String uploadQualification(MultipartFile file) throws Exception {
		// 判断图片的大小是否符合要求 
		BufferedImage logo = ImageIO.read(file.getInputStream());
		if (logo == null) {
            throw new DuplicateEntityException(PhrasesConstant.FILE_IS_NOT_IMAGE);
        }
		float volume = file.getSize();		
		float maxVolume = (float) 1024 * 1024;		
		if (maxVolume < volume) {
			throw new DuplicateEntityException(PhrasesConstant.IMAGE_VOLUME_NOT_STANDARD);			
		}
		// 判断图片的类型是否符合要求 
		String name = file.getOriginalFilename();
        String fileExtension = FileUtil.getFileExtensionByDot(name);
        log.debug("<=DSP-Advertiser=> fileExtension {后缀名}" + fileExtension);
        if (fileExtension != null && !fileExtension.isEmpty()) {
        	if (!fileExtension.equalsIgnoreCase("jpg") && !fileExtension.equalsIgnoreCase("png")) {
        		throw new DuplicateEntityException(PhrasesConstant.IMAGE_TYPE_NOT_STANDARD);
        	}
        }
        // 上传
		String path = upload(file);
        
    	return path;		
	}
	
	/**
	 * 上传Logo到本地
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public String uploadLogo(MultipartFile file) throws Exception {		
		// 判断图片的尺寸是否符合要求
		BufferedImage logo = ImageIO.read(file.getInputStream());
		if (logo == null) {
            throw new DuplicateEntityException(PhrasesConstant.FILE_IS_NOT_IMAGE);
        }
		Integer logoWidth = logo.getWidth();
		Integer logoHeight = logo.getHeight();
		float volume = file.getSize();
		if (logoWidth != 80 || logoHeight != 80) {
			throw new DuplicateEntityException(PhrasesConstant.IMAGE_SIZE_NOT_STANDARD);
		}
		// 判断图片的大小是否符合要求
		float maxVolume = (float)30 * 1024;
		if (maxVolume < volume) {
			throw new DuplicateEntityException(PhrasesConstant.IMAGE_VOLUME_NOT_STANDARD);
		}
		// 判断图片的类型是否符合要求
		String name = file.getOriginalFilename();
        String fileExtension = FileUtil.getFileExtensionByDot(name);
        if (fileExtension != null && !fileExtension.isEmpty()) {
        	if (!fileExtension.equalsIgnoreCase("jpg") && !fileExtension.equalsIgnoreCase("png")) {
        		throw new DuplicateEntityException(PhrasesConstant.IMAGE_TYPE_NOT_STANDARD);
        	}
        }
        // 上传
		String path = upload(file);
        
    	return path;		
	}
	
	/**
	 * 添加特殊资质
	 * @param advertiser
	 */
	private void addQualification(AdvertiserVO advertiser) {
		Qualification[] qualifications = advertiser.getQualifications();
		if (qualifications != null && qualifications.length > 0) {
			// 如果特殊资质信息不为空
			for (Qualification qualification : qualifications) {
				QualificationModel qualificationModel = new QualificationModel();
				// 特殊资质信息
				qualificationModel.setAdvertiserId(advertiser.getId());
				String path = qualification.getPath();
				String type = qualification.getType();
				// 判断特殊资质的基本信息是否为空，下面的参数在此结构中为必需
				if (path == null || type == null) {
					throw new IllegalStatusException(PhrasesConstant.LACK_NECESSARY_PARAM);
				}
				// 添加
				qualificationModel.setPath(path);
				qualificationModel.setType(type);
				qualificationMapper.insert(qualificationModel);
			}
		}
	}
	
	/**
	 * 删除广告主对应的特殊资质
	 * @param advertiserId 广告主id
	 */
	private void deleteQualification(Integer advertiserId) {
		qualificationMapper.deleteQualificationByAdvertiserId(advertiserId);
	}
	
	/**
	 * 判断广告主是否存在
	 * @param name
	 * @return
	 */
	public boolean isAdvertiserExist(String name) {	
		AdvertiserModel advertiser = advertiserMapper.selectAdvertiserByName(name);
		if (advertiser != null) {
			return true;
		} else {
			return false;
		}		
	}
	
	/**
	 * 判断公司名称是否存在
	 * @param companyName
	 * @return
	 */
	public boolean isCompanyNameExist(String companyName) {
		AdvertiserModel advertiser = advertiserMapper.selectByCompanyName(companyName);
		if (advertiser != null) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 判断除自己外公司名称是否存在
	 * @param companyName
	 * @param id
	 * @return
	 */
	public boolean isCompanyNameAndNotId(String companyName, Integer id) {
		AdvertiserModel advertiser = advertiserMapper.selectByCompanyNameAndNotId(companyName, id);
		if (advertiser != null) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 上传到本地
	 * @param file
	 * @return
	 */
	private String upload(MultipartFile file) {
		
		return FileUtil.uploadFileToLocal(localPath + TEMP_DIR, UUID.randomUUID().toString(), file);	
	}
	
	/**
	 * 复制到正式目录
	 * @param advertiser
	 * @throws Exception
	 */
	private void uploadToFtp(AdvertiserVO advertiser) throws Exception {
		
		// logo图片
		String logoPath = advertiser.getLogoPath();
		if (logoPath != null && logoPath.contains(TEMP_DIR)) {
			// 上传到正式目录
			String path = upload(logoPath);
			advertiser.setLogoPath(path.replace(uploadDir, ""));
		}
		
		// 开户许可证图片
		String accountLicencePath = advertiser.getAccountLicencePath();
		if (accountLicencePath != null && accountLicencePath.contains(TEMP_DIR)) {
			// 上传到正式目录
			String path = upload(accountLicencePath);
			advertiser.setAccountLicencePath(path.replace(uploadDir, ""));
		}
		
		// 营业执照图片
		String businessLicencePath = advertiser.getBusinessLicencePath();
		if (businessLicencePath != null && businessLicencePath.contains(TEMP_DIR)) {
			// 上传到正式目录
			String path = upload(businessLicencePath);
			advertiser.setBusinessLicencePath(path.replace(uploadDir, ""));
		}
		
		// 组织机构代码证图片
		String organizationCodePath = advertiser.getOrganizationCodePath();
		if (organizationCodePath != null && organizationCodePath.contains(TEMP_DIR)) {
			// 上传到正式目录
			String path = upload(organizationCodePath);
			advertiser.setOrganizationCodePath(path.replace(uploadDir, ""));
		}
		
		// 网站ICP图片
		String icpPath = advertiser.getIcpPath();
		if (icpPath != null && icpPath.contains(TEMP_DIR)) {
			// 上传到正式目录
			String path = upload(icpPath);
			advertiser.setIcpPath(path.replace(uploadDir, ""));
		}
		
		// 特殊资质
		Qualification[] qualifications = advertiser.getQualifications();
		if (qualifications != null && qualifications.length > 0) {
			for (Qualification qualification : qualifications) {
				String path = qualification.getPath();
				if (path != null && path.contains(TEMP_DIR)) {
					// 上传到正式目录
					String pathImg = upload(path);
					// 将临时目录替换成正式目录
					qualification.setPath(pathImg.replace(uploadDir, ""));
				}				
			}
		}
	}
	
	/**
	 * 图片复制到远程ftp上
	 * @param localPath
	 * @param advertiserId
	 * @return
	 * @throws Exception
	 */
	private String upload(String localPath) throws Exception {
		
		FileInputStream input = new FileInputStream(new File(localPath));
		// 上传路径
		String path = uploadDir + FORMAL_DIR;
		// 文件名后缀
		String fileExtension = FileUtil.getFileExtensionByDot(localPath);
		// 生成文件名
		String fileName = UUID.randomUUID().toString();
		// 全名
		String fullName = fileName + "." + fileExtension; 

		FtpUtil.uploadFile(host, port, username, password, path, fullName, input);
		
		String ftpPath = path + fullName;
		
		return ftpPath;
					
	}
}
