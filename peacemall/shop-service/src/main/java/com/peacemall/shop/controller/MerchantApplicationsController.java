package com.peacemall.shop.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peacemall.common.domain.R;
import com.peacemall.shop.domain.po.MerchantApplications;
import com.peacemall.shop.domain.vo.AdminCheckApplications;
import com.peacemall.shop.enums.ApplicationStatus;
import com.peacemall.shop.service.MerchantApplicationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("用户申请成为商家相关接口")
@RequestMapping("/merchant-applications")
@RequiredArgsConstructor
@RestController
public class MerchantApplicationsController {

    private final MerchantApplicationService merchantApplicationService;

    //用户申请成为商家
    @ApiOperation(value = "用户申请成为商家")
    @PostMapping("/userCreateMerchantApplication")
    public R<String> userCreateMerchantApplication(@RequestBody MerchantApplications merchantApplications){
        return merchantApplicationService.userCreateMerchantApplication(merchantApplications);
    }

    //用户取消申请
    @ApiOperation(value = "用户取消成为商家申请")
    @PutMapping("/userCancelMerchantApplication")
    public R<String> userCancelMerchantApplication(@RequestParam(value = "applicationId") Long applicationId){
        return merchantApplicationService.userCancelMerchantApplication(applicationId);
    }

    //用户查看自己的申请记录
    //在这个页面中，可以选择取消申请
    @ApiOperation(value = "用户查看自己的申请记录")
    @GetMapping("/userGetMerchantApplication")
    public R<List<MerchantApplications>> userGetMerchantApplication(){
        return merchantApplicationService.userGetMerchantApplication();
    }

    //管理员查看特定状态的申请记录
    @ApiOperation(value = "管理员查看特定状态的申请记录")
    @GetMapping("/admin/adminGetMerchantApplicationByStatus")
    public R<Page<MerchantApplications>> adminGetMerchantApplicationByStatus(@RequestParam(value = "page",defaultValue = "1")int page,
                                                                             @RequestParam(value = "pageSize",defaultValue = "10")int pageSize,
                                                                             @RequestParam(value = "merchantApplicationStatus",defaultValue = "PENDING")ApplicationStatus merchantApplicationStatus){
        return merchantApplicationService.adminGetMerchantApplicationByStatus(page,pageSize,merchantApplicationStatus);
    }
    //管理员审批用户请求
    @ApiOperation(value = "管理员审批用户请求")
    @PutMapping("/admin/adminCheckMerchantApplication")
    public R<String> adminCheckMerchantApplication(@RequestBody AdminCheckApplications adminCheckApplications){
        return merchantApplicationService.adminCheckMerchantApplication(adminCheckApplications);
    }
}
