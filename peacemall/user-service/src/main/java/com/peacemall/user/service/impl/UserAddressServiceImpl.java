package com.peacemall.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.user.domain.po.UserAddress;
import com.peacemall.user.mapper.UserAddressMapper;
import com.peacemall.user.service.UserAddressService;
import org.springframework.stereotype.Service;


@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {

}