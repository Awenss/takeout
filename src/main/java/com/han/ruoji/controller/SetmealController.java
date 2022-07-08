package com.han.ruoji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.ruoji.common.R;
import com.han.ruoji.dto.SetmealDto;
import com.han.ruoji.entity.*;
import com.han.ruoji.service.service.CategoryService;
import com.han.ruoji.service.service.DishService;
import com.han.ruoji.service.service.SetmealDishService;
import com.han.ruoji.service.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/setmeal")
/*
* 套餐管理
* */
public class SetmealController {

    @Autowired
    SetmealService setmealService;

    @Autowired
    SetmealDishService setmealDishService;

    @Autowired
    DishService dishService;



    /**
     * 新增套餐
     * */
    @PostMapping
    public R<String> saveMeal(@RequestBody SetmealDto setmealDto){


        setmealDto.setCode("2");

        boolean b = setmealService.saveWithDish(setmealDto);

        if(b){
            return R.success("添加成功");
        }

        log.info("套餐信息:{}",setmealDto);


        return R.error("添加失败");
    }




/*
* 分页查询
* */
    @GetMapping("/page")
    public R<Page<Setmeal>> getPage(int page,int pageSize,String name){

        log.info("套餐查询当前页:{}",page);
        log.info("套餐查询最大数:{}",pageSize);

        Page<Setmeal> pageInfor = new Page(page,pageSize);

        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);
        queryWrapper.orderByAsc(Setmeal::getPrice);

        setmealService.page(pageInfor, queryWrapper);

        return R.success(pageInfor);
    }


    /*
    * 修改套餐信息前，回显浏览器数据
    * */

    @GetMapping("/{id}")
    public R<SetmealDto> updaSetmeal(@PathVariable Long id){

        log.info("修改前的回显:{}",id);

        SetmealDto setmealDto = setmealService.getSetmealWithId(id);



        return R.success(setmealDto);


    }


    /*
    * 修改套餐信息
    * */

    @PutMapping()
    public R<String> updaSetmeal(@RequestBody SetmealDto setmealDto){

        log.info("要修改的套餐信息:{}",setmealDto.toString());
        boolean b = setmealService.updateSetmealWithId(setmealDto);
        if(b){
            return R.success("修改成功");
        }


        return R.error("修改失败");
    }




    @DeleteMapping()
    public R<String> deleteSetmeal(@RequestParam List<Long> ids){

        log.info("要删除的:{}",ids);

        boolean b = setmealService.deleteSetmealWithId(ids);

        if(b){
            return R.success("删除成功");
        }

        return R.error("删除失败");


    }

    //修改状态

    @PostMapping("/status/{status}")
    public R<String> uodateStatus(@PathVariable int status, @RequestParam List<Long> ids){
        log.info("修改状态为：{}",status);
        log.info("要修改的:{}",ids);

        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        List<Setmeal> list = setmealService.list(queryWrapper);
        List<Setmeal> newSetmealList = null;

        newSetmealList=list.stream().map((item)->{
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());

        boolean b = setmealService.updateBatchById(newSetmealList);

        if(b){
            return R.success("状态修改成功");
        }

        return R.error("状态修改失败");
    }

    /**
     * 用户页----条件查询套餐数据
     * **/

    @GetMapping("/list")
    public R<List<SetmealDto>> userGetCategory(Long categoryId,Integer status){

        log.info("套餐id::{}",categoryId);
        log.info("状态");

        LambdaQueryWrapper<Setmeal> query = new LambdaQueryWrapper<Setmeal>();

        query.eq(categoryId!=null, Setmeal::getCategoryId,categoryId);
        query.eq(status!=null,Setmeal::getCode,status);
        query.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(query);

        SetmealDto newSetmeal=new SetmealDto();


        List<SetmealDto> collect = list.stream().map((item) -> {
            BeanUtils.copyProperties(item, newSetmeal);
            Long id = item.getId();
            LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId, id);
            List<SetmealDish> dishService = setmealDishService.list(queryWrapper);
            newSetmeal.setSetmealDishes(dishService);
            return newSetmeal;
        }).collect(Collectors.toList());


        // SetmealDto setmealWithId = setmealService.getSetmealWithId(categoryId);

        return R.success(collect);
    }




}
