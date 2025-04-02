package com.peachmall.search.domain.po;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@Document(indexName = "user")
@AllArgsConstructor
@NoArgsConstructor
public class UserDoc {

    @Id
    @Field(type = FieldType.Keyword)
    private Long userId;

    @Field(type = FieldType.Keyword)
    private String username;

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String nickname;

    @Field(type = FieldType.Keyword, index = false)
    private String avatarUrl;

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String signature;

    @Field(type = FieldType.Keyword)
    private String status; // 添加用户状态字段

}
