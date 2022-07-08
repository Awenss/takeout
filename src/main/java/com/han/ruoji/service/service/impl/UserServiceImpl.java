package com.han.ruoji.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.han.ruoji.entity.User;
import com.han.ruoji.mapper.UserMapper;
import com.han.ruoji.service.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
