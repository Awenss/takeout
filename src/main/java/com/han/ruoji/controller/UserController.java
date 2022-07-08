package com.han.ruoji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.han.ruoji.common.R;
import com.han.ruoji.entity.User;
import com.han.ruoji.service.service.UserService;
import com.han.ruoji.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {



    @Autowired
    UserService userService;

    //登录
    @PostMapping("/login")
    public R<User> userlogin(@RequestBody Map user,HttpSession session){

        log.info("手机号:{}",user.get("phone"));
        log.info("发送的验证码:{}",user.get("code"));

        String phone = user.get("phone").toString();
        String code = user.get("code").toString();

        String CodeInSession = (String) session.getAttribute(phone);//登录的
        log.info("生成的验证码:{}",code);

        if(CodeInSession!=null&&CodeInSession.equals(code)){
            //如果是新用户自动注册

            LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User one = userService.getOne(queryWrapper);
            if(one==null){
                User newUser = new User();
                newUser.setPhone(phone);
                newUser.setStatus(1)    ;
                userService.save(newUser);


            }
            LambdaQueryWrapper<User> loginqueryWrapper=new LambdaQueryWrapper<>();
            loginqueryWrapper.eq(User::getPhone,phone);
            User loginUser = userService.getOne(queryWrapper);

//            User Loginuser=new User();
//            Loginuser.setPhone(phone);
//            LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
//            lambdaQueryWrapper.eq(User::getPhone,phone);
//            User LoginUser = userService.getOne(lambdaQueryWrapper);
//

            session.setAttribute("user",loginUser.getId());
            return R.success(loginUser);
       //    return   R.success(one);
        }

        return R.error("登录失败");
    }


    //获取验证码
    @PostMapping("/sendMsg")
    public R<String> sendMessage(HttpSession session, @RequestBody String phoneNumber){

        if(StringUtils.isNotEmpty(phoneNumber)){
            String integer = ValidateCodeUtils.generateValidateCode(6).toString();
            log.info("手机号:{}",phoneNumber);
            log.info("生成的验证码:{}",integer);

            session.setAttribute(phoneNumber,integer);
            log.info("存放的验证码;{}",session.getAttribute(phoneNumber));

            return R.success("验证码发送成功");
        }

        return R.error("验证码发送失败");


    }


    @PostMapping("/loginout")
    public R<String> logout(HttpSession session){

        log.info("登录登录");
        session.removeAttribute("user");

        return R.success("退出登录");

    }




}
