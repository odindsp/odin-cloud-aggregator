package com.pxene.odin.cloud.service;

import static com.pxene.odin.cloud.common.constant.PhrasesConstant.FILE_NOT_FOUND;
import static com.pxene.odin.cloud.common.constant.PhrasesConstant.FILE_READ_ERROR;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.pxene.odin.cloud.common.constant.ConfKeyConstant;
import com.pxene.odin.cloud.common.constant.PhrasesConstant;
import com.pxene.odin.cloud.common.util.ExcelUtil;
import com.pxene.odin.cloud.common.util.FileUtil;
import com.pxene.odin.cloud.common.util.FtpUtil;
import com.pxene.odin.cloud.domain.model.ImageModel;
import com.pxene.odin.cloud.domain.model.VideoModel;
import com.pxene.odin.cloud.domain.vo.SizeVO;
import com.pxene.odin.cloud.exception.DuplicateEntityException;
import com.pxene.odin.cloud.exception.ResourceNotFoundException;
import com.pxene.odin.cloud.exception.ServerFailureException;
import com.pxene.odin.cloud.repository.mapper.basic.ImageMapper;
import com.pxene.odin.cloud.repository.mapper.basic.VideoMapper;
import com.pxene.odin.cloud.web.api.ImageSizeClient;

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.MultimediaInfo;
import it.sauronsoftware.jave.VideoSize;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangshiyi
 */
@Service
@Transactional
@Slf4j
public class FileUploadService extends BaseService {

    private String host;

    private int port;

    private String username;

    private String password;

    private String uploadDir;

    private String localFilePath;

    private String urlPrefix;

    @Autowired
    private ImageSizeClient imageSizeClient;
    @Autowired
    private ImageMapper imageMapper;
    @Autowired
    private VideoMapper videoMapper;


    @Autowired
    public FileUploadService(Environment env) {
        host = env.getProperty(ConfKeyConstant.FILESERVER_REMOTE_HOST);
        port = Integer.parseInt(env.getProperty(ConfKeyConstant.FILESERVER_REMOTE_PORT));
        username = env.getProperty(ConfKeyConstant.FILESERVER_REMOTE_USERNAME);
        password = env.getProperty(ConfKeyConstant.FILESERVER_REMOTE_PASSWORD);
        uploadDir = env.getProperty(ConfKeyConstant.FILESERVER_REMOTE_UPLOAD_DIR);
        localFilePath = env.getProperty(ConfKeyConstant.FILESERVER_LOCAL_FILE_PATH);
        urlPrefix = env.getProperty(ConfKeyConstant.FILESERVER_REMOTE_URL_PREFIX);
    }

    public Map<String, Object> uploadImage(String checkSize, MultipartFile file) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String prefixPath = "creative" + File.separator + LocalDateTime.now().format(dateTimeFormatter) + File.separator;
        String fileName = UUID.randomUUID().toString() + "." + FileUtil.getFileExtensionByDot(file.getOriginalFilename());

        BufferedImage sourceImg = null;
        try {
            sourceImg = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            throw new DuplicateEntityException(PhrasesConstant.FILE_READ_ERROR);
        }
        if (sourceImg == null) {
            throw new DuplicateEntityException(PhrasesConstant.FILE_IS_NOT_IMAGE);
        }
        String imgwh = sourceImg.getWidth() + "*" + sourceImg.getHeight();
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
        Map<String, String> sizeMap = items.stream().collect(Collectors.toMap(e -> {
            return e.getWidth() + "*" + e.getHeight();
        }, SizeVO::getId));
        if (sizeMap.get(imgwh) == null) {
            throw new DuplicateEntityException(PhrasesConstant.IMAGE_SIZE_NOT_STANDARD);
        }
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new DuplicateEntityException(PhrasesConstant.FILE_READ_ERROR);
        }
        boolean flag = FtpUtil.uploadFile(host, port, username, password, uploadDir + prefixPath, fileName, inputStream);

        if (flag) {
            String fileExtensionByDot = FileUtil.getFileExtensionByDot(file.getOriginalFilename());

            ImageModel imageModel = new ImageModel();
            imageModel.setPath(prefixPath + fileName);
            switch (fileExtensionByDot) {
                case "png":
                    imageModel.setFormatId(17);
                    break;
                case "jpg":
                    imageModel.setFormatId(18);
                    break;
                case "jpeg":
                    imageModel.setFormatId(18);
                    break;
                case "gif":
                    imageModel.setFormatId(19);
                    break;
                default:
                    imageModel.setFormatId(0);
                    break;
            }
            imageModel.setVolume(Long.valueOf(file.getSize()).intValue() / 1024);
            imageModel.setSizeId(Integer.valueOf(sizeMap.get(imgwh)));

            if (imageMapper.insert(imageModel) > 0) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", imageModel.getId());
                map.put("path", urlPrefix + imageModel.getPath());
                return map;
            }
        }
        throw new DuplicateEntityException(PhrasesConstant.FILE_UPLOAD_FAILED);
    }

    public Map<String, Object> uploadVideo(MultipartFile file) {
        File videoFile = null;

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String prefixPath = "creative" + File.separator + LocalDateTime.now().format(dateTimeFormatter) + File.separator;
        String fileName = UUID.randomUUID().toString() + "." + FileUtil.getFileExtensionByDot(file.getOriginalFilename());

        Encoder encoder = new Encoder();
        try {
            videoFile = File.createTempFile("tmp", "." + FileUtil.getFileExtensionByDot(file.getOriginalFilename()));
            file.transferTo(videoFile);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DuplicateEntityException(PhrasesConstant.FILE_READ_ERROR);
        }

        MultimediaInfo mu = null;
        try {
            mu = encoder.getInfo(videoFile);
        } catch (EncoderException e) {
            e.printStackTrace();
            throw new DuplicateEntityException(PhrasesConstant.FILE_IS_NOT_VIDEO);
        }
        long ls = mu.getDuration();//时长-单位毫秒
        int timeLength = new BigDecimal((double) ls / 1000).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();//视频时长
        VideoSize videoSize = mu.getVideo().getSize();

        String videowh = videoSize.getWidth() + "*" + videoSize.getHeight();
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
        Map<String, String> sizeMap = items.stream().collect(Collectors.toMap(e -> {
            return e.getWidth() + "*" + e.getHeight();
        }, SizeVO::getId));
        if (sizeMap.get(videowh) == null) {
            throw new DuplicateEntityException(PhrasesConstant.IMAGE_SIZE_NOT_STANDARD);
        }

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = FileUtils.openInputStream(videoFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new DuplicateEntityException(PhrasesConstant.FILE_READ_ERROR);
        }
        boolean flag = FtpUtil.uploadFile(host, port, username, password, uploadDir + prefixPath, fileName, fileInputStream);

        if (flag) {
            String fileExtensionByDot = FileUtil.getFileExtensionByDot(file.getOriginalFilename());
            VideoModel videoModel = new VideoModel();
            videoModel.setPath(prefixPath + fileName);
            switch (fileExtensionByDot) {
                case "flv":
                    videoModel.setFormatId(33);
                    break;
                case "mp4":
                    videoModel.setFormatId(34);
                    break;
                default:
                    videoModel.setFormatId(0);
                    break;
            }
            videoModel.setSizeId(Integer.valueOf(sizeMap.get(videowh)));
            videoModel.setTimeLength(timeLength);
            videoModel.setVolume(Long.valueOf(file.getSize()).intValue() / 1024);

            if (videoMapper.insert(videoModel) > 0) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", videoModel.getId());
                map.put("path", urlPrefix + videoModel.getPath());
                return map;
            }
        }
        throw new DuplicateEntityException(PhrasesConstant.FILE_UPLOAD_FAILED);
    }

    public Map<String, String> uploadFile(MultipartFile multipartFile) {
        if (multipartFile == null) {
            throw new ResourceNotFoundException();
        }

        Map<String, String> result = new HashMap<String, String>();

        String originalFilename = multipartFile.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        String fileName = localFilePath + uuid + "." + extension;

        try {
            FileUtils.writeByteArrayToFile(new File(fileName), multipartFile.getBytes());
        } catch (IOException e) {
            throw new ServerFailureException(PhrasesConstant.FILE_UPLOAD_FAILED);
        }

        result.put("path", fileName);
        result.put("name", originalFilename);

        return result;
    }

    /**
     * 读取GEO经纬度Excel文件。
     *
     * @param path Excel文档路径
     * @return 包含若干个Map的List集合，其中Map的key“Lng”表示经度，key“Lat”表示纬度
     */
    public List<Map<String, String>> readGeoExcel(String path) {
        if (StringUtils.isEmpty(path)) {
            throw new ServerFailureException(FILE_NOT_FOUND);
        }

        List<Map<String, String>> result = new ArrayList<Map<String, String>>();

        ExcelUtil excelUtil = new ExcelUtil(path);
        excelUtil.setStartReadPos(1);

        List<Row> rowList = null;
        try {
            rowList = excelUtil.readExcel();
        } catch (IOException e) {
            log.error(FILE_READ_ERROR, e);
            throw new ServerFailureException(FILE_READ_ERROR);
        }

        for (Row row : rowList) {
            Map<String, String> tmpMap = new HashMap<String, String>();
            tmpMap.put("Lng", String.valueOf(row.getCell(0).getNumericCellValue())); // 经度
            tmpMap.put("Lat", String.valueOf(row.getCell(1).getNumericCellValue())); // 纬度

            result.add(tmpMap);
        }

        return result;
    }
}
