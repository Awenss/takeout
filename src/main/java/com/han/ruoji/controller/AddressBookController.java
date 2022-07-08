package com.han.ruoji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.han.ruoji.common.BaseContext;
import com.han.ruoji.common.R;
import com.han.ruoji.entity.AddressBook;
import com.han.ruoji.entity.User;
import com.han.ruoji.service.service.AddressBookService;
import com.han.ruoji.service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    AddressBookService addressBookService;

    @Autowired
    UserService userService;

    /*
    *
    * 添加收货地址
    *
    * */
    @PostMapping
    public R<String> saveAddress(@RequestBody AddressBook addressBook){
//        LambdaQueryWrapper<User> userLambdaQueryWrapper=new LambdaQueryWrapper<>();
//
//        userLambdaQueryWrapper.eq(User::getPhone,addressBook.getPhone());
//        //获取与用户相同号码的ID
//        User one = userService.getOne(userLambdaQueryWrapper);
        //给地址簿对象uid赋值
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("添加的地址::{}",addressBook.toString());
        boolean save = addressBookService.save(addressBook);

        if(save){
           return R.success("添加成功");
        }

        return R.error("添加失败");
    }


    /*
    * 获取收货地址列表
    * */
    @GetMapping("/list")
    public R<List<AddressBook>> getAddress(){
        LambdaQueryWrapper<AddressBook> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());

        List<AddressBook> list = addressBookService.list(queryWrapper);


        return R.success(list);
    }


    /*
    * 修改回显
    * */

    @GetMapping("/{id}")
    public R<AddressBook> getAddressById(@PathVariable Long id){
        log.info("要修改的id::{}",id);
        LambdaQueryWrapper<AddressBook> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getId,id);
        AddressBook one = addressBookService.getOne(queryWrapper);

        return R.success(one);
    }


    /*
    * 修改地址
    * */
    @PutMapping
    public R<String> updateAddress(@RequestBody AddressBook addressBook){
//        LambdaQueryWrapper<User> userLambdaQueryWrapper=new LambdaQueryWrapper<>();
//
//        userLambdaQueryWrapper.eq(User::getPhone,addressBook.getPhone());
//        //获取与用户相同号码的ID
//        User one = userService.getOne(userLambdaQueryWrapper);
        //给地址簿对象uid赋值
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("修改的地址::{}",addressBook.toString());
        boolean save = addressBookService.updateById(addressBook);

        if(save){
            return R.success("修改成功");
        }

        return R.error("修改失败");
    }

    /**
     * 删除地址
     * */

    @DeleteMapping
    public R<String> deleteAddress(@RequestParam List<Long> ids){
        LambdaQueryWrapper<AddressBook> queryWrapper=new LambdaQueryWrapper<AddressBook>();
        queryWrapper.in(AddressBook::getId,ids);
        log.info("要删除的地址::{}",ids);
        boolean remove = addressBookService.remove(queryWrapper);

        if(remove){
           return R.success("删除成功");
        }

        return R.error("删除失败");
    }


    /*
    * 设置默认地址
    * */

    @PutMapping("/default")
    public R<String> setDefaulAddress(@RequestBody AddressBook addressBook){
        log.info("修改成默认地址的id:{}",addressBook.toString());
        LambdaUpdateWrapper<AddressBook> updateWrapper=new LambdaUpdateWrapper<>();

        //将当前用户下所有id默认地址设置为0
        updateWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        updateWrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(updateWrapper);
        //SQL: update address_book set is_default = 0 where user_id=0

        addressBook.setIsDefault(1);
        boolean b = addressBookService.updateById(addressBook);
        if(b){
            return R.success("设置成功");
        }

        return R.error("设置失败");
    }

    @GetMapping("/default")
    public R<AddressBook> getDefaultAddressById(){

        log.info("根据i:::{}d用户获取默认地址",BaseContext.getCurrentId());

        LambdaQueryWrapper<AddressBook> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook one = addressBookService.getOne(queryWrapper);


        return R.success(one);
    }


}
