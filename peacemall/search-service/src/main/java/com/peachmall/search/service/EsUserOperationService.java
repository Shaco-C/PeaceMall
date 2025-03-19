package com.peachmall.search.service;

import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.UserDTO;

import java.util.List;

public interface EsUserOperationService {

    //将mysql中的用户数据,批量导入到es中
    R<String> loadUserDocs();

    //创建user索引
    R<String> createUserIndex();

    //以下的方法都是通过RabbitMQ异步调用的
    //增加用户文档数据
    void addUserDoc(UserDTO userDTO);

    //删除用户文档数据
    void deleteUserDoc(List<Long> userId);

    //修改用户文档数据
    void updateUserDoc(UserDTO userDTO);
}
