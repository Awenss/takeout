package com.han.ruoji.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.han.ruoji.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressMapper extends BaseMapper<AddressBook> {
}
