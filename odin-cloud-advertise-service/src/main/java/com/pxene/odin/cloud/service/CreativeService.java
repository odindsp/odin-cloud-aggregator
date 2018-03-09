package com.pxene.odin.cloud.service;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pxene.odin.cloud.common.constant.CodeTableConstant;
import com.pxene.odin.cloud.common.constant.ConfKeyConstant;
import com.pxene.odin.cloud.common.constant.PhrasesConstant;
import com.pxene.odin.cloud.common.constant.StatusConstant;
import com.pxene.odin.cloud.common.enumeration.CreativeState;
import com.pxene.odin.cloud.common.enumeration.PolicyState;
import com.pxene.odin.cloud.domain.model.CreativeAuditModel;
import com.pxene.odin.cloud.domain.model.CreativeMaterialModel;
import com.pxene.odin.cloud.domain.model.CreativeModel;
import com.pxene.odin.cloud.domain.model.ImageModel;
import com.pxene.odin.cloud.domain.model.PackageModel;
import com.pxene.odin.cloud.domain.model.PolicyCreativeModel;
import com.pxene.odin.cloud.domain.model.VideoModel;
import com.pxene.odin.cloud.domain.vo.AdxVO;
import com.pxene.odin.cloud.domain.vo.CreativeVO;
import com.pxene.odin.cloud.domain.vo.ImageVo;
import com.pxene.odin.cloud.domain.vo.InfoflowVO;
import com.pxene.odin.cloud.domain.vo.SizeVO;
import com.pxene.odin.cloud.domain.vo.VideoPosVo;
import com.pxene.odin.cloud.domain.vo.VideoVO;
import com.pxene.odin.cloud.exception.DuplicateEntityException;
import com.pxene.odin.cloud.exception.IllegalStatusException;
import com.pxene.odin.cloud.repository.mapper.basic.CreativeAuditMapper;
import com.pxene.odin.cloud.repository.mapper.basic.CreativeMapper;
import com.pxene.odin.cloud.repository.mapper.basic.CreativeMaterialMapper;
import com.pxene.odin.cloud.repository.mapper.basic.ImageMapper;
import com.pxene.odin.cloud.repository.mapper.basic.PackageMapper;
import com.pxene.odin.cloud.repository.mapper.basic.PolicyCreativeMapper;
import com.pxene.odin.cloud.repository.mapper.basic.VideoMapper;
import com.pxene.odin.cloud.web.api.ChannelAdxClient;
import com.pxene.odin.cloud.web.api.ChannelPosClient;
import com.pxene.odin.cloud.web.api.ChannelSizeClient;
import com.pxene.odin.cloud.web.api.ImageSizeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class CreativeService extends BaseService {

    @Autowired
    private CreativeMapper creativeMapper;

    @Autowired
    private CreativeMaterialMapper creativeMaterialMapper;

    @Autowired
    private CreativeAuditMapper creativeAuditMapper;

    @Autowired
    private PackageMapper packageMapper;

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private ChannelAdxClient channelAdxClient;

    @Autowired
    private ChannelSizeClient channelSizeClient;

    @Autowired
    private ChannelPosClient channelPosClient;

    @Autowired
    private ImageSizeClient imageSizeClient;

    @Autowired
    private PolicyService policyService;

    @Autowired
    PolicyCreativeMapper policyCreativeMapper;

    @Autowired
    RedisService redisService;

    private String urlPrefix;

    @Autowired
    public CreativeService(Environment env) {
        urlPrefix = env.getProperty(ConfKeyConstant.FILESERVER_REMOTE_URL_PREFIX);
    }

    /**
     * 创建创意
     */
    public List<Integer> saveCreative(CreativeVO creative) {
        List<Integer> ids = new ArrayList<>(); // 返回创意id
        CreativeModel creativeModel = modelMapper.map(creative, CreativeModel.class);
        String type = creative.getType();
        if (CodeTableConstant.CREATIVE_TYPE_IMG.equals(type)) {
            // 图片创意
            Integer[] materialIds = creative.getMaterialIds();
            if (materialIds != null && materialIds.length > 0) {
                for (Integer materialId : materialIds) {
                    // 创建创意
                    creativeModel.setId(null);
                    creativeModel.setAuditStatus(StatusConstant.CREATIVE_STATUS_APPROVED);
                    creativeModel.setEnable(StatusConstant.ON_STATUS);
                    // 根据素材id查询尺寸
                    ImageModel imageModel = imageMapper.selectByPrimaryKey(materialId);
                    if (imageModel == null) {
                        throw new DuplicateEntityException(PhrasesConstant.GET_IMAGE_SIZE_NULL);
                    }
                    // 根据sizeid查询宽、高
                    String size = channelSizeClient.selectSizeById(imageModel.getSizeId());
                    if (size == null || "".equals(size)) {
                        throw new DuplicateEntityException(PhrasesConstant.GET_IMAGE_SIZE_FAILED);
                    }

                    JsonParser parser = new JsonParser();

                    JsonObject jsonObject = parser.parse(size).getAsJsonObject();
                    SizeVO sizeVO = new Gson().fromJson(jsonObject.toString(), SizeVO.class);

                    creativeModel.setName(sizeVO.getWidth() + "*" + sizeVO.getHeight());

                    creativeMapper.insert(creativeModel);
                    Integer id = creativeModel.getId();
                    ids.add(id);
                    // 添加创意素材关联
                    CreativeMaterialModel materialModel = new CreativeMaterialModel();
                    materialModel.setCreativeId(id);
                    materialModel.setMaterialId(materialId);
                    materialModel.setMaterialType(CodeTableConstant.CREATIVE_MATERIAL_IMAGE);
                    creativeMaterialMapper.insert(materialModel);
                }
            } else {
                throw new IllegalStatusException(PhrasesConstant.CREATIVE_MATERIAL_NOT_NULL);
            }
        } else { // 视频、信息流
            if (CodeTableConstant.CREATIVE_TYPE_VIDEO.equals(type)) {
                // 视频校验
                checkVideo(creative);
            } else if (CodeTableConstant.CREATIVE_TYPE_INFO.equals(type)) {
                // 信息流默认的名称与标题相同 --------（add by zhengyi, 20170926）
                creativeModel.setName(creative.getTitle());
                // 信息流校验
                checkInfoflow(creative);
            } else {
                throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
            }
            creativeModel.setAuditStatus(StatusConstant.CREATIVE_STATUS_APPROVED);
            creativeModel.setEnable(StatusConstant.ON_STATUS);
            // 创建创意
            creativeMapper.insert(creativeModel);
            Integer id = creativeModel.getId();
            ids.add(id);
            // 创意素材关联
            creative.setId(id);
            addMaterials(creative);
        }
        // 写入redis
        return ids;
    }

    /**
     * 检查视频创意信息
     */
    private void checkVideo(CreativeVO creative) {
        Integer posId = creative.getPosId();
        String video = channelPosClient.selectVideoById(posId);
        if (video == null || "".equals(video)) {
            throw new DuplicateEntityException(PhrasesConstant.GET_ADX_INFO_FAILED);
        }

        JsonParser parser = new JsonParser();

        JsonObject jsonObject = parser.parse(video).getAsJsonObject();
        VideoPosVo videoVo = new Gson().fromJson(jsonObject.toString(), VideoPosVo.class);
        if (videoVo != null) {
            if (jsonObject.has("videoFormats")) {
                JsonElement videoFormats = jsonObject.get("videoFormats");
                videoVo.setVideoPosFormat(new Gson().fromJson(videoFormats.toString(), new TypeToken<List<Integer>>() {
                }.getType()));
            }
            // 名称判断
            String name = creative.getName();
            if (name == null || name.length() <= 0) {
                throw new IllegalStatusException(PhrasesConstant.CREATIVE_VIDEO_NAME_NOT_NULL);
            } else if (name.length() > 20) {
                throw new IllegalStatusException(PhrasesConstant.CREATIVE_VIDEO_NAME_TO_LONG);
            }
            CreativeVO.Material[] materials = creative.getMaterials();
            if (materials != null && materials.length > 0) {
                VideoModel videoModel;
                for (CreativeVO.Material material : materials) {
                    if (CodeTableConstant.CREATIVE_MATERIAL_VIDEO.equals(material.getType())) {
                        // 根据素材id查询
                        videoModel = videoMapper.selectByPrimaryKey(material.getId());
                        if (videoModel != null) {

                            // 尺寸查询
                            String size = channelSizeClient.selectSizeById(videoModel.getSizeId());
                            if (size == null || "".equals(size)) {
                                throw new DuplicateEntityException(PhrasesConstant.GET_IMAGE_SIZE_FAILED);
                            }
                            parser = new JsonParser();
                            JsonObject sizeObject = parser.parse(size).getAsJsonObject();
                            SizeVO sizeVO = new Gson().fromJson(sizeObject.toString(), SizeVO.class);

                            // 尺寸校验
                            if (!videoVo.getFrameWidth().equals(sizeVO.getWidth()) || !videoVo.getFrameHeight()
                                .equals(sizeVO.getHeight())) {
                                throw new IllegalStatusException(PhrasesConstant.CREATIVE_VIDEO_SIZE_NOT_DIFF);
                            }

                            // 大小校验
                            if (videoVo.getMaxVolume() < videoModel.getVolume()) {
                                throw new IllegalStatusException(PhrasesConstant.CREATIVE_VIDEO_VOLUME_TOO_BIG);
                            }
                            // 时长
                            if (!videoVo.getDuration().equals(videoModel.getTimeLength())) {
                                throw new IllegalStatusException(PhrasesConstant.CREATIVE_VIDEO_TIMELENGTH_TOO_LONG);
                            }
                            // 格式校验
                            if (!videoVo.getVideoPosFormat().contains(videoModel.getFormatId())) {
                                throw new IllegalStatusException(PhrasesConstant.CREATIVE_VIDEO_FORMAT_ERROR);
                            }
                        } else {
                            throw new IllegalStatusException(PhrasesConstant.CREATIVE_MATERIAL_NOT_NULL);
                        }
                    } else if (CodeTableConstant.CREATIVE_MATERIAL_IMAGE.equals(material.getType())) {
                        ImageModel imageModel = imageMapper.selectByPrimaryKey(material.getId());
                        if (imageModel != null) {
                            // 尺寸查询
                            String size = channelSizeClient.selectSizeById(imageModel.getSizeId());
                            if (size == null || "".equals(size)) {
                                throw new DuplicateEntityException(PhrasesConstant.GET_IMAGE_SIZE_FAILED);
                            }
                            parser = new JsonParser();
                            JsonObject sizeObject = parser.parse(size).getAsJsonObject();
                            SizeVO sizeVO = new Gson().fromJson(sizeObject.toString(), SizeVO.class);

                            // 判断尺寸
                            if (!videoVo.getFrameWidth().equals(sizeVO.getWidth()) || !videoVo.getFrameHeight()
                                .equals(sizeVO.getHeight())) {
                                throw new IllegalStatusException(PhrasesConstant.CREATIVE_VIDEO_SIZE_NOT_DIFF);
                            }
                        }
                    }
                }
            }
        } else {
            throw new IllegalStatusException(PhrasesConstant.CREATIVE_VIDEO_POS_NOT_NULL);
        }
    }

    /**
     * 检查信息流创意信息
     */
    private void checkInfoflow(CreativeVO creative) {
        Integer posId = creative.getPosId();

        String infoflow = channelPosClient.selectInfoflowById(posId);
        if (infoflow == null || "".equals(infoflow)) {
            throw new DuplicateEntityException(PhrasesConstant.GET_ADX_INFO_FAILED);
        }

        JsonParser parser = new JsonParser();

        JsonObject jsonObject = parser.parse(infoflow).getAsJsonObject();
        InfoflowVO infoflowVO = new Gson().fromJson(jsonObject.toString(), InfoflowVO.class);

        InfoflowVO.InfoflowTmpl infoFlowTmpl = infoflowVO.getInfoflowTmpl();

        if (infoFlowTmpl != null) {
            if (jsonObject.getAsJsonObject("infoflowTmpl").has("imageTmpls")) {
                JsonArray jsonArray = jsonObject.getAsJsonObject("infoflowTmpl").getAsJsonArray("imageTmpls");
                infoFlowTmpl
                    .setImageTmpls(new Gson().fromJson(jsonArray.toString(), new TypeToken<List<InfoflowVO.InfoflowTmpl.ImageTmpl>>() {
                    }.getType()));
            } else {
                throw new IllegalStatusException(PhrasesConstant.CREATIVE_INFOFLOW_IMAGE_NOT_NULL);
            }

            if (jsonObject.getAsJsonObject("infoflowTmpl").has("imageFormats")) {
                JsonElement imageFormats = jsonObject.getAsJsonObject("infoflowTmpl").get("imageFormats");
                infoFlowTmpl.setImageFormats(new Gson().fromJson(imageFormats.toString(), new TypeToken<List<Integer>>() {
                }.getType()));
            }

            // 创意相关
            String title = creative.getTitle(); // 标题
            String description = creative.getDescription(); // 描述
            String ctaDesc = creative.getCtaDesc(); // 行为按钮
            // 模板相关
            Integer titleMaxLen = infoFlowTmpl.getTitleMaxLen();// 标题长度
            String descRequire = infoFlowTmpl.getDescriptionRequire(); // 描述填写要求
            Integer descMaxLen = infoFlowTmpl.getDescriptionMaxLen(); // 描述最大长度
            String ctaDescRequire = infoFlowTmpl.getCtaDescRequire(); // 行为按钮填写要求
            Integer ctaDescMaxLen = infoFlowTmpl.getCtaDescMaxLen(); // 行为按钮最大长度
            String needGoodsStar = infoFlowTmpl.getNeedGoodsStar(); // 是否需要评分
            String needOriginalPrice = infoFlowTmpl.getNeedOriginalPrice(); // 是否需要原价
            String needDiscountPrice = infoFlowTmpl.getNeedDiscountPrice(); // 是否需要折后价
            String needSalesVolume = infoFlowTmpl.getNeedSalesVolume(); // 是否需要销量
            // 标题判断
            if (title != null) {
                if (titleMaxLen < title.length()) {
                    throw new IllegalStatusException(PhrasesConstant.CREATIVE_INFOFLOW_TITLE_TOO_LONG);
                }
            } else {
                throw new IllegalStatusException(PhrasesConstant.CREATIVE_INFOFLOW_TITLE_NOT_NULL);
            }
            // 描述，选填、必填判断
            if (!StatusConstant.CREATIVE_INFOFLOW_EMPTY.equals(descRequire)) {
                if (description != null) {
                    if (descMaxLen < description.length()) {
                        throw new IllegalStatusException(PhrasesConstant.CREATIVE_INFOFLOW_DESC_TOO_LONG);
                    }
                } else {
                    if (StatusConstant.CREATIVE_INFOFLOW_REQUIRED.equals(descRequire)) {
                        // 必填
                        throw new IllegalStatusException(PhrasesConstant.CREATIVE_INFOFLOW_DESC_NOT_NULL);
                    }
                }
            }
            // 行为按钮，选填、必填判断
            if (!StatusConstant.CREATIVE_INFOFLOW_EMPTY.equals(ctaDescRequire)) {
                if (ctaDesc != null) {
                    if (ctaDescMaxLen < ctaDesc.length()) {
                        throw new IllegalStatusException(PhrasesConstant.CREATIVE_INFOFLOW_CTADESC_TOO_LONG);
                    }
                } else {
                    if (StatusConstant.CREATIVE_INFOFLOW_REQUIRED.equals(ctaDescRequire)) {
                        // 必填
                        throw new IllegalStatusException(PhrasesConstant.CREATIVE_INFOFLOW_CTADESC_NOT_NULL);
                    }
                }
            }
            // 是否需要评分
            if (StatusConstant.CREATIVE_INFOFLOW_REQUIRED.equals(needGoodsStar) && creative.getGoodsStar() == null) {
                // 必填
                throw new IllegalStatusException(PhrasesConstant.CREATIVE_INFOFLOW_GOODSSTAR_NOT_NULL);
            }
            // 是否需要原价
            if (StatusConstant.CREATIVE_INFOFLOW_REQUIRED.equals(needOriginalPrice) && creative.getOriginalPrice() == null) {
                // 必填
                throw new IllegalStatusException(PhrasesConstant.CREATIVE_INFOFLOW_ORIGINALPRICE_NOT_NULL);
            }
            // 是否需要折后价
            if (StatusConstant.CREATIVE_INFOFLOW_REQUIRED.equals(needDiscountPrice) && creative.getDiscountPrice() == null) {
                // 必填
                throw new IllegalStatusException(PhrasesConstant.CREATIVE_INFOFLOW_DISCOUNTPRICE_NOT_NULL);
            }
            // 是否需要销量
            if (StatusConstant.CREATIVE_INFOFLOW_REQUIRED.equals(needSalesVolume) && creative.getSalesVolume() == null) {
                // 必填
                throw new IllegalStatusException(PhrasesConstant.CREATIVE_INFOFLOW_SSLESVOLUME_NOT_NULL);
            }
            // 图片相关校验
            List<InfoflowVO.InfoflowTmpl.ImageTmpl> imageTmpls = infoFlowTmpl.getImageTmpls();
            if (imageTmpls.size() > 0) {
                CreativeVO.Material[] materials = creative.getMaterials();
                if (materials != null && materials.length > 0) {
                    for (InfoflowVO.InfoflowTmpl.ImageTmpl imageTmpl : imageTmpls) {
                        String type = imageTmpl.getType();
                        ImageModel imageModel;
                        for (CreativeVO.Material material : materials) {
                            // icon
                            if (CodeTableConstant.CREATIVE_MATERIAL_ICON.equals(type)) {
                                if (type.equals(material.getType())) {
                                    // 根据素材id查询尺寸
                                    imageModel = imageMapper.selectByPrimaryKey(material.getId());
                                    if (imageModel != null) {
                                        // 尺寸校验
                                        if (!imageTmpl.getSizeId().equals(imageModel.getSizeId())) {
                                            throw new IllegalStatusException(PhrasesConstant.CREATIVE_INFOFLOW_SIZE_NOT_DIFF);
                                        }
                                        // 判断大小
                                        if (imageTmpl.getMaxVolume() < imageModel.getVolume()) {
                                            throw new IllegalStatusException(PhrasesConstant.CREATIVE_INFOFLOW_VOLUME_TOO_BIG);
                                        }
                                        // 格式校验
                                        if (!infoFlowTmpl.getImageFormats().contains(imageModel.getFormatId())) {
                                            throw new IllegalStatusException(PhrasesConstant.CREATIVE_INFOFLOW_FORMAT_ERROR);
                                        }
                                    } else {
                                        throw new IllegalStatusException(PhrasesConstant.CREATIVE_MATERIAL_NOT_NULL);
                                    }
                                }
                            } else if (CodeTableConstant.CREATIVE_MATERIAL_IMAGE.equals(type)) {
                                // 大图
                                if (type.equals(material.getType()) && imageTmpl.getOrderNo().equals(material.getOrderNo())) {
                                    imageModel = imageMapper.selectByPrimaryKey(material.getId());
                                    if (imageModel != null) {
                                        // 判断尺寸
                                        if (!imageTmpl.getSizeId().equals(imageModel.getSizeId())) {
                                            throw new IllegalStatusException(PhrasesConstant.CREATIVE_INFOFLOW_SIZE_NOT_DIFF);
                                        }
                                        // 判断大小
                                        if (imageTmpl.getMaxVolume() < imageModel.getVolume()) {
                                            throw new IllegalStatusException(PhrasesConstant.CREATIVE_INFOFLOW_VOLUME_TOO_BIG);
                                        }
                                        // 格式校验
                                        if (!infoFlowTmpl.getImageFormats().contains(imageModel.getFormatId())) {
                                            throw new IllegalStatusException(PhrasesConstant.CREATIVE_INFOFLOW_FORMAT_ERROR);
                                        }
                                    } else {
                                        throw new IllegalStatusException(PhrasesConstant.CREATIVE_MATERIAL_NOT_NULL);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    throw new IllegalStatusException(PhrasesConstant.CREATIVE_MATERIAL_NOT_NULL);
                }
            } else {
                throw new IllegalStatusException(PhrasesConstant.CREATIVE_INFOFLOW_IMAGE_NOT_NULL);
            }
        } else {
            throw new IllegalStatusException(PhrasesConstant.CREATIVE_INFOFLOW_POS_NOT_NULL);
        }
    }


    /**
     * 添加创意素材关联（视频、信息流）
     */
    private void addMaterials(CreativeVO creative) {
        CreativeVO.Material[] materials = creative.getMaterials();
        if (materials != null && materials.length > 0) {
            for (CreativeVO.Material material : materials) {
                CreativeMaterialModel materialModel = new CreativeMaterialModel();
                materialModel.setCreativeId(creative.getId());
                materialModel.setMaterialId(material.getId());
                materialModel.setMaterialType(material.getType());
                materialModel.setOrderNo(material.getOrderNo());
                creativeMaterialMapper.insert(materialModel);
            }
        } else {
            throw new IllegalStatusException(PhrasesConstant.CREATIVE_MATERIAL_NOT_NULL);
        }
    }

    /***
     * 查询创意列表
     */
    public List<CreativeVO> listCreatives(Integer campaignId, Integer packageId, Integer policyId, String creativeType,
        String auditStatus) {
        if (policyId != null) {
            // 策略下的创意列表
            return listCreativePolicys(policyId, creativeType, auditStatus);
        } else if (packageId != null || campaignId != null) {
            // 物料包下的创意列表
            return listCreativePackages(campaignId, packageId, creativeType, auditStatus);
        } else {
            throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
        }
    }

    /**
     * 物料包下的创意列表
     *
     * @param campaignId 活动id
     * @param packageId 物料包id
     * @param creativeType 创意类型
     * @param auditStatus 奥丁审核状态
     */
    private List<CreativeVO> listCreativePackages(Integer campaignId, Integer packageId, String creativeType, String auditStatus) {
        List<CreativeVO> creativeVOs = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("campaignId", campaignId);
        params.put("id", packageId);
        params.put("type", creativeType);
        params.put("auditStatus", auditStatus);
        // 查询物料包名称
        List<CreativeModel> creativeModels = creativeMapper.selectCreatives(params);
        for (CreativeModel creativeModel : creativeModels) {
            Integer creativeId = creativeModel.getId();
            // 尺寸相关查询
            CreativeVO creativeVO = getSizeByCreative(creativeModel);
            creativeVO.setPackageEnable(creativeModel.getEnable());
            // 物料包名称
            PackageModel packageModel = packageMapper.selectByPrimaryKey(creativeModel.getPackageId());
            if (packageModel != null) {
                creativeVO.setPackageName(packageModel.getName());
            }
            // 渠道审核相关查询
            List<CreativeAuditModel> creativeAuditModels = creativeAuditMapper.selectCreativeAuditByCreativeId(creativeId);
            if (creativeAuditModels != null && creativeAuditModels.size() > 0) {
                List<CreativeVO.CreativeAudit> creativeAudits = new ArrayList<>();
                for (CreativeAuditModel creativeAuditModel : creativeAuditModels) {
                    CreativeVO.CreativeAudit creativeAudit = modelMapper.map(creativeAuditModel, CreativeVO.CreativeAudit.class);
                    // 查询渠道名称
                    String adx = channelAdxClient.selectAdxById(creativeAudit.getAdxId());
                    if (adx == null || "".equals(adx)) {
                        throw new DuplicateEntityException(PhrasesConstant.GET_ADX_INFO_FAILED);
                    }

                    JsonParser parser = new JsonParser();

                    JsonObject jsonObject = parser.parse(adx).getAsJsonObject();
                    AdxVO adxVO = new Gson().fromJson(jsonObject.toString(), AdxVO.class);

                    creativeAudit.setAdxName(adxVO.getName());
                    creativeAudits.add(creativeAudit);
                }
                creativeVO.setCreativeAudits(creativeAudits.toArray(new CreativeVO.CreativeAudit[creativeAudits.size()]));
            }
            creativeVOs.add(creativeVO);
        }
        return creativeVOs;
    }

    /**
     * 策略下的创意列表
     *
     * @param policyId 策略id
     * @param creativeType 创意类型
     * @param auditStatus 奥丁审核状态
     */
    private List<CreativeVO> listCreativePolicys(Integer policyId, String creativeType, String auditStatus) {
        List<CreativeVO> creativeVOs = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("id", policyId);
        params.put("type", creativeType);
        params.put("auditStatus", auditStatus);
        // 查询策略下的创意
        List<CreativeModel> creativeModels = creativeMapper.seleceByPolicyIdAndCreativeType(params);
        for (CreativeModel creativeModel : creativeModels) {
            Integer creativeId = creativeModel.getId();
            // 尺寸相关查询
            CreativeVO creativeVO = getSizeByCreative(creativeModel);

            // 数据相关查询
            // TODO: 2017/9/21

            creativeVOs.add(creativeVO);
        }
        return creativeVOs;
    }

    /**
     * 尺寸相关查询
     */
    private CreativeVO getSizeByCreative(CreativeModel creativeModel) {
        CreativeVO creativeVO = modelMapper.map(creativeModel, CreativeVO.class);
        Integer creativeId = creativeModel.getId();
        String type = creativeModel.getType(); // 查询出的创意类型
        if (CodeTableConstant.CREATIVE_TYPE_IMG.equals(type) || CodeTableConstant.CREATIVE_TYPE_INFO.equals(type)) {
            // 图片尺寸查询
            List<ImageModel> imageModels = creativeMaterialMapper.selectCreativeImgByCreativeId(creativeId);
            if (imageModels != null) {
                List<CreativeVO.Material> materials = new ArrayList<>();
                for (ImageModel imageModel : imageModels) {
                    // 根据尺寸id查询尺寸
                    String size = channelSizeClient.selectSizeById(imageModel.getSizeId());
                    if (size == null || "".equals(size)) {
                        throw new DuplicateEntityException(PhrasesConstant.GET_IMAGE_SIZE_FAILED);
                    }
                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(size).getAsJsonObject();
                    SizeVO sizeVO = new Gson().fromJson(jsonObject.toString(), SizeVO.class);
                    if (sizeVO != null) {
                        CreativeVO.Material material = new CreativeVO.Material();
                        material.setId(imageModel.getId());
                        material.setPath(urlPrefix + imageModel.getPath());
                        material.setFormat(imageModel.getFormatId());
                        material.setType(type);
                        material.setOrderNo(imageModel.getOrderNo());
                        material.setVolume(imageModel.getVolume());
                        material.setWidth(sizeVO.getWidth());
                        material.setHeight(sizeVO.getHeight());
                        materials.add(material);
                    }
                }
                creativeVO.setMaterials(materials.toArray(new CreativeVO.Material[materials.size()]));
            }
        } else if (CodeTableConstant.CREATIVE_TYPE_VIDEO.equals(type)) {
            // 视频尺寸相关查询
            VideoModel videoModel = creativeMaterialMapper
                .selectCreativeVideoByCreativeId(creativeId, CodeTableConstant.CREATIVE_MATERIAL_VIDEO);
            if (videoModel != null) {
                CreativeVO.Material material = new CreativeVO.Material();
                material.setId(videoModel.getId());
                material.setPath(urlPrefix + videoModel.getPath());
                material.setFormat(videoModel.getFormatId());
                material.setType(type);
                material.setTimeLength(videoModel.getTimeLength());
                material.setVolume(videoModel.getVolume());
                // 根据尺寸id查询尺寸
                String size = channelSizeClient.selectSizeById(videoModel.getSizeId());
                if (size == null || "".equals(size)) {
                    throw new DuplicateEntityException(PhrasesConstant.GET_IMAGE_SIZE_FAILED);
                }
                JsonParser parser = new JsonParser();
                JsonObject jsonObject = parser.parse(size).getAsJsonObject();
                SizeVO sizeVO = new Gson().fromJson(jsonObject.toString(), SizeVO.class);
                if (sizeVO != null) {
                    material.setWidth(sizeVO.getWidth());
                    material.setHeight(sizeVO.getHeight());
                }
                creativeVO.setMaterials(new CreativeVO.Material[]{material});
            }
        }
        return creativeVO;
    }

    /**
     * 判断物料包下的创意开关是否打开
     */
    public boolean isOpenPackageCreative(Integer creativeId) {
        // 根据创意id查询创意信息
        CreativeModel creative = creativeMapper.selectByPrimaryKey(creativeId);
        // 创意状态
        String enable = creative.getEnable();
        // 如果开关打开，返回true
        return policyService.isOpenSwitch(enable);
    }

    /**
     * 判断策略下的创意开关是否打开
     *
     * @param id 策略创意关联表的id
     */
    public boolean isOpenPolicyCreative(Integer id) {
        // 根据创意策略关联id查询关联信息
        PolicyCreativeModel policyCreative = policyCreativeMapper.selectByPrimaryKey(id);
        // 创意状态
        String enable = policyCreative.getEnable();
        // 如果开关打开，返回true
        return policyService.isOpenSwitch(enable);
    }

    /**
     * 判断创意是否通过审核，只要有一个ADX通过审核即可
     */
    public boolean isPassAudit(Integer creativeId) {
        // 根据创意id查询创意审核信息
        List<CreativeAuditModel> creativeAudits = creativeAuditMapper.selectCreativeAuditByCreativeId(creativeId);
        // 如果有渠道通过审核该创意，返回true
        return creativeAudits.size() > 0;
    }

    /**
     * 查询指定创意的投放状态。
     *
     * @return 状态机枚举
     */
    public CreativeState getState(Integer mapId) {
        // 根据创意-策略关联ID，获得创意ID、策略ID、创意所属策略的状态
        PolicyCreativeModel policyCreative = policyCreativeMapper.selectByPrimaryKey(mapId);
        Integer policyId = policyCreative.getPolicyId();
        Integer creativeId = policyCreative.getCreativeId();
        PolicyState policyState = policyService.getState(policyId);

        // 判断创意所属的策略是否已结束，如果已结束，返回true
        boolean finished = policyService.isFinished(policyState);
        if (finished) {
            return CreativeState.POLICY_HAS_FINISHED;
        }

        // 判断是否有可投放渠道，如果有至少一个投放渠道返回true
        boolean hasChannel = isPassAudit(creativeId);
        if (!hasChannel) {
            return CreativeState.NO_AVAILABLE_CHANNEL;
        }

        // 判断策略下的创意开关是否打开，如果投放开关打开，返回true
        boolean creativeOpened = isOpenPolicyCreative(mapId);
        if (!creativeOpened) {
            return CreativeState.MANUAL_SUSPEND;
        }

        // 判断创意所属的投放策略是否是暂停状态，如果是暂停状态，返回true
        boolean policySuspended = policyService.isSuspended(policyState);
        if (policySuspended) {
            return CreativeState.POLICY_HAS_SUSPEND;
        }

        // 判断创意在物料包下是否可用，如果创意在物料包下的开关打开，返回true
        boolean avaiableInMaterial = isOpenPackageCreative(creativeId);
        if (!avaiableInMaterial) {
            return CreativeState.SUSPEND_IN_MATERIAL;
        }

        return CreativeState.LAUNCHING;
    }

    /**
     * 判断策略下的创意是否是已暂停状态（无可投放渠道、手动暂停、所属投放策略已暂停、创意在物料包下已暂停）。
     *
     * @param state 创意的状态枚举
     * @return 如果状态概念上属于已暂停，即括中描述的几种状态，则返回true，否则返回false
     */
    public boolean isSuspended(CreativeState state) {
        boolean result = false;

        switch (state) {
            case NO_AVAILABLE_CHANNEL:
            case MANUAL_SUSPEND:
            case POLICY_HAS_SUSPEND:
            case SUSPEND_IN_MATERIAL:
                result = true;
                break;

            default:
                break;
        }

        return result;
    }

    /**
     * 判断策略下的创意是否是投放中状态。
     *
     * @param state 创意的状态枚举
     * @return 如果状态是LAUNCHING，则返回true，否则返回false
     */
    public boolean isLaunching(CreativeState state) {
        if (state == CreativeState.LAUNCHING) {
            return true;
        }
        return false;
    }

    /**
     * 判断策略下的创意是否是结束状态。
     *
     * @param state 创意的状态枚举
     * @return 如果状态是FINISH，则返回true，否则返回false
     */
    public boolean isFinished(CreativeState state) {
        if (state == CreativeState.POLICY_HAS_FINISHED) {
            return true;
        }
        return false;
    }

    /**
     * 物料包创意开关
     */
    public void enableCreative(String enable, Integer id) {
        List<Map<String, Integer>> mapIds = policyCreativeMapper.selectByCreativeId(id);

        switch (enable) {
            case StatusConstant.ON_STATUS:
                creativeMapper.updateEnableById(StatusConstant.ON_STATUS, id);
                if (mapIds.size() > 0) {
                    if (isPassAudit(id)) {
                        for (Map<String, Integer> m : mapIds) {
                            if (isOpenPolicyCreative(m.get("id"))) {
                                if (policyService.isPolicyPeriod(m.get("policy_id"))) {
                                    try {
                                        redisService.writeOneCreativeId(m.get("policy_id"), m.get("id"));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case StatusConstant.OFF_STATUS:
                creativeMapper.updateEnableById(StatusConstant.OFF_STATUS, id);
                if (mapIds.size() > 0) {
                    for (Map<String, Integer> m : mapIds) {
                        try {
                            redisService.removeOneCreativeId(m.get("policy_id"), m.get("id"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;

        }
    }

    public void auditCreative(String auditStatus, Integer[] ids) {
        creativeMapper.auditCreative(auditStatus, ids);
    }

    public CreativeModel getCreativeById(Integer id) {
        return creativeMapper.selectByPrimaryKey(id);
    }

    public CreativeModel updateCreativeNameById(Integer id, String name) {
        creativeMapper.updateCreativeName(id, name);
        return creativeMapper.selectByPrimaryKey(id);
    }


    public List<ImageVo> getImages(Integer advertiserId, Integer campaignId, Integer creativeId, Integer projectId, Integer sizeId) {
        List<ImageModel> imageModel = imageMapper.selectBySizeId(advertiserId, campaignId, creativeId, projectId, sizeId);
        if (imageModel != null) {
            List<ImageVo> imageVos = modelMapper.map(imageModel, new TypeToken<List<ImageVo>>() {
            }.getType());
            return setSize(imageVos);
        }
        return null;
    }

    private List<ImageVo> setSize(List<ImageVo> imageList) {
        List<ImageVo> imageVos = new ArrayList<>(imageList);
        if (imageVos != null) {
            String listSizes = imageSizeClient.listSizes();
            JsonParser parser = new JsonParser();
            if (listSizes == null) {
                throw new DuplicateEntityException(PhrasesConstant.GET_IMAGE_SIZE_FAILED);
            }
            if (!parser.parse(listSizes).getAsJsonObject().has("items")) {
                throw new DuplicateEntityException(PhrasesConstant.GET_IMAGE_SIZE_NULL);
            }

            JsonArray jsonArray = parser.parse(listSizes).getAsJsonObject().getAsJsonArray("items");
            List<SizeVO> items = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<SizeVO>>() {
            }.getType());
            Map<String, SizeVO> sizeMap = items.stream().collect(Collectors.toMap(SizeVO::getId, Function.identity()));
            for (ImageVo vo : imageVos) {
                vo.setPath(urlPrefix + vo.getPath());
                SizeVO sizeVO = sizeMap.get(vo.getSizeId().toString());
                if (sizeVO != null) {
                    vo.setWidth(sizeVO.getWidth());
                    vo.setHeight(sizeVO.getHeight());
                }
            }
        }
        return imageVos;
    }

    public List<VideoVO> getVideos(Integer advertiserId, Integer campaignId, Integer creativeId, Integer projectId, Integer sizeId) {
        List<VideoModel> videoModels = videoMapper.selectBySize(advertiserId, campaignId, creativeId, projectId, sizeId);
        if (videoModels != null) {
            List<VideoVO> videoVos = modelMapper.map(videoModels, new TypeToken<List<VideoVO>>() {
            }.getType());

            String listSizes = imageSizeClient.listSizes();
            JsonParser parser = new JsonParser();
            if (listSizes == null) {
                throw new DuplicateEntityException(PhrasesConstant.GET_IMAGE_SIZE_FAILED);
            }
            if (!parser.parse(listSizes).getAsJsonObject().has("items")) {
                throw new DuplicateEntityException(PhrasesConstant.GET_IMAGE_SIZE_NULL);
            }

            JsonArray jsonArray = parser.parse(listSizes).getAsJsonObject().getAsJsonArray("items");
            List<SizeVO> items = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<SizeVO>>() {
            }.getType());
            Map<String, SizeVO> sizeMap = items.stream().collect(Collectors.toMap(SizeVO::getId, Function.identity()));

            for (VideoVO vo : videoVos) {
                vo.setPath(urlPrefix + vo.getPath());
                SizeVO sizeVO = sizeMap.get(vo.getSizeId().toString());
                if (sizeVO != null) {
                    vo.setWidth(sizeVO.getWidth());
                    vo.setHeight(sizeVO.getHeight());
                }
            }
            return videoVos;
        }
        return null;
    }


    /**
     * 修改策略下创意价格
     */
    public void updatePolicyCreativeBid(Integer id, Integer bid) throws Exception {
        PolicyCreativeModel policyCreativeModel = policyCreativeMapper.selectByPrimaryKey(id);
        policyCreativeModel.setId(id);
        policyCreativeModel.setBid(bid);
        policyCreativeMapper.updateByIdSelective(policyCreativeModel);

        // 更新redis中的key（dsp_mapid_bid）
        if (policyService.isPolicyPeriod(policyCreativeModel.getPolicyId())) {
            redisService.writeCreateBid(id);
        }
    }
}