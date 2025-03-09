package com.peacemall.shop.domain.vo;

import com.peacemall.shop.enums.ApplicationStatus;
import lombok.Data;

@Data

public class AdminCheckApplications {

    private Long applicationId;

    private Long userId;

    private ApplicationStatus applicationStatus;

    private String reason;
}
