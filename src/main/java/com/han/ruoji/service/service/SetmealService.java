package com.han.ruoji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.han.ruoji.dto.SetmealDto;
import com.han.ruoji.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /*
    * 新增套餐的同时更新套餐和菜品的关联信息
    * */
    public boolean saveWithDish(SetmealDto setmealDto );


    /**
     *
     * 修改套餐页面回显数据
     */
    public SetmealDto getSetmealWithId(Long id);


    /*
    * 根据id修改套餐信息，同时修改套餐关系信息
    * */
    public boolean updateSetmealWithId(SetmealDto setmealDto);


    /*
    * 根据id删除套餐信息，同时套餐关联的菜单信息
    * */
    public boolean deleteSetmealWithId(List<Long> ids);


    /*
    *
    * */
    //public boolean updaStatu(int status,List<Long> ids);

}
