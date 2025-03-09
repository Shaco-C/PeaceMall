package com.peacemall.shop.controller;

import com.peacemall.shop.service.ShopsService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("商家商店服务相关接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/shops")
public class ShopsController {

    private final ShopsService shopsService;

}
