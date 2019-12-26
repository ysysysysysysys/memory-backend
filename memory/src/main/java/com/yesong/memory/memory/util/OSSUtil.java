package com.yesong.memory.memory.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

@Component
public class OSSUtil {
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;
    @Value("${sliyun.oss.accessKeySecret}")
    private String accessKeySecret;
    @Value("${oss.bucket}")
    private String bucket;

    public String getUploadUrl(Long time,String objectName){
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        Date expiration = new Date(time);
        URL url = ossClient.generatePresignedUrl(bucket, objectName, expiration);
        ossClient.shutdown();
        return url.toString();
    }

    public PutObjectResult upload(MultipartFile file, String path, OSS ossClient) throws IOException {
        File f = FileUtil.getFile(file);
        FileInputStream fileInputStream = new FileInputStream(f);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, path, fileInputStream);
        FileUtil.deleteFile(f);
        return ossClient.putObject(putObjectRequest);
    }

    public Long getExpireTime(){
        return new Date().getTime() + 3600 * 100000;
    }

}
