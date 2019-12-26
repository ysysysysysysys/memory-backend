package com.yesong.memory.memory.ServiceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.internal.OSSUtils;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.yesong.memory.memory.config.PutObjectProgressListener;
import com.yesong.memory.memory.entity.UploadInfo;
import com.yesong.memory.memory.enums.MemoryType;
import com.yesong.memory.memory.mapper.UploadMapper;
import com.yesong.memory.memory.request.DownloadRequest;
import com.yesong.memory.memory.request.UploadRequest;
import com.yesong.memory.memory.response.CommonResponse;
import com.yesong.memory.memory.response.UploadInfoResponse;
import com.yesong.memory.memory.service.OSSService;
import com.yesong.memory.memory.util.CommonUtil;
import com.yesong.memory.memory.util.OSSUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class OSSServiceImpl implements OSSService {
    private final static Logger log = LoggerFactory.getLogger(OSSServiceImpl.class);
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;
    @Value("${sliyun.oss.accessKeySecret}")
    private String accessKeySecret;
    @Value("${oss.bucket}")
    private String bucket;
    @Value("${download.temp.path}")
    private String path;

    @Autowired
    private OSSUtil ossUtil;
    @Autowired
    private UploadMapper uploadMapper;

    @Override
    public CommonResponse upLoad(List<MultipartFile> file, MemoryType memoryType, String account) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        //返回值
        List<UploadInfoResponse> uploadInfoResponses = new ArrayList<>();
        //上传
        if (file != null) {
            uploadInfoResponses = file.parallelStream().map(multipartFile -> {
                try {
                    //获得path
                    String path = CommonUtil.getName(account, memoryType.getPath(), multipartFile.getOriginalFilename());
                    PutObjectResult upload = ossUtil.upload(multipartFile, path, ossClient);
                    log.info("上传结果{}", upload);
                    //获取图片url
                    Long expireTime = ossUtil.getExpireTime();
                    String objectName = path;
                    String uploadUrl = ossUtil.getUploadUrl(expireTime, objectName);
                    //封装返回值
                    UploadInfoResponse uploadInfoResponse = new UploadInfoResponse();
                    uploadInfoResponse.setName(multipartFile.getOriginalFilename());
                    uploadInfoResponse.setUrl(uploadUrl);
                    //入库
                    UploadInfo uploadInfo = new UploadInfo();
                    uploadInfo.setAccount(account);
                    uploadInfo.setExpireTime(expireTime);
                    uploadInfo.setObjectName(objectName);
                    uploadInfo.setType(memoryType.getKey());
                    Calendar cal = Calendar.getInstance();
                    int i = cal.get(Calendar.YEAR);
                    uploadInfo.setYear(i);
                    uploadInfo.setOriginalFilename(multipartFile.getOriginalFilename());
                    uploadInfo.setUrl(uploadUrl);
                    uploadMapper.insert(uploadInfo.getCreateEntity());
                    return uploadInfoResponse;
                } catch (Exception e) {
                    log.error("上传文件{}出错{}", multipartFile.getOriginalFilename(), e);
                    return null;
                }
            }).collect(Collectors.toList());

        }
        ossClient.shutdown();
        return CommonResponse.builder().success(true).body(JSONObject.toJSON(uploadInfoResponses)).build();
    }

    @Override
    public CommonResponse getUploadList(UploadRequest request) {
        Page<UploadInfo> uploadInfoPage = new Page<>();
        uploadInfoPage.setCurrent(request.getPageNum());
        uploadInfoPage.setSize(request.getPageSize());
        Page<UploadInfo> upload = uploadMapper.selectPage(uploadInfoPage, new QueryWrapper<UploadInfo>()
                .lambda()
                .eq(UploadInfo::getAccount, request.getAccount())
                .eq(UploadInfo::getType, request.getMemoryType().getKey())
                .eq(UploadInfo::getYear, request.getYear())
                .orderByDesc(UploadInfo::getCreateTime)
        );
        List<UploadInfoResponse> collect = new ArrayList<>();
        if (upload.getRecords() != null) {
            List<UploadInfo> records = upload.getRecords();
            collect = records.stream().map(n -> {
                //如果现在时间大于过期时间 那么重新获取url并更新
                if (System.currentTimeMillis() > n.getExpireTime()) {
                    Long expireTime = ossUtil.getExpireTime();
                    String newUrl = ossUtil.getUploadUrl(expireTime, n.getObjectName());
                    //更新url
                    n.setUrl(newUrl);
                    n.setExpireTime(expireTime);
                    uploadMapper.updateById(n.getUpdateEntity());
                }
                UploadInfoResponse uploadInfoResponse = new UploadInfoResponse();
                BeanUtils.copyProperties(n, uploadInfoResponse);
                uploadInfoResponse.setName(n.getOriginalFilename());
                uploadInfoResponse.setTotal(upload.getTotal());
                return uploadInfoResponse;
            }).collect(Collectors.toList());
        }
        return CommonResponse.builder().success(true).body(JSONObject.toJSON(collect)).build();
    }

    @Override
    public void download(List<DownloadRequest> downloadRequests, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Integer> map = new HashMap<>();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        //暂存路径
        String fileName = downloadRequests.get(0).getAccount() + downloadRequests.get(0).getMemoryType().getKey() + System.currentTimeMillis() + ".zip";
        String realPath = path + fileName;
        log.info("路径{}", realPath);
        File file = new File(realPath);
        //创建压缩流
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file));
            if (downloadRequests != null) {
                downloadRequests.stream().forEach(n -> {
                    String name = CommonUtil.getName(n.getAccount(), n.getMemoryType().getPath(), n.getObjectName());
                    OSSObject ossObject = ossClient.getObject(bucket, name);
                    try {
                        InputStream inputStream = ossObject.getObjectContent();
                        out.putNextEntry(new ZipEntry(getName(n.getObjectName(), map)));
                        int len = 0;
                        byte[] bytes = new byte[1024];
                        while ((len = inputStream.read(bytes)) > 0) {
                            out.write(bytes, 0, len);
                        }
                        out.closeEntry();
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error("下载到服务器失败{}", e);
        }
        this.out(response, realPath, fileName);
    }

    /**
     * 重复名字加特殊字符
     */
    private String getName(String name, Map<String, Integer> map) {
        //判断是否重复用的
        boolean exist = map.containsKey(name);
        if (exist) {
            //如果存在
            String[] split = name.split("\\.");
            StringBuilder stringBuilder = new StringBuilder();
            String s = stringBuilder.append(split[0])
                    .append("(").append(map.get(name)).append(").").append(split[1]).toString();
            map.put(name, map.get(name) + 1);
            return s;
        } else {
            map.put(name, 1);
            return name;
        }
    }

    private void out(HttpServletResponse response, String realpath, String name) {

        try {
            response.reset();
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(name, "UTF-8"));
            response.setContentType("application/octet-stream");
            File file = new File(realpath);
            response.addHeader("Content-Length", "" + file.length());
            if (file.exists()) {
                try (
                     BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(realpath));
                     BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());) {
                    int len = 0;
                    byte[] bytes = new byte[1024];
                    while ((len = bufferedInputStream.read(bytes)) != -1) {
                        bufferedOutputStream.write(bytes, 0, len);
                    }
                } catch (IOException e){

                }
            } else {
                log.error("文件不存在");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
