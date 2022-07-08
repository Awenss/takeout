package com.han.ruoji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.han.ruoji.common.CoustmentExcepiton;
import com.han.ruoji.entity.Category;
import com.han.ruoji.entity.Dish;
import com.han.ruoji.entity.Setmeal;
import com.han.ruoji.mapper.CateGoryMapper;
import com.han.ruoji.service.service.CategoryService;
import com.han.ruoji.service.service.DishService;
import com.han.ruoji.service.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CateGoryServiceImpl extends ServiceImpl<CateGoryMapper,Category> implements CategoryService {

    @Autowired
    public DishService dishService;


    @Autowired
    public SetmealService setmealService;


    //根据id删除分类，删除之前进行判断

    @Override
    public boolean remove(Long id) {



        //查询当前分类是否关联菜品，如果已经关联，抛出一个业务异常

        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(queryWrapper);
        if(count>0){
            //已经关联

            throw new CoustmentExcepiton("当前分类下关联了菜品，不能删除");

        }

        //查询当前分类是否关联套餐，如果已经关联，抛出一个业务异常

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper =new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        if(count1>0){
            //已经关联
            throw new CoustmentExcepiton("当前分类下关联了套餐，不能删除");
        }


        //正常删除
        super.removeById(id);
        return true;
    }
}
