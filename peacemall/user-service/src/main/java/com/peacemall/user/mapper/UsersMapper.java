package com.peacemall.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peacemall.user.domain.po.Users;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UsersMapper extends BaseMapper<Users> {
}
