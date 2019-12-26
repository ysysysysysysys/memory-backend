package com.yesong.memory.memory.action;

import com.yesong.memory.memory.annotations.Auth;
import com.yesong.memory.memory.enums.MemoryType;
import com.yesong.memory.memory.request.DownloadRequest;
import com.yesong.memory.memory.request.UploadRequest;
import com.yesong.memory.memory.response.CommonResponse;
import com.yesong.memory.memory.service.OSSService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/oss")
@Api(value = "oss", description = "oss相关")
@Auth
public class OSSAction {
    @Autowired
    private OSSService ossService;

    @RequestMapping(value = "/upload", method = {RequestMethod.POST})
    @ApiOperation(value = "上传")
    public CommonResponse upload(@RequestParam("file") List<MultipartFile> file, MemoryType type, Date date, HttpServletRequest request) {
        String account = request.getHeader("account");
        CommonResponse commonResponse = ossService.upLoad(file, type, account);
        return commonResponse;
    }

    @RequestMapping(value = "/getUploadList", method = {RequestMethod.POST})
    @ApiOperation(value = "获取上传的内容")
    public CommonResponse getUploadList(@RequestBody UploadRequest uploadRequest) {
        CommonResponse uploadList = ossService.getUploadList(uploadRequest);
        return uploadList;
    }

    @RequestMapping(value = "/download", method = {RequestMethod.POST})
    @ApiOperation(value = "download")
    public void download(@RequestBody List<DownloadRequest> downloadRequests, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ossService.download(downloadRequests,request,response);
    }

    @RequestMapping(value = "/test", method = {RequestMethod.POST})
    @ApiOperation(value = "test")
    public void test(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("test.jpg", "UTF-8"));
        response.setContentType("application/octet-stream;charset=utf-8");
        File file = new File("/Users/yesong/Pictures/2019/图片/7C7DEE87-18C0-4FB7-9B08-90EED72F143B-5284-000006BF316C0323.jpg");
        try(BufferedInputStream b = new BufferedInputStream(new FileInputStream(file));
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream())){
            byte[] bytes = new byte[b.available()];
            b.read(bytes);
            out.write(bytes);
            out.flush();
        }
    }
}
