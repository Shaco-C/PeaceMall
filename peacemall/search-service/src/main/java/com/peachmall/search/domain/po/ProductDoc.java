package com.peachmall.search.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Document(indexName = "product")
@AllArgsConstructor
@NoArgsConstructor
public class ProductDoc {

    @Id
    @Field(type = FieldType.Keyword)
    private Long productId;

    @Field(type = FieldType.Keyword)
    private Long categoryId;

    @Field(type = FieldType.Keyword)
    private String categoryName;
    
    @Field(type = FieldType.Keyword)
    private String brand;

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String name;

    @Field(type = FieldType.Scaled_Float, scalingFactor = 100)
    private BigDecimal price;

    @Field(type = FieldType.Keyword, index = false)
    private String imageUrl;

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String description;

    @Field(type = FieldType.Integer)
    private Integer sales;

}
