package com.peacemall.review.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.review.domain.po.Reviews;
import com.peacemall.review.mapper.ReviewsMapper;
import com.peacemall.review.service.ReviewsService;
import org.springframework.stereotype.Service;

@Service
public class ReviewsServiceImpl extends ServiceImpl<ReviewsMapper, Reviews> implements ReviewsService {

}
