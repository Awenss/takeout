package com.han.ruoji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.ruoji.common.R;
import com.han.ruoji.entity.Employee;
import com.han.ruoji.service.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService service;
    /**
     * 登录接口
     * @param request
     * @param employee
     * @return
     */
//
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        /*
        * 加密
        *
        * */

        String password = employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> querWapper = new LambdaQueryWrapper<>();

        querWapper.eq(Employee::getUsername,employee.getUsername());

        Employee emp = service.getOne(querWapper);

        if(emp==null){
            return R.error("登录失败");
        }
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        if(emp.getStatus()==0){
            return R.error("账号已禁用");
        }

        request.getSession().setAttribute("employee",emp.getId());

        return R.success(emp);

    }


    @PostMapping("/logout")
    public R<String> loginout(HttpServletRequest request){

        request.getSession().removeAttribute("employee");
        log.info("退出登录");

    return R.success("退出登录");
    }


    /*
    * 新增员工
    * */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){


        log.info("新增员工信息{}",employee.toString());

        //为员工设置初始密码

        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());


        //获取当前登录用户id
        Long empId= (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        service.save(employee);

        return R.success("新增员工成功");

    }

    /**
     *
     * 分页查询
     */

    @GetMapping("/page")
    public R<Page> employeeList(int page,int pageSize,String name){
        log.info("当前页{}",page);
        log.info("页面大小{}",pageSize);
        log.info("Name{}",name);
        //构造分页  构造器
        Page pageInfor = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> query = new LambdaQueryWrapper();
        //添加过滤条件
        query.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        query.orderByDesc(Employee::getUpdateTime);
        service.page(pageInfor,query);
        //执行
        return R.success(pageInfor);
    }


    /*
    * 禁用账号
    *
    * */
    @PutMapping
    public R<String> updaStatus(HttpServletRequest request, @RequestBody Employee employee){

        log.info("修改的ID{}",employee.getId());
        log.info("修改的状态{}",employee.getStatus());

        /**
         *已通过 Mybatis元数据自动填充
         */
//
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser((Long)request.getSession().getAttribute("employee"));

        service.updateById(employee);
        return R.success("状态修改成功");
    }


    //根据id查用户回显
    @GetMapping("/{id}")
    public R<Employee> getUserById(@PathVariable Long id){
        log.info("修改的id{}",id);
        LambdaQueryWrapper<Employee> query = new LambdaQueryWrapper<>();
        query.eq(Employee::getId,id);
        Employee emp = service.getOne(query);

        if(emp!=null){
            return R.success(emp);
        }
        return R.error("没有查询到员工信息");


    }







}
