package com.pxene.odin.cloud.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 * FTP工具包：图片的上传、下载
 * @author 
 *
 */
public class FtpUtil {
	
	public static boolean uploadFile(String url, int port, String username, String password, String path, String filename, InputStream input)
    {
        boolean success = false;
        FTPClient ftp = new FTPClient();

        try
        {
            int reply;
            ftp.connect(url, port);

            ftp.login(username, password);

            reply = ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply))
            {
                ftp.disconnect();
                return success;
            }

            if (!checkDirectoryExists(ftp, path))
            {
                boolean makeDirectory = makeDirectories(ftp, path);
                System.out.println(makeDirectory);
            }

            ftp.changeWorkingDirectory(path);

            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            ftp.storeFile(filename, input);

            input.close();
            ftp.logout();
            success = true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (ftp.isConnected())
            {
                try
                {
                    ftp.disconnect();
                }
                catch (IOException ioe)
                {
                }
            }
        }
        return success;
    }

    public static boolean downloadFile(String host, int port, String username, String password, String remotePath, String fileName, String localPath)
    {
        boolean result = false;
        FTPClient ftpClient = new FTPClient();

        try
        {
            int reply;
            ftpClient.connect(host, port);

            ftpClient.login(username, password);
            reply = ftpClient.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply))
            {
                ftpClient.disconnect();
                return result;
            }

            // 切换到FTP服务器目录
            ftpClient.changeWorkingDirectory(remotePath);
            FTPFile[] fileList = ftpClient.listFiles();

            for (FTPFile file : fileList)
            {
                if (file.getName().equals(fileName))
                {
                    File localFile = new File(localPath + "/" + file.getName());

                    OutputStream is = new FileOutputStream(localFile);
                    ftpClient.retrieveFile(file.getName(), is);
                    is.close();
                }
            }

            ftpClient.logout();
            result = true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (ftpClient.isConnected())
            {
                try
                {
                    ftpClient.disconnect();
                }
                catch (IOException ioe)
                {
                }
            }
        }
        return result;
    }

    private static boolean checkDirectoryExists(FTPClient ftpClient, String dirPath) throws IOException
    {
        ftpClient.changeWorkingDirectory(dirPath);
        int returnCode = ftpClient.getReplyCode();

        if (returnCode == 550)
        {
            return false;
        }

        return true;
    }

    @SuppressWarnings("unused")
    private static boolean checkFileExists(FTPClient ftpClient, String filePath) throws IOException
    {
        InputStream inputStream = ftpClient.retrieveFileStream(filePath);
        int returnCode = ftpClient.getReplyCode();

        if (inputStream == null || returnCode == 550)
        {
            return false;
        }

        return true;
    }

    public static boolean makeDirectories(FTPClient ftpClient, String dirPath) throws IOException
    {
        String[] pathElements = dirPath.split("/");

        if (pathElements != null && pathElements.length > 0)
        {
            for (String singleDir : pathElements)
            {
                if ("".equals(singleDir))
                {
                    singleDir = "/";
                }

                boolean existed = ftpClient.changeWorkingDirectory(singleDir);
                if (!existed)
                {
                    boolean created = ftpClient.makeDirectory(singleDir);

                    if (created)
                    {
                        System.out.println("CREATED directory: " + singleDir);
                        ftpClient.changeWorkingDirectory(singleDir);
                    }
                    else
                    {
                        System.out.println("COULD NOT create directory: " + singleDir);
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
