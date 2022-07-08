package com.han.ruoji.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.han.ruoji.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {

    /*
    * 自定义SQL语句
    * */
//    @Update("update dish set status=#{status} where id=#{id}")
//    public boolean updateStatusById(@Param("status") int status, @Param("id")Long id);

}
