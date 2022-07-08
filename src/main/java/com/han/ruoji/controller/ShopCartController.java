package com.han.ruoji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.han.ruoji.common.BaseContext;
import com.han.ruoji.common.R;
import com.han.ruoji.entity.ShoppingCart;
import com.han.ruoji.service.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShopCartController {

    @Autowired
    ShoppingCartService ShoppingCartService;

    /*
    * 添加购物车
    * */
    @PostMapping("/add")
    public R<ShoppingCart> addShopCart(@RequestBody ShoppingCart shoppingCart){


        log.info("添加的商品::{}",shoppingCart);

        Long userId= BaseContext.getCurrentId();//登录的用户id


        //设置用户userId

        shoppingCart.setUserId(userId);

        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<ShoppingCart>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        //查询当前菜品是否存在
        Long dishId = shoppingCart.getDishId();
        if(dishId !=null){
            //菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            //套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        //SQL:: select * form shopping_cart where user_id= ? and dishId=?/setmealId=?
        ShoppingCart CartOne = ShoppingCartService.getOne(queryWrapper);

        //  LambdaQueryWrapper<ShoppingCart>

        if(CartOne!=null){
            //存在 Num+1
            Integer number = CartOne.getNumber();
            CartOne.setNumber(number+1);
            ShoppingCartService.updateById(CartOne);

        }else {
            //不存在 num1
            shoppingCart.setNumber(1);
            ShoppingCartService.save(shoppingCart);
            CartOne=shoppingCart;
        }





        return R.success(CartOne);
    }

    /*
    * 减少菜品
    * */

    @PostMapping("/sub")
    public R<String> subtract(@RequestBody ShoppingCart shoppingCart){

        log.info("商品id{}减少",shoppingCart.getId());

        Long dishId=shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper=new LambdaQueryWrapper<>();
        //查询用户id和登录的用户id
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        if(dishId!=null){
            //存在查询菜单
           shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {
            //不存在查询套餐
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart CartOne = ShoppingCartService.getOne(shoppingCartLambdaQueryWrapper);

        if(CartOne!=null){
            Integer number = CartOne.getNumber();
            if(number<1){
                ShoppingCartService.removeById(CartOne);
            }else {
                CartOne.setNumber(number-1);
                ShoppingCartService.updateById(CartOne);
            }
        }


        return R.success("减少成功");

    }





    /*
    * 查看购物车
    * */
    @GetMapping("/list")
    public R<List<ShoppingCart>> getShoppingCartList(){

        log.info("查看当前{}id用户购物车",BaseContext.getCurrentId());

        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        List<ShoppingCart> list = ShoppingCartService.list(queryWrapper);


        return R.success(list);
    }


    /*
    * 清空购物车
    * */

    @DeleteMapping("/clean")
    public R<String> cleanShoppingCart(){

        log.info("清空购物车的用户id:{}",BaseContext.getCurrentId());

        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        ShoppingCartService.remove(queryWrapper);
        return R.success("清空成功");
    }

}
