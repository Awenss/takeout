package com.han.ruoji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.han.ruoji.entity.Category;

public interface CategoryService extends IService<Category> {
    //自定义删除方法

    public boolean remove(Long id);
    
}
