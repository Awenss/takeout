package com.han.ruoji.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.han.ruoji.entity.AddressBook;
import com.han.ruoji.mapper.AddressMapper;
import com.han.ruoji.service.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, AddressBook> implements AddressBookService {
}
