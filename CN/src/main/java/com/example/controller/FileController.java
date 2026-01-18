package com.example.controller;

import cn.hutool.core.io.FileUtil;
import com.example.common.Result;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/files")
public class FileController {

    //创建项目根目录用于存储文件
    private static final String filePath=System.getProperty("user.dir") +"/files/";

    @Value("${fileBaseUrl}")
    private String fileBaseUrl;

    //文件上传
    @PostMapping("/upload")
    public Result upload(MultipartFile file) {
        //定义文件的唯一标识
        String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        //拼接完整的文件储存路径
        String realFilePath = filePath + fileName;
        try{
            // 检查存储目录是否存在，如果不存在则创建
            if(!FileUtil.isDirectory(filePath)){
                FileUtil.mkdir(filePath);
            }
            FileUtil.writeBytes(file.getBytes(),realFilePath);
        }catch (IOException e){
            System.out.println("文件上传错误");
        }
        String url=fileBaseUrl+"/files/download/"+fileName;
        return Result.success(url);
    }
}
