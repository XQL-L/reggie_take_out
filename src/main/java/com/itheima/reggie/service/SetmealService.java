package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {
//    新增套餐，同时保存套餐和菜品的关联关系
    void saveWithDish(SetmealDto setmealDto);
//      删除套餐，同时删除套餐和菜品的关联关系
    void removeWithDish(Long[] ids);
}
