package com.peacemall.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peacemall.user.domain.po.Users;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UsersMapper extends BaseMapper<Users> {
    // 查询用户名是否存在
    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    int existsByUsername(@Param("username") String username);

    //查询邮箱是否存在
    @Select("SELECT COUNT(*) FROM users WHERE email = #{email}")
    int existsByEmail(@Param("email") String email);
    //查询电话号码是否已经存在
    @Select("SELECT COUNT(*) FROM users WHERE phone_number = #{phoneNumber}")
    int existsByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}
