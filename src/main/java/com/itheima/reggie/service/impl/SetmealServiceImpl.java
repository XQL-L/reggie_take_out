package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
//        保存基本套餐信息
        this.save(setmealDto);
        log.info("保存基本套餐信息后==》{}",setmealDto);
//        保存套餐与菜品的关联关系
        List<SetmealDish> collect = setmealDto.getSetmealDishes().stream().map(new Function<SetmealDish, SetmealDish>() {
            @Override
            public SetmealDish apply(SetmealDish setmealDish) {
                setmealDish.setSetmealId(setmealDto.getId());
                return setmealDish;
            }
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(collect);


    }

    /**
     * 删除套餐，同时删除套餐和菜品的关联关系
     * @param ids
     */
    @Override
    public void removeWithDish(Long[] ids) {
        List<Long> idList = new ArrayList<>();
        Collections.addAll(idList,ids);//数组转为Collection集合
//        查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.in(Setmeal::getId,idList);
        qw.eq(Setmeal::getStatus,1);
        int count = this.count(qw);
        if(count > 0){
//            若不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }
//        如果可以删除，先删除套餐表中的数据
        this.removeByIds(idList);
//        还要删除setmeal_dish中的关联关系
/*        for (Long id : idList) {
            LambdaQueryWrapper<SetmealDish> qw2 = new LambdaQueryWrapper<>();
            qw2.eq(SetmealDish::getSetmealId,id);
            setmealDishService.remove(qw2);
        }*/
        LambdaQueryWrapper<SetmealDish> qw2 = new LambdaQueryWrapper<>();
        qw2.in(SetmealDish::getSetmealId,idList);
        setmealDishService.remove(qw2);

    }
}
