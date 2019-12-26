package com.yesong.memory.memory.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileUtil {
    public static File getFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String prefix = originalFilename.substring(originalFilename.lastIndexOf("."));
        final File excelFile = File.createTempFile(UUID.randomUUID().toString(), prefix);
        file.transferTo(excelFile);
        return excelFile;
    }

    public static void deleteFile(File... files) {
        if (files != null) {
            for (File file : files) {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }
}
