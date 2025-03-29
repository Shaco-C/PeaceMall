package com.watergun.controller;

import com.peacemall.common.domain.R;
import com.watergun.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Api(tags = "文件上传")
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {
    private final FileService fileService;

    @ApiOperation("上传文件到阿里云OSS")
    @PostMapping("/uploadToAliOSS/single")
    public R<String> uploadFileToAliOSS(@RequestParam("file") MultipartFile file){
        return fileService.uploadFileToAliOSS(file);
    }


    @ApiOperation("上传多个文件到阿里云OSS")
    @PostMapping("/uploadToAliOSS/multi")
    public R<List<String>> uploadFilesToAliOSS(@RequestParam("files")MultipartFile[] files){
        return fileService.uploadFilesToAliOSS(files);
    }
}
