package com.yesong.memory.memory.service;

import com.yesong.memory.memory.enums.MemoryType;
import com.yesong.memory.memory.request.DownloadRequest;
import com.yesong.memory.memory.request.UploadRequest;
import com.yesong.memory.memory.response.CommonResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * OSS service
 */
public interface OSSService {

    public CommonResponse upLoad(List<MultipartFile> file, MemoryType memoryType, String account);

    public CommonResponse getUploadList(UploadRequest request);

    public void download(List<DownloadRequest> downloadRequests, HttpServletRequest request, HttpServletResponse response) throws IOException;
}
