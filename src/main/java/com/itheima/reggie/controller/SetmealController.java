package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setmealDto
     * @return
     */
//    http://localhost/setmeal
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("新增套餐信息为===>{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }


    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.like(null!= name,Setmeal::getName,name);
        qw.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,qw);
//        但是SetMeal没有分类名称这一项,转为SetmealDto传值
        Page<SetmealDto> dtoPage = new Page<>();
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");

        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> collect = records.stream().map((item) -> {
            Long categoryId = item.getCategoryId();
            Category byId = categoryService.getById(categoryId);
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            if (null != byId) {
                setmealDto.setCategoryName(byId.getName());
                return setmealDto;
            }
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(collect);


        return R.success(dtoPage);
    }
//    http://localhost/setmeal?ids=1625757784879427585,1625752871470370817
    @DeleteMapping
    public R<String> delete(Long... ids){//或者 @RequstParam List<Long> ids
        log.info("修改的ids为==》{}",ids);
        setmealService.removeWithDish(ids);
        return R.success("删除菜品成功");
    }

//    http://localhost/setmeal/status/0?ids=1625757784879427585,1625752871470370817   ----POST
//    TODO:判断点起售的请求，本身是不是已经是起售的状态
    @PostMapping("/status/{toStatus}")//REST风格无法完成String转化到实体类，@PathVariable Setmeal setmeal不可以
    public R<String> update(@PathVariable Integer toStatus,@RequestParam List<Long> ids){
        log.info(toStatus.toString());
        log.info(ids.toString());

        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.in(Setmeal::getId,ids);
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(toStatus);
        setmealService.update(setmeal,qw);
        return R.success("状态修改成功");
    }

    /**
     * 根据条件查询套餐
     * @param setmeal
     * @return
     */
//    http://localhost/setmeal/list?categoryId=1413342269393674242&status=1
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.eq(null!=setmeal.getCategoryId(),Setmeal::getCategoryId,setmeal.getCategoryId());
        qw.eq(null!=setmeal.getStatus(),Setmeal::getStatus,setmeal.getStatus());
        qw.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(qw);
        return R.success(list);
    }





}
