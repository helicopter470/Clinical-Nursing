package com.example.controller;

import cn.hutool.core.io.FileUtil;
import com.example.common.Result;
import com.example.exception.CustomException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileController {

    /**
     * 上传目录改为可配置，避免写死 user.dir + "/files/" 导致不同环境不可控。
     * application.yml: files.storageDir
     */
    @Value("${files.storageDir:${user.dir}/files}")
    private String storageDir;

    @Value("${fileBaseUrl}")
    private String fileBaseUrl;

    // 仅允许常见图片类型（按需扩展）
    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp", "gif");

    //文件上传
    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException("文件不能为空");
        }

        String original = file.getOriginalFilename();
        String ext = getExtension(original);
        if (!ALLOWED_EXT.contains(ext)) {
            throw new CustomException("不支持的文件类型，仅允许：jpg/jpeg/png/webp/gif");
        }

        // 服务端生成文件名，避免用户原始文件名带来的路径穿越/特殊字符问题
        String safeName = System.currentTimeMillis() + "-" + UUID.randomUUID().toString().replace("-", "") + (ext.isEmpty() ? "" : ("." + ext));
        String dir = FileUtil.normalize(storageDir);
        String realFilePath = FileUtil.normalize(dir + "/" + safeName);

        try {
            if (!FileUtil.isDirectory(dir)) {
                FileUtil.mkdir(dir);
            }
            FileUtil.writeBytes(file.getBytes(), realFilePath);
        } catch (IOException e) {
            throw new CustomException("文件上传失败");
        }

        String url = fileBaseUrl + "/files/download/" + safeName;
        return Result.success(url);
    }

    @GetMapping("/download/{fileName}")
    public void download(@PathVariable String fileName, HttpServletResponse response) {
        // 防路径穿越：只允许文件名本身，不允许带路径分隔符
        if (!StringUtils.hasText(fileName) || fileName.contains("/") || fileName.contains("\\")) {
            throw new CustomException("文件名不合法");
        }

        String dir = FileUtil.normalize(storageDir);
        String realFilePath = FileUtil.normalize(dir + "/" + fileName);
        if (!FileUtil.exist(realFilePath) || FileUtil.isDirectory(realFilePath)) {
            throw new CustomException("文件不存在");
        }

        // 简单按扩展名设置 Content-Type（也可以统一 octet-stream）
        String ext = getExtension(fileName);
        if ("png".equals(ext)) response.setContentType(MediaType.IMAGE_PNG_VALUE);
        else if ("jpg".equals(ext) || "jpeg".equals(ext)) response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        else if ("gif".equals(ext)) response.setContentType(MediaType.IMAGE_GIF_VALUE);
        else if ("webp".equals(ext)) response.setContentType("image/webp");
        else response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        // 兼容中文文件名下载（这里 fileName 为服务器生成的安全名）
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));

        try {
            byte[] bytes = FileUtil.readBytes(realFilePath);
            ServletOutputStream os = response.getOutputStream();
            os.write(bytes);
            os.flush();
            os.close();
        } catch (IOException e) {
            throw new CustomException("文件下载失败");
        }
    }

    private static String getExtension(String filename) {
        if (filename == null) {
            return "";
        }
        String clean = filename.trim();
        int idx = clean.lastIndexOf('.');
        if (idx < 0 || idx == clean.length() - 1) {
            return "";
        }
        return clean.substring(idx + 1).toLowerCase();
    }
}
