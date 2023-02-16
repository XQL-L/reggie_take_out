package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;


    /**
     * 根据id进行删除，删除前先判断有没有关联数据
     * @param id
     */
    @Override
    public void myRemoveById(Long id) {
//        查询当前分类是否关联了菜品，若关联了，则抛出一个业务异常
/*        QueryWrapper<Dish> qw = new QueryWrapper<>();
        qw.eq("category_id",id);                        //QueryWrapper(╬▔皿▔)╯ WCNM不能进行驼峰命名法的映射不知道为什么
        int count1 = dishService.count(qw);*/

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);

        int count1 = dishService.count(dishLambdaQueryWrapper);
        if(count1 > 0){
            throw new CustomException("当前分类关联了菜品，不能删除");
        }

//        查询当前分类是否关联了套餐，若关联了，则抛出一个业务异常
        LambdaQueryWrapper<Setmeal> dishLambdaQueryWrapper2=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper2.eq(Setmeal::getCategoryId,id);

        int count2 = setmealService.count(dishLambdaQueryWrapper2);
        if(count2 >0){
            throw new CustomException("当前分类关联了套餐，不能删除");
        }

//        正常删除
        this.removeById(id);

    }
}
