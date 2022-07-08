package com.han.ruoji.common;


//全局异常处理器


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})//添加要出里异常的注解
@ResponseBody
@Slf4j
public class GlobalException {
    /*
    * 加入异常处理方法
    * */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)//添加要处理的异常
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException e){

        log.error(e.getMessage());

        if(e.getMessage().contains("Duplicate entry")){
            String[] split = e.getMessage().split(" ");
            String msg = split[2]+"已存在";
           return R.error(msg);
        }

        return R.error("未知错误");
    }

/**
 * 处理自定义的业务异常
 * */
    @ExceptionHandler(CoustmentExcepiton.class)//添加要处理的异常
    public R<String> exceptionHandler(CoustmentExcepiton e){

        log.error(e.getMessage());

        return R.error(e.getMessage());
    }


}
