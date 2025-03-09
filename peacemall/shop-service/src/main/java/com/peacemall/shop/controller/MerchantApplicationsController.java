package com.peacemall.shop.controller;


import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("用户申请成为商家相关接口")
@RequestMapping("/merchant-applications")
@RequiredArgsConstructor
@RestController
public class MerchantApplicationsController {
}
