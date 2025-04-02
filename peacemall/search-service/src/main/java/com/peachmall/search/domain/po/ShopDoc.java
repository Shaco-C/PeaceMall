package com.peachmall.search.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@Document(indexName = "shop")
@AllArgsConstructor
@NoArgsConstructor
public class ShopDoc {

    @Id
    @Field(type = FieldType.Keyword)
    private Long shopId;  // 店铺ID，改为String以防止long精度丢失

    @Field(type = FieldType.Keyword)
    private Long userId;  // 商家用户ID（MERCHANT）

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String shopName;  // 店铺名称，支持模糊搜索

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String shopDescription;  // 店铺简介，支持全文搜索


    @Field(type = FieldType.Keyword, index = false)
    private String shopAvatarUrl;  // 店铺头像URL，不索引
}
