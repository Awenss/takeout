package com.han.ruoji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.han.ruoji.common.BaseContext;
import com.han.ruoji.common.CoustmentExcepiton;
import com.han.ruoji.dto.OrdersDto;
import com.han.ruoji.entity.*;
import com.han.ruoji.mapper.OrderMapper;
import com.han.ruoji.service.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {


    @Autowired
    ShoppingCartService shoppingCartService;
    
    @Autowired
    UserService UserService;
    
    @Autowired
    AddressBookService addressBookService;

    @Autowired
    OrderDetailService orderDetailService;
    
    

    /**
     * 用户下单
     * */
    @Override
    @Transactional
    public void SubmitOrderWithId(Orders order) {

        //当前用户id
        Long currentId = BaseContext.getCurrentId();


        //查询当前用户菜单
        LambdaQueryWrapper<ShoppingCart> shoppingCartQuery = new LambdaQueryWrapper<ShoppingCart>();
        shoppingCartQuery.eq(ShoppingCart::getUserId,currentId);
        List<ShoppingCart> shoppingCartServiceOne  = shoppingCartService.list(shoppingCartQuery);

        if(shoppingCartServiceOne==null && shoppingCartServiceOne.size()==0){
            throw new CoustmentExcepiton("购物车为空,无法下单");
        }


        //根据id查询用户信息
        User user = UserService.getById(currentId);


        //查询地址信息

        AddressBook addressBook = addressBookService.getById(order.getAddressBookId());

        if(addressBook==null){
            throw new CoustmentExcepiton("地址信息有有误，无法下单");
        }

        //完成下单，向订单一条数据、


        long orderid = IdWorker.getId();//订单号


        //金额，原子操作//多线程不会出错,保持线程安全
        AtomicInteger amount = new AtomicInteger(0);

        //封装订单明细，计算总金额
        List<OrderDetail> orderDetails= shoppingCartServiceOne.stream().map((item)->{
            OrderDetail orderDetail=new OrderDetail();
            orderDetail.setOrderId(orderid);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            //从list[0]开始;
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue()); //累加操作
            return orderDetail;

        }).collect(Collectors.toList());


        order.setNumber(String.valueOf(orderid));
        order.setUserId(user.getId());
        order.setCheckoutTime(LocalDateTime.now());//下单时间
        order.setOrderTime(LocalDateTime.now());//结账时间
        order.setAmount(new BigDecimal(amount.get()));//实收金额
        order.setStatus(2);//订单状态
        order.setUserName(user.getName());//用户名
        order.setPhone(addressBook.getPhone());//收货人手机
        order.setConsignee(addressBook.getConsignee());//收货人


        order.setAddress(addressBook.getDetail()); //地址

        this.save(order);

        // 订单明细插入多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据
        shoppingCartService.remove(shoppingCartQuery);

    }

//    @Override
//    public void getOrderDto(int page,int pageSize) {
//
//        Page<Orders> pageInfor=new Page<>();
//        LambdaQueryWrapper<Orders> Orderquery=new LambdaQueryWrapper<>();
//        Orderquery.eq(Orders::getUserId,);
//
//
//
//
//
//    }


    /*
    * 订单查询，详情查询
    *
    * */



}
