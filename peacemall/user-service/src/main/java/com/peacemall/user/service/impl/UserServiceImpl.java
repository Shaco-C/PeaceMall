package com.peacemall.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.user.domain.po.Users;
import com.peacemall.user.mapper.UsersMapper;
import com.peacemall.user.service.UserService;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UsersMapper, Users> implements UserService {

}