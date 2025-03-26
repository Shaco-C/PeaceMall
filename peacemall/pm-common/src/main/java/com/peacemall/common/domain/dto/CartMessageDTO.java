package com.peacemall.common.domain.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class CartMessageDTO implements Serializable {
    private Long userId;
    private List<Long> configIds;
    private String operation; // 操作类型，例如 "DELETE"
}
