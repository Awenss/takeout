package com.han.ruoji.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.han.ruoji.entity.OrderDetail;
import com.han.ruoji.mapper.OrderDetailMapper;
import com.han.ruoji.service.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
