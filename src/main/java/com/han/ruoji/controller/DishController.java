package com.han.ruoji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.ruoji.common.R;
import com.han.ruoji.dto.DishDto;
import com.han.ruoji.entity.Category;
import com.han.ruoji.entity.Dish;
import com.han.ruoji.entity.DishFlavor;
import com.han.ruoji.service.service.CategoryService;
import com.han.ruoji.service.service.DishFlavorService;
import com.han.ruoji.service.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/*
*
* 菜品管理
* */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    //菜品管理
    @Autowired
    DishService dishService;


    //菜品口味
    @Autowired
    DishFlavorService dishFlavorService;


    //分类
    @Autowired
    CategoryService categoryService;

/*
* 新增菜品
* */
    @PostMapping
    public R<String> addDish(@RequestBody DishDto dto){

        dishService.saveWithFlavor(dto);

        log.info(dto.toString());

        return R.success("添加菜品成功");
    }

    /***
     * 分页查询菜品
     *
     */
//
    @GetMapping("/page")
    public R<Page> getDish(int page,int pageSize,String name){

        log.info("当前页{}",page);
        log.info("页面大小{}",pageSize);
        log.info("Name{}",name);


        //分页构造器
        Page<Dish> pageInfor = new Page(page,pageSize);
        Page<DishDto> dishDtoPage=new Page<>();


        //条件查询构造器
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper();

        //过滤
        queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        //降序
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行查询
        dishService.page(pageInfor,queryWrapper);

        //对象拷贝

        BeanUtils.copyProperties(pageInfor,dishDtoPage,"records");

        List<Dish> records = pageInfor.getRecords();//拿到元Page中的数据

        //通过stream流赋值
        List<DishDto> dishDtoList= records.stream().map((item)->{
            DishDto dishDto=new DishDto();
            //将Dish（item）对象拷贝到新page对象DishDto中
            BeanUtils.copyProperties(item,dishDto);
            //拿到Dish
            Long categoryId = item.getCategoryId();//分类id

            //通过id查询分类表拿到分类对象
            Category category = categoryService.getById(categoryId);
            //拿到分类名称
            String categoryName = category.getName();
            //将新对象中的分类名赋值
            dishDto.setCategoryName(categoryName);

            return dishDto;
        }).collect(Collectors.toList());//转换为集合

        dishDtoPage.setRecords(dishDtoList);//把处理后的元数据赋值给新的数据


        return R.success(dishDtoPage);
    }


    /**
     *
     * 修改根据id查询菜品和口味数据
     *
     *
     */

    @GetMapping("/{id}")
    public R<DishDto> getDishById(@PathVariable Long id){

        DishDto withById = dishService.getWithById(id);

        log.info("修改的是菜品id:{}",id);

        return R.success(withById);
    }


    /*
    *
    * 修改菜品信息
    * */

    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dishDto){
        boolean b = dishService.updateWithFlavor(dishDto);

        log.info(dishDto.toString());
        if(b){
            return R.success("修改成功");
        }

        return R.error("修改失败");

    }

    //删除菜品

    @DeleteMapping()
    public R<String> deleteDish(@RequestParam List<Long> ids){

        log.info("删除菜品的id:{}",ids);

        boolean b = dishService.removeByIdWithFlavor(ids);
        if(b){
            return R.success("删除成功");
        }
        return R.error("删除失败");
    }


    /**
     *
     *
     * 修改菜品状态---批量修改
     */
    @PostMapping("/status/{status}")
     public R<String> updateDishStatus(@PathVariable int status,@RequestParam List<Long> ids){

        log.info("修改的id:{}",ids);
        log.info("修改的状态：{}",status);

        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        List<Dish> list = dishService.list(queryWrapper);
        List<Dish> newDishList = null;
        newDishList=list.stream().map((item)->{
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());

        boolean b = dishService.updateBatchById(newDishList);
        if(b){
            return R.success("状态修改成功");
        }
        return R.error("状态修改失败");
    }


    /**
     *
     * 套餐管理页面，获取菜品类别
     * **/
//    @GetMapping("/list")
//    public R<List<Dish>> getDishByIdList(Long categoryId){
//        log.info("获取菜品分类的id:{}",categoryId);
//
//        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
//        queryWrapper.eq(Dish::getCategoryId,categoryId);
//        queryWrapper.orderByAsc(Dish::getPrice);
//        queryWrapper.eq(Dish::getStatus,1);
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return R.success(list);
//    }


    /**
     *
     * 套餐管理页面，获取菜品类别
     * **/
    @GetMapping("/list")
    public R<List<DishDto>> getDishByIdList(Long categoryId){
        log.info("获取菜品分类的id:{}",categoryId);

        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,categoryId);
        queryWrapper.orderByAsc(Dish::getPrice);
        queryWrapper.eq(Dish::getStatus,1);
        List<Dish> list = dishService.list(queryWrapper);


        List<DishDto> dishDtoList= list.stream().map((item)->{
            //拿到dishId
            Long DishId = item.getId();
            //创建一个dishDto
            DishDto dishDto=new DishDto();
            //将Dish（item）对象拷贝到新page对象DishDto中
            BeanUtils.copyProperties(item,dishDto);
            //查询dishid对应的口味表中的集合数据
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper=new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,DishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());//转换为集合






        return R.success(dishDtoList);
    }

}
