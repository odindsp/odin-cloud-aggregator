package com.pxene.odin.cloud.service;

import com.pxene.odin.cloud.common.constant.CodeTableConstant;
import com.pxene.odin.cloud.common.constant.PhrasesConstant;
import com.pxene.odin.cloud.common.util.HttpRequest;
import com.pxene.odin.cloud.domain.model.AdvertiserModel;
import com.pxene.odin.cloud.domain.model.CampaignModel;
import com.pxene.odin.cloud.domain.model.CreativeMaterialModel;
import com.pxene.odin.cloud.domain.model.CreativeModel;
import com.pxene.odin.cloud.domain.model.ImageModel;
import com.pxene.odin.cloud.domain.model.PackageModel;
import com.pxene.odin.cloud.domain.model.ProjectModel;
import com.pxene.odin.cloud.domain.model.VideoModel;
import com.pxene.odin.cloud.domain.vo.PackageVO;
import com.pxene.odin.cloud.domain.vo.PackageVO.ImageCreatives;
import com.pxene.odin.cloud.domain.vo.PackageVO.InfoflowCreatives;
import com.pxene.odin.cloud.domain.vo.PackageVO.VideoCreatives;
import com.pxene.odin.cloud.exception.ResourceNotFoundException;
import com.pxene.odin.cloud.repository.mapper.basic.AdvertiserMapper;
import com.pxene.odin.cloud.repository.mapper.basic.CampaignMapper;
import com.pxene.odin.cloud.repository.mapper.basic.CreativeMapper;
import com.pxene.odin.cloud.repository.mapper.basic.CreativeMaterialMapper;
import com.pxene.odin.cloud.repository.mapper.basic.ImageMapper;
import com.pxene.odin.cloud.repository.mapper.basic.PackageMapper;
import com.pxene.odin.cloud.repository.mapper.basic.ProjectMapper;
import com.pxene.odin.cloud.repository.mapper.basic.VideoMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物料包的具体操作实现
 *
 * @author lizhuoling
 */
@Service
@Transactional
public class PackageService extends BaseService {

    @Autowired
    private PackageMapper packageMapper;

    @Autowired
    private CampaignMapper campaignMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private AdvertiserMapper advertiserMapper;

    @Autowired
    private CreativeMapper creativeMapper;

    @Autowired
    private CreativeMaterialMapper creativeMaterialMapper;

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private VideoMapper videoMapper;

    /**
     * 创建物料包
     *
     * @param packageVO 物料包信息
     */
    public void createPackage(PackageVO packageVO) throws Exception {
        // 将VO中的物料包信息复制到对应的model中
        PackageModel packageModel = modelMapper.map(packageVO, PackageModel.class);
        packageModel.setIsLandpageCode(packageVO.getNeedMonitorCode());
        // 创建物料包
        packageMapper.insert(packageModel);
        packageVO.setId(packageModel.getId());
    }

    /**
     * 编辑物料包
     *
     * @param id 物料包ID
     * @param packageVO 物料包信息
     */
    public void updatePackage(Integer id, PackageVO packageVO) throws Exception {
        // 将VO中的物料包信息复制到对应的model中
        PackageModel packageModel = modelMapper.map(packageVO, PackageModel.class);
        packageModel.setIsLandpageCode(packageVO.getNeedMonitorCode());
        packageModel.setId(id);
        // 更新物料包
        packageMapper.updateByPrimaryKey(packageModel);
    }

    /**
     * 根据id查询物料包
     *
     * @param id 物料包id
     */
    public PackageVO getPackage(Integer id) throws Exception {
        // 查询物料包
        PackageModel packageModel = packageMapper.selectByPrimaryKey(id);
        // 获取物料包详情基本信息
        PackageVO packageVO = getPackageInfo(packageModel);
        // 返回物料包信息
        return packageVO;
    }

    /**
     * 批量查询物料包
     */
    public List<PackageVO> listPackages(Integer campaignId) throws Exception {
        // 批量查询物料包
        Map<String, Object> params = new HashMap<>();
        params.put("campaginId", campaignId);
        List<PackageModel> packageModels = packageMapper.selectAllPackage(params);
        // 批量物料包信息
        List<PackageVO> packages = new ArrayList<PackageVO>();
        if (packageModels != null && !packageModels.isEmpty()) {
            for (PackageModel packageModel : packageModels) {
                // 获取物料包详情基本信息
                PackageVO packageVO = getPackageInfo(packageModel);
                packages.add(packageVO);
                // 查询创意信息，一个物料包可以有多个创意，一个创意只能在一个物料包下
                List<CreativeModel> creatives = creativeMapper.selectByPackageId(packageModel.getId());
                if (creatives != null && !creatives.isEmpty()) {
                    // 图片创意
                    ImageCreatives imageCreative = null;
                    List<ImageCreatives> imageCreatives = new ArrayList<ImageCreatives>();
                    // 信息流创意
                    InfoflowCreatives infoflowCreative = null;
                    List<InfoflowCreatives> infoflowCreatives = new ArrayList<InfoflowCreatives>();
                    // 视频创意
                    VideoCreatives videoCreative = null;
                    List<VideoCreatives> videoCreatives = new ArrayList<VideoCreatives>();
                    for (CreativeModel creative : creatives) {
                        Integer creativeId = creative.getId();
                        String name = creative.getName();
                        String type = creative.getType();
                        if (type != null && CodeTableConstant.CREATIVE_TYPE_IMG.equals(type)) {
                            // 图片创意
                            imageCreative = new ImageCreatives();
                            imageCreative.setId(creativeId);
                            imageCreative.setName(name);
                            // 根据创意id查询创意素材信息
                            List<CreativeMaterialModel> creativeMaterials = creativeMaterialMapper.selectByCreativeId(creativeId);
                            if (creativeMaterials != null && !creativeMaterials.isEmpty()) {
                                CreativeMaterialModel creativeMaterial = creativeMaterials.get(0);
                                String materialType = creativeMaterial.getMaterialType();
                                if (materialType != null && materialType.equals(CodeTableConstant.CREATIVE_MATERIAL_IMAGE)) {
                                    ImageModel image = imageMapper.selectByPrimaryKey(creativeMaterial.getMaterialId());
                                    if (image != null) {
                                    	imageCreative.setPath(image.getPath());
                                    }                                    
                                }
                            }
                            imageCreatives.add(imageCreative);
                        } else if (type != null && CodeTableConstant.CREATIVE_TYPE_INFO.equals(type)) {
                            // 信息流创意
                            infoflowCreative = new InfoflowCreatives();
                            infoflowCreative.setId(creativeId);
                            infoflowCreative.setTitle(creative.getTitle());
                            infoflowCreative.setDescription(creative.getDescription());
                            // 根据创意id查询创意素材信息
                            List<CreativeMaterialModel> creativeMaterials = creativeMaterialMapper.selectByCreativeId(creativeId);
                            if (creativeMaterials != null && !creativeMaterials.isEmpty()) {
                                for (CreativeMaterialModel creativeMaterial : creativeMaterials) {
                                    String materialType = creativeMaterial.getMaterialType();
                                    // 查询图片信息
                                    ImageModel image = imageMapper.selectByPrimaryKey(creativeMaterial.getMaterialId());
                                    // 图片路径
                                    String[] imagePaths = new String[0];
                                    if (materialType != null && materialType.equals(CodeTableConstant.CREATIVE_MATERIAL_IMAGE)) {
                                    	if (image != null) {
                                    		imagePaths = image.getPath().split(",");
                                    	}                                        
                                        infoflowCreative.setImagePaths(imagePaths);
                                    }
                                    // 小图路径
                                    if (materialType != null && materialType.equals(CodeTableConstant.CREATIVE_MATERIAL_ICON)) {
                                    	if (image != null) {
                                    		infoflowCreative.setIconPath(image.getPath());
                                    	}                                       
                                    }
                                }
                            }
                            infoflowCreatives.add(infoflowCreative);
                        } else if (type != null && CodeTableConstant.CREATIVE_TYPE_VIDEO.equals(type)) {
                            // 视频创意
                            videoCreative = new VideoCreatives();
                            videoCreative.setId(creativeId);
                            videoCreative.setName(name);
                            // 根据创意id查询创意素材信息
                            List<CreativeMaterialModel> creativeMaterials = creativeMaterialMapper.selectByCreativeId(creativeId);
                            if (creativeMaterials != null && !creativeMaterials.isEmpty()) {
                                for (CreativeMaterialModel creativeMaterial : creativeMaterials) {
                                    String materialType = creativeMaterial.getMaterialType();
                                    if (materialType != null && materialType.equals(CodeTableConstant.CREATIVE_MATERIAL_VIDEO)) {
                                        // 查询视频信息
                                        VideoModel video = videoMapper.selectByPrimaryKey(creativeMaterial.getMaterialId());
                                        videoCreative.setPath(video.getPath());
                                    }
                                }
                            }
                            videoCreatives.add(videoCreative);
                        }
                    }
                    // 图片创意
                    ImageCreatives[] images = new ImageCreatives[imageCreatives.size()];
                    if (!imageCreatives.isEmpty()) {
                        for (int i = 0; i < imageCreatives.size(); i++) {
                            images[i] = imageCreatives.get(i);
                        }
                    }
                    packageVO.setImageCreatives(images);
                    // 视频创意
                    VideoCreatives[] videos = new VideoCreatives[videoCreatives.size()];
                    if (!videoCreatives.isEmpty()) {
                        for (int i = 0; i < videoCreatives.size(); i++) {
                            videos[i] = videoCreatives.get(i);
                        }
                    }
                    packageVO.setVideoCreatives(videos);
                    // 信息流创意
                    InfoflowCreatives[] infoflows = new InfoflowCreatives[infoflowCreatives.size()];
                    if (!infoflowCreatives.isEmpty()) {
                        for (int i = 0; i < infoflowCreatives.size(); i++) {
                            infoflows[i] = infoflowCreatives.get(i);
                        }
                    }
                    packageVO.setInfoflowCreatives(infoflows);
                }
            }
        }
        return packages;
    }

    /**
     * 单个物料包详情信息
     */
    private PackageVO getPackageInfo(PackageModel packageModel) {
        // 判断物料包是否存在
        if (packageModel == null) {
            throw new ResourceNotFoundException(PhrasesConstant.PACKAGE_IS_NOT_EXIST);
        }
        // 查询物料包所属的活动
        Integer campaignId = packageModel.getCampaignId();
        CampaignModel campaign = campaignMapper.selectByPrimaryKey(campaignId);
        // 查询物料所属的项目
        if (campaign == null) {
            throw new ResourceNotFoundException(PhrasesConstant.CAMPAIGN_NOT_FOUND);
        }
        Integer projectId = campaign.getProjectId();
        ProjectModel project = projectMapper.selectProjectById(projectId);
        // 查询物料包所属的广告主
        if (project == null) {
            throw new ResourceNotFoundException(PhrasesConstant.PROJECT_NOT_FOUND);
        }
        Integer advertiserId = project.getAdvertiserId();
        AdvertiserModel advertiser = advertiserMapper.selectByPrimaryKey(advertiserId);
        // 将model信息复制到vo中
        PackageVO packageVO = null;
        packageVO = modelMapper.map(packageModel, PackageVO.class);
        packageVO.setCampaignName(campaign.getName());      
        packageVO.setProjectName(project.getName());
        packageVO.setAdvertiserId(advertiserId);
        packageVO.setAdvertiserName(advertiser.getName());
        packageVO.setNeedMonitorCode(packageModel.getIsLandpageCode());
        return packageVO;
    }

    /**
     * 判断物料包名称是否重复，同一活动下物料包不能重复
     */
    public boolean isPackageNameExist(String name, Integer campaignId) {
    	List<PackageModel> packageModels = packageMapper.selectByNameAndCampaignId(name, campaignId);
        return  packageModels.size() > 0;
    }
    
    /**
     * 判断物料包名称是否重复（除了自己），同一活动下物料包不能重复
     * @param id 物料包id
     * @param name 物料包名称
     * @param campaignId 活动id
     * @return
     */
    public boolean isUpdateNameExist(Integer id, PackageVO packageVO) {
    	Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", packageVO.getName());
        map.put("campaignId", packageVO.getCampaignId());
    	List<PackageModel> packageModels = packageMapper.selectByNameAndCampaignIdAndNotId(map);
    	return  packageModels.size() > 0;
    }

    /**
     * 监测代码片段_开始
     */
    private static final String CODE_START = "<script>var_pxe=_pxe||[];var_pxe_id='";
    /**
     * 监测代码片段_开始
     */
    private static final String CODE_START_TEXT = "<scripttype='text/javascript'>var_pxe=_pxe||[];var_pxe_id='";
    /**
     * 监测代码片段_结束
     */
    private static final String CODE_END =
        "';(function(){varpxejs=document.createElement('script');var_pxejsProtocol=(('https:'==document.location.protocol)?'https://':'http://');pxejs.src=_pxejsProtocol+'"
//            + "//192.168.3.93/pap/pxene.js"
            + "//img.pxene.com/pxene.js"
            + "';varone=document.getElementsByTagName('script')[0];one.parentNode.insertBefore(pxejs,one);})();</script>";

    public String checkCode(Integer id) {
        PackageModel packageModel = packageMapper.selectByPrimaryKey(id);
        if(packageModel==null){
            throw new ResourceNotFoundException(PhrasesConstant.PACKAGE_IS_NOT_EXIST);
        }
        if (!packageModel.getIsLandpageCode().equals("1")) {//是1   否0
            return "1";
        }
        String reCode = HttpRequest.sendGet(packageModel.getLandpageUrl());
        String UpperReCode = reCode.toUpperCase();
        if (!StringUtils.isBlank(reCode)) {
            int headStart = UpperReCode.indexOf("<HEAD>");
            int headEnd = UpperReCode.indexOf("</HEAD>");
            if (headStart > 0 && headEnd > 0) {
                reCode = reCode.substring(headStart + 6, headEnd).replaceAll(" ", "").replaceAll("\"", "'").replaceAll("\n", "")
                    .replace("\t", "");
                if (reCode.indexOf(CODE_START + id + CODE_END) != -1 || reCode.indexOf(CODE_START_TEXT + id + CODE_END) != -1) {
                    return "0";
                }
            }
        }
        return "1";
    }
}
