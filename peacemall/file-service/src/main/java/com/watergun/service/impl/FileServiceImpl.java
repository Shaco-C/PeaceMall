package com.watergun.service.impl;

import com.peacemall.common.domain.R;
import com.watergun.service.FileService;
import com.watergun.utils.AliOSSUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final AliOSSUtils aliOSSUtils;

    @Override
    public R<String> uploadFileToAliOSS(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return R.error("上传文件不能为空！");
        }

        log.info("将文件上传到阿里云OSS：{}", file.getOriginalFilename());
        try {
            String fileUrl = aliOSSUtils.upload(file);
            return R.ok(fileUrl);
        } catch (IOException e) {
            log.error("上传文件失败，文件名：{}", file.getOriginalFilename(), e);
            return R.error("上传文件失败，请稍后重试");
        }
    }

    @Override
    public R<List<String>> uploadFilesToAliOSS(MultipartFile[] files) {
        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String fileUrl = aliOSSUtils.upload(file);
                fileUrls.add(fileUrl);
            } catch (Exception e) {
                log.error("文件上传失败，文件名：{}", file.getOriginalFilename(), e);
                return R.error("部分文件上传失败，请稍后重试");
            }
        }
        return R.ok(fileUrls);
    }
}
