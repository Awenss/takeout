package com.han.ruoji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.ruoji.common.BaseContext;
import com.han.ruoji.common.R;
import com.han.ruoji.dto.OrdersDto;
import com.han.ruoji.entity.OrderDetail;
import com.han.ruoji.entity.Orders;
import com.han.ruoji.service.service.OrderDetailService;
import com.han.ruoji.service.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService ordersService;

    @Autowired
    OrderDetailService orderDetailService;

/*
* 用户下单
* */
    @PostMapping("/submit")
    public R<String> submitOrder(@RequestBody Orders order){

        ordersService.SubmitOrderWithId(order);

        log.info("订单信息::{}",order.toString());

        return R.success("下单成功");
    }


    @GetMapping("/userPage")
    public R<List<OrdersDto>> getOrdersDtoList(int page,int pageSize){


        //当前登录用户id
        Long loginUserId=BaseContext.getCurrentId();

        //查询当前用户id的所有订单
        log.info("当前用户id::{}的订单：,页数:{},页面大小:{}",loginUserId,page,pageSize);
        Page<Orders> pageInfor=new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> orderquery = new LambdaQueryWrapper<>();
        orderquery.eq(Orders::getUserId,loginUserId);
        ordersService.page(pageInfor, orderquery);

        List<Orders> Orderrecords = pageInfor.getRecords();


        /*
        * 查询订单详情数据
        * */

        List<OrdersDto> ordersDtoList=null;

        ordersDtoList= Orderrecords.stream().map((item)->{
            LambdaQueryWrapper<OrderDetail> orderdetailquery = new LambdaQueryWrapper<>();
            OrdersDto OrdersDto = new OrdersDto();
            orderdetailquery.eq(OrderDetail::getOrderId,item.getNumber());

            List<OrderDetail> list = orderDetailService.list(orderdetailquery);
           BeanUtils.copyProperties(item,OrdersDto);
            OrdersDto.setOrderDetails(list);
            return OrdersDto;

        }).collect(Collectors.toList());

//        LambdaQueryWrapper<OrderDetail> OrderDetail=new LambdaQueryWrapper<>();
//        OrderDetail.eq()


        return R.success(ordersDtoList);

    };





    @GetMapping("/page")
    public R<List<OrdersDto>> getAdminOrdersDtoList(int page,int pageSize){

        //当前登录用户id
        Long loginUserId=BaseContext.getCurrentId();

        //查询当前用户id的所有订单
        log.info("当前用户id::{}的订单：,页数:{},页面大小:{}",loginUserId,page,pageSize);
        Page<Orders> pageInfor=new Page<>(page,pageSize);
       // LambdaQueryWrapper<Orders> orderquery = new LambdaQueryWrapper<>();
       // orderquery.eq(Orders::getUserId,loginUserId);
        ordersService.page(pageInfor);

        List<Orders> Orderrecords = pageInfor.getRecords();


        /*
         * 查询订单详情数据
         * */

        List<OrdersDto> ordersDtoList=null;

        ordersDtoList= Orderrecords.stream().map((item)->{
            LambdaQueryWrapper<OrderDetail> orderdetailquery = new LambdaQueryWrapper<>();
            OrdersDto OrdersDto = new OrdersDto();
            orderdetailquery.eq(OrderDetail::getOrderId,item.getNumber());

            List<OrderDetail> list = orderDetailService.list(orderdetailquery);
            BeanUtils.copyProperties(item,OrdersDto);
            OrdersDto.setOrderDetails(list);
            return OrdersDto;

        }).collect(Collectors.toList());

//        LambdaQueryWrapper<OrderDetail> OrderDetail=new LambdaQueryWrapper<>();
//        OrderDetail.eq()


        return R.success(ordersDtoList);

    };


    @PutMapping()
    public R<String> AdminOderStatus(@RequestBody Orders orders) {

        LambdaUpdateWrapper<Orders> updateWrapper=new LambdaUpdateWrapper<>();

       // updateWrapper.eq(Orders::getId,orders.getId(),orders.getStatus());


        log.info("要修改状态的商品id::{}",orders.getId());
        log.info("状态修改为：{}",orders.getStatus());
        updateWrapper.eq(Orders::getId,orders.getId());
        updateWrapper.set(Orders::getStatus,orders.getStatus());
        boolean b = ordersService.update(updateWrapper);

        if(b){
            return R.success("修改成功");
        }
        return R.error("修改失败");



    }





}
