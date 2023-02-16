package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("新增分类：{}",category.toString());
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    @GetMapping("/page")
    public R<IPage<Category>> page(int page,int pageSize){//    http://localhost/category/page?page=1&pageSize=10
        log.info("page = {}, pageSize = {}",page,pageSize);
        IPage<Category> ipage = new Page<>(page,pageSize);//翻页对象
        QueryWrapper<Category> qw = new QueryWrapper<>();//查询条件对象
        qw.orderByAsc("sort");
        categoryService.page(ipage,qw);
//        System.out.println(ipage.getRecords());
//        删除本页最后一个数据会出现bug，解决                      //(╬▔皿▔)╯ WCNM这个案例好像不需要，他没有删除操作，只有禁用
        if(page > ipage.getPages()){
            IPage<Category> ipage2 = new Page<>(ipage.getPages(),pageSize);//翻页对象
            QueryWrapper<Category> qw2 = new QueryWrapper<>();//查询条件对象
            qw2.orderByAsc("sort");
            categoryService.page(ipage2,qw2);
            return R.success(ipage2);
        }else {
            return R.success(ipage);
        }

    }

    /**
     * 删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") Long id){
        log.info("删除分类，ID为===>{}",id.toString());
        categoryService.myRemoveById(id);
        return R.success("分类删除成功");

    }

    /**
     * 修改分类
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改信息为{} ",category);
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }

//    http://localhost/category/list?type=1

    /**
     * 菜品种类下拉框展示
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){//使用实体类通用性更好

        LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<>();
        qw.eq(null != category.getType(),Category::getType,category.getType());
        qw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(qw);

        return R.success(list);


    }

}
