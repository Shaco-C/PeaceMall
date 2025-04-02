package com.peachmall.search.domain.query;


import com.peacemall.common.domain.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "商品分页查询条件")
public class ProductPageQuery extends PageQuery {
    @ApiModelProperty("搜索关键字")
    private String key;

    @ApiModelProperty("品牌")
    private String brand;
}