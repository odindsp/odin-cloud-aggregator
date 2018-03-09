package com.pxene.odin.cloud.common.util;

import java.io.File;
import java.io.IOException;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileUtil {
	
	/**
	 * 上传文件到本地
	 * @param uploadDir
	 * @param fileName
	 * @param file
	 * @return
	 */
	public static String uploadFileToLocal(String uploadDir, String fileName, MultipartFile file) {
		String path = null;
        String name = file.getOriginalFilename();
        String fileExtension = getFileExtensionByDot(name);
        path = uploadDir + fileName + "." + fileExtension;
        
        // 上传至本地
        try {
			org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path), file.getBytes());
		} catch (IOException e) {			
			e.printStackTrace();
		}
        
        return path;
	}
	
	/**
     * 获取文件的后缀
     * @param contentType
     * @return
     */
    public static String getFileExtensionByDot(String contentType) {

    	return getFileExtension(".", contentType);      	
    }
    
    public static String getFileExtension(String seperator, String source) {    	
    	String[] tokenizeToStringArray = StringUtils.tokenizeToStringArray(source, seperator, true, true);
    	if (tokenizeToStringArray.length >= 2) {
    		return tokenizeToStringArray[tokenizeToStringArray.length-1];
    	}
		return null;    	
    }
}
