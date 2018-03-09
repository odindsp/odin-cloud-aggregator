package com.pxene.odin.cloud.web.controller;

import static com.pxene.odin.cloud.common.constant.PhrasesConstant.FILE_READ_ERROR;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pxene.odin.cloud.exception.ServerFailureException;
import com.pxene.odin.cloud.service.FileUploadService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangshiyi
 */
@RestController
@Slf4j
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadImage(
        @RequestParam(value = "checkSize", required = false, defaultValue = "1") String checkSize,
        @RequestParam(value = "image") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fileUploadService.uploadImage(checkSize, file));
    }

    @PostMapping(value = "/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadVideo(@RequestParam(value = "video") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fileUploadService.uploadVideo(file));
    }

    @PostMapping(value = "/geo-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadExcel(@RequestPart(value = "file", required = true) MultipartFile multipartFile) {
        Map<String, String> body = fileUploadService.uploadFile(multipartFile);

        try {
            String path = body.get("path");
            fileUploadService.readGeoExcel(path);
        } catch (RuntimeException e) {
            log.error(FILE_READ_ERROR, e);
            throw new ServerFailureException(FILE_READ_ERROR);
        }

        ResponseEntity<Map<String, String>> responseEntity = new ResponseEntity<Map<String, String>>(body, HttpStatus.CREATED);
        return responseEntity;
    }

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadFileToLocal(
        @RequestPart(value = "file", required = true) MultipartFile multipartFile) {
        Map<String, String> body = fileUploadService.uploadFile(multipartFile);

        ResponseEntity<Map<String, String>> responseEntity = new ResponseEntity<Map<String, String>>(body, HttpStatus.CREATED);
        return responseEntity;
    }
}
