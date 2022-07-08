package com.han.ruoji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.han.ruoji.dto.DishDto;
import com.han.ruoji.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    //新增菜品同时。插入口味数据‘操作两张表
    public void saveWithFlavor(DishDto dto);


    //根据id查询菜品信息以及口味信息
    public DishDto getWithById(Long id);


    //更新菜品信息及口味信息
    boolean updateWithFlavor(DishDto dishDto);

    //删除菜品信息及口味
    boolean removeByIdWithFlavor(List<Long> ids);

   // boolean updateStatusById(int status, Long[] ids);

}
