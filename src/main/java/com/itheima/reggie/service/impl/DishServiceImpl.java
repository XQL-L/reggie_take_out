package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishFlavorMapper;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品
     * @param dishDto
     */
    @Override
    @Transactional//涉及到多个表的控制，要加入事务管理
    public void saveWithFlavor(DishDto dishDto) {
//        保存菜品基本信息到菜品表dish
//        DishDto继承Dish，所以可以传入，不用管多余数据
        this.save(dishDto);

//        保存菜品口味表到dish_flavor
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishDto.getId());
        }
        log.info("加入id后==> {}",flavors.toString());
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据Id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
//        查基本信息
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
//      查口味数据
        LambdaQueryWrapper<DishFlavor> qw = new LambdaQueryWrapper<>();
        qw.eq(null!=id,DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(qw);

        dishDto.setFlavors(list);

        return dishDto;
    }

    /**
     * 修改菜品信息和对应口味信息
     * @param dishDto
     */
    @Transactional//涉及到多个表的控制，要加入事务管理
    @Override
    public void updateWithFlavor(DishDto dishDto) {
//        修改dish表基本信息
        this.updateById(dishDto);
//        修改对应的口味信息-------------->一个dishId对应多条数据，修改不方便，不如先删除再添加
//        清理当前菜品对应口味数据
        LambdaQueryWrapper<DishFlavor> qw = new LambdaQueryWrapper<>();
        qw.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(qw);

//        添加当前提交过来的口味数据,别放了加入菜品ID
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(flavors);


    }


}
