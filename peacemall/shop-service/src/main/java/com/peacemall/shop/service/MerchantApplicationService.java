package com.peacemall.shop.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.shop.domain.po.MerchantApplications;
import com.peacemall.shop.domain.vo.AdminCheckApplications;
import com.peacemall.shop.enums.ApplicationStatus;

import java.util.List;

public interface MerchantApplicationService extends IService<MerchantApplications> {

    //用户申请成为商家
    R<String> userCreateMerchantApplication(MerchantApplications merchantApplications);

    //用户取消申请
    R<String> userCancelMerchantApplication(Long applicationId);

    //用户查看自己的申请记录
    //在这个页面中，可以选择取消申请
    R<List<MerchantApplications>> userGetMerchantApplication();

    //管理员查看特定状态的申请记录
    R<PageDTO<MerchantApplications>> adminGetMerchantApplicationByStatus(int page, int pageSize, ApplicationStatus merchantApplicationStatus);

    //管理员审批用户请求
    R<String> adminCheckMerchantApplication(AdminCheckApplications adminCheckApplications);


}
