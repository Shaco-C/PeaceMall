package com.peacemall.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;

@Configuration
public class MyMetaObjectHandler implements MetaObjectHandler {
    // 在插入数据时，自动填充createdAt字段
    @Override
    public void insertFill(MetaObject metaObject) {
        // 判断metaObject中是否存在createdAt字段
        if (metaObject.hasGetter("createdAt")) {
            // 使用strictInsertFill方法填充createdAt字段，类型为Timestamp，值为当前时间
            this.strictInsertFill(metaObject, "createdAt", Timestamp.class, new Timestamp(System.currentTimeMillis()));
        }
        // 判断metaObject中是否存在updatedAt字段
        if (metaObject.hasGetter("updatedAt")) {
            // 使用strictInsertFill方法填充updatedAt字段，类型为Timestamp，值为当前时间
            this.strictInsertFill(metaObject, "updatedAt", Timestamp.class, new Timestamp(System.currentTimeMillis()));
        }
    }

    // 在更新数据时，自动填充updatedAt字段
    @Override
    public void updateFill(MetaObject metaObject) {
        // 判断metaObject中是否存在updatedAt字段
        if (metaObject.hasGetter("updatedAt")) {
            // 使用strictUpdateFill方法填充updatedAt字段，类型为Timestamp，值为当前时间
            this.strictUpdateFill(metaObject, "updatedAt", Timestamp.class, new Timestamp(System.currentTimeMillis()));
        }
    }
}
