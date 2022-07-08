package com.han.ruoji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.han.ruoji.common.CoustmentExcepiton;
import com.han.ruoji.dto.SetmealDto;
import com.han.ruoji.entity.Setmeal;
import com.han.ruoji.entity.SetmealDish;
import com.han.ruoji.mapper.SetmealMapper;
import com.han.ruoji.service.service.SetmealDishService;
import com.han.ruoji.service.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


    @Autowired
    SetmealDishService setmealDishService;

    @Override
    @Transactional
    public boolean saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息

        this.save(setmealDto);

        //保存套餐对应的菜品信息

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes= setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        boolean b = setmealDishService.saveBatch(setmealDishes);

        if(b){
            return true;
        }
        return false;

    }


    //**
    //
    /*
    * 修改数据前回显给浏览器数据
    * */
    @Override
    public SetmealDto getSetmealWithId(Long id) {

        //先查基本信息
        Setmeal byId = this.getById(id);
        SetmealDto setmealDto =new SetmealDto();

        BeanUtils.copyProperties(byId,setmealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(list);
        return setmealDto;

    }


    /*
    * 修改套餐数据
    * */
    @Transactional
    @Override
    public boolean updateSetmealWithId(SetmealDto setmealDto) {


        //先修改基本信息

        this.updateById(setmealDto);


        //修改菜品表

        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();

        //先清除数据

        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes= setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        boolean b = setmealDishService.saveBatch(setmealDishes);
        if(b){
            return true;
        }

        return false;
    }


    /*
    * 删除套餐信息，同时删除对应的菜单关联信息
    * */
    @Override
    @Transactional
    public boolean deleteSetmealWithId(List<Long> ids) {

        //查询套餐状态，确定是否删除
        LambdaQueryWrapper<Setmeal> query = new LambdaQueryWrapper<>();
        query.in(Setmeal::getId,ids);
        query.eq(Setmeal::getStatus,1);
        int count = this.count(query);
        if(count>0){
            throw new CoustmentExcepiton("套餐正在售卖中，无法删除");
        }

        //可以删除
        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId,ids);
        boolean remove = setmealDishService.remove(queryWrapper);
        if(remove){
            return true;
        }

        return false;
    }


    /*
    * 修改状态---批量修改
    * */

//    @Override
//    public boolean updaStatu(int status, List<Long> ids) {
//
//        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
//        queryWrapper.in(Setmeal::getStatus,ids);
//
//        return false;
//    }
}
