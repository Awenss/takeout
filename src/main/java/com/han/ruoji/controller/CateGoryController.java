package com.han.ruoji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.ruoji.common.R;
import com.han.ruoji.entity.Category;
import com.han.ruoji.service.service.impl.CateGoryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CateGoryController {

    @Autowired
    CateGoryServiceImpl service;


    /***
     * 添加分类
     *
     * @param request
     * @param category
     * @return
     */
//
    //添加菜品
    @PostMapping
    public R<String> addCategory(HttpServletRequest request, @RequestBody Category category){
        log.info("新加的分类{}",category.toString());

        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setCreateUser((Long) request.getSession().getAttribute("employee"));
        category.setUpdateUser((Long) request.getSession().getAttribute("employee"));

        service.save(category);
        return R.success("新增分类成功");
    }



    /*
    * //分类查询
    *
    * */
    @GetMapping("/page")
    public R<Page> getCategory(int page,int pageSize){

        log.info("菜品当前分页{}",page);
        log.info("菜品分页大小{}",pageSize);

        //分页构造器
        Page<Category> pageInfor = new Page(page, pageSize);

        //条件查询
        LambdaQueryWrapper<Category> query = new LambdaQueryWrapper();

        query.orderByDesc(Category::getType);

        Page page1 = service.page(pageInfor, query);

        log.info("结果{}",page1);

        return R.success(page1);
    }

    /**
     *
     *
     * 分类修改
     */

    @PutMapping()
    public R<String> updateCategory(HttpServletRequest request, @RequestBody Category category){

        category.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        category.setUpdateTime(LocalDateTime.now());
        boolean b = service.updateById(category);

        if(b){
            return R.success("修改成功");
        }

        return R.error("修改失败");
    }

    /**
     *
     * 删除分类
     *
     */
    @DeleteMapping()
    public R<String> deleteCategory(HttpServletRequest request, Long ids){
        log.info("删除的分类ID：{}",ids);

       // boolean b = service.removeById(ids);
        boolean b = service.remove(ids);
        if(b){
            return R.success("分类删除成功");
        }

            return R.error("分类删除失败");
    }


    /*
    * 返回添加菜品分类数据给新增菜品页面
    * */

    @GetMapping("/list")
    public R<List<Category>> categoryList(Category category) {

        log.info("分类{}",category.getType());

        LambdaQueryWrapper<Category> query = new LambdaQueryWrapper<Category>();

        //条件
        query.eq(category.getType()!=null,Category::getType,category.getType());

        //排序
        query.orderByAsc(Category::getType).orderByDesc(Category::getUpdateTime);
        List<Category> list = service.list(query);
        return R.success(list);
    }

//    @GetMapping("/user/list")
//    public R<List<Category>> categoryList() {
//
//        LambdaQueryWrapper<Category> query = new LambdaQueryWrapper<Category>();
//        query.orderByAsc(Category::getType).orderByDesc(Category::getSort);
//        List<Category> list = service.list(query);
//        return R.success(list);
//    }


}
