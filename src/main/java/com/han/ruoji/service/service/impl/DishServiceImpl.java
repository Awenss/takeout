package com.han.ruoji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.han.ruoji.common.CoustmentExcepiton;
import com.han.ruoji.dto.DishDto;
import com.han.ruoji.entity.Dish;
import com.han.ruoji.entity.DishFlavor;
import com.han.ruoji.mapper.DishMapper;
import com.han.ruoji.service.service.DishFlavorService;
import com.han.ruoji.service.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements  DishService {
    /*
     * 新增菜品同时新增口味数据
     * */
    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品基本信息到菜品表
        this.save(dishDto);

        Long dishid = dishDto.getId();//菜品id
        //保存口味到口味表

        List<DishFlavor> flavors = dishDto.getFlavors();//口味

        //菜品口味
        //使用stream流的方式给flavors表中的菜品id赋值
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishid);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);


    }

    /**
     * 根据id查询菜品和口味信息
     */

    @Override
    public DishDto getWithById(Long id) {

        //查询菜品基本信息
        Dish byIdDish = this.getById(id);

        DishDto dishDto = new DishDto();


        //将id查询的菜品信息拷贝到新的dish对象中
        BeanUtils.copyProperties(byIdDish, dishDto);

        //查询菜品口味信息

        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();

        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, byIdDish.getId());
        List<DishFlavor> list = dishFlavorService.list(dishFlavorLambdaQueryWrapper);

        //将根据dish菜品id对应的口味表信息 设置到新的dishDto中
        dishDto.setFlavors(list);
        return dishDto;
    }

    @Transactional
    @Override
    public boolean updateWithFlavor(DishDto dishDto) {
        //更新dish菜品表
        this.updateById(dishDto);
        //更新口味表
        //1.先清理
        //2.在提交
        LambdaQueryWrapper<DishFlavor> lambdaQuery = new LambdaQueryWrapper<>();
        lambdaQuery.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(lambdaQuery);
        //添加
        List<DishFlavor> flavors = dishDto.getFlavors();
        //使用stream流的方式给flavors表中的菜品id赋值

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        boolean b = dishFlavorService.saveBatch(flavors);

        if (b) {
            return true;
        }
        return false;

    }


    /**
     * 删除菜品信息的同时，删除菜品对应的口味信息----批量删除
     */

    @Override
    public boolean removeByIdWithFlavor(List<Long> id) {

        LambdaQueryWrapper<Dish> dishquery = new LambdaQueryWrapper<Dish>();
        dishquery.in(Dish::getId,id);
        dishquery.eq(Dish::getStatus,1);
        int count = this.count(dishquery);
        if(count>0){
            throw new CoustmentExcepiton("菜品正在售卖中，无法删除");
        }

        this.removeByIds(id);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId, id);
        boolean remove = dishFlavorService.remove(queryWrapper);
        if (remove) {
            return true;
        }

        return false;
    }

//    @Override
//    public boolean updateStatusById(int status, Long[] ids) {
//
//        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
//        queryWrapper.eq(Dish::getId,ids);
//
//        this.update(status,queryWrapper);
//
//        return false;
//    }


}


    /*
    * 修改状态
    * */



