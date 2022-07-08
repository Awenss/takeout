package com.han.ruoji.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.han.ruoji.entity.Employee;
import com.han.ruoji.mapper.EmployeeMapper;
import com.han.ruoji.service.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
