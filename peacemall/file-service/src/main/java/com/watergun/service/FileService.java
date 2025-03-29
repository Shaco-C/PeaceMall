package com.watergun.service;

import com.peacemall.common.domain.R;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    R<String> uploadFileToAliOSS(MultipartFile file);

    R<List<String>> uploadFilesToAliOSS(MultipartFile[] files);
}
