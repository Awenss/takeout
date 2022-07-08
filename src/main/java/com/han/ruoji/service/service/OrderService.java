package com.han.ruoji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.han.ruoji.entity.Orders;


public interface OrderService extends IService<Orders> {


    void SubmitOrderWithId(Orders order);


    //void getOrderDto(int page,int pageSize);
}
