package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 添加菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");


    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        qw.like(null!=name,Dish::getName,name);
        qw.orderByDesc(Dish::getUpdateTime);
//        pageInfo里的records缺少categoryName这一属性
        dishService.page(pageInfo,qw);
//        dao对象中有categoryName属性
        Page<DishDto> dishDtoPage = new Page<>();
//        将pageInfo中的属性，(浅复制)复制到dishDaoPage中，不包括records属性，其要进行处理
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map(dish -> {
            Long categoryId = dish.getCategoryId();
            Category category = categoryService.getById(categoryId);
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);
            if(null != category){
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;

        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    /**
     * 根据Id查询菜品的基本信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto byIdWithFlavor = dishService.getByIdWithFlavor(id);
        return R.success(byIdWithFlavor);
    }

    /**
     * 修改菜品信息和对应口味信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

//    http://localhost/dish/list?categoryId=1397844391040167938

    /**
     * 根据条件查询菜品数据
     * @param categoryId
     * @param name
     * @return
     */
/*    @GetMapping("/list")
    public R<List<Dish>> listByCategoryId(Long categoryId,String name){
        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        qw.eq(null!=categoryId,Dish::getCategoryId,categoryId);
        qw.like(name!=null,Dish::getName,name);
        qw.eq(Dish::getStatus,1);
        List<Dish> list = dishService.list(qw);
        return R.success(list);
    }*/
//  在基本菜品数据基础上加上口味数据，封装为DishDto
    @GetMapping("/list")
    public R<List<DishDto>> listByCategoryId(Long categoryId,String name){
        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        qw.eq(null!=categoryId,Dish::getCategoryId,categoryId);
        qw.like(name!=null,Dish::getName,name);
        qw.eq(Dish::getStatus,1);
        List<Dish> list = dishService.list(qw);
        List<DishDto> collect = list.stream().map((item) -> {
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
            lqw.eq(DishFlavor::getDishId, id);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lqw);
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(collect);
    }

}
