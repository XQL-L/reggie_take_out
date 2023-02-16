package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee){

//        将提交的密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        
//        根据username查询数据库
        String username = employee.getUsername();
        QueryWrapper<Employee> qw = new QueryWrapper<>();
        qw.eq("username",username);
        Employee emp = employeeService.getOne(qw);

//        没有查询到用户
        if(null == emp){
            return R.error("没有查询到该用户");
        }

//        比对密码
//        一个为空而一个不为空
        if(  ((null == emp.getPassword()) && (null != password))
                ||  ((null != emp.getPassword()) && (null == password))  ){
            return R.error("密码错误");
        }

//        比对密码
//        真实密码不为空, 若真实密码为空则输入的密码一定也为空
        if( null != emp.getPassword() ){
//            密码不同
            if(!emp.getPassword().equals(password)){
                return R.error("密码错误");
            }
        }
//        已禁用
        if(!emp.getStatus().equals(1)){
            return R.error("账号已禁用");
        }

//        ----------------------------登陆成功---------------------------
//        设置session
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工登出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
//        清除Session
        request.getSession().removeAttribute("employee");
//        返回结果
        return R.success("退出成功");

    }

    @PostMapping
    public R<String> addEmployee(HttpServletRequest request, @RequestBody Employee emp){
//        username具有唯一约束
//        status 在mysql数据库中有默认值1
        log.info(emp.toString());

//        初始密码为123456，并进行mb5加密处理
        emp.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

/*        emp.setCreateTime(LocalDateTime.now()); 进行了自动填充
        emp.setUpdateTime(LocalDateTime.now());
//        获取当前创建人的ID
        emp.setCreateUser((long)request.getSession().getAttribute("employee"));
        emp.setUpdateUser((long)request.getSession().getAttribute("employee"));*/

        employeeService.save(emp);
        return R.success("新增员工成功");

    }

//    http://localhost/employee/page?page=1&pageSize=10
// 条件分页查询
// IPage<T> page(IPage<T> page, Wrapper<T> queryWrapper);
    @GetMapping("/page")
    public R<IPage<Employee>> getPage(int page, int pageSize, String name){//get在url中传参，直接写到形参中
        log.info("page = {}, pageSize = {}, name = {}",page,pageSize,name);
        IPage<Employee> ipage = new Page<>(page,pageSize);//翻页对象
        QueryWrapper<Employee> qw = new QueryWrapper<>();//查询条件对象
        qw.like(Strings.isNotEmpty(name),"name",name);
        employeeService.page(ipage,qw);
//        System.out.println(ipage.getRecords());
//        删除本页最后一个数据会出现bug，解决                      //(╬▔皿▔)╯ WCNM这个案例好像不需要，他没有删除操作，只有禁用
        if(page > ipage.getPages()){
            IPage<Employee> ipage2 = new Page<>(ipage.getPages(),pageSize);//翻页对象
            QueryWrapper<Employee> qw2 = new QueryWrapper<>();//查询条件对象
            qw2.like(Strings.isNotEmpty(name),"name",name);
            employeeService.page(ipage2,qw2);
            return R.success(ipage2);
        }else {
            return R.success(ipage);
        }
    }

//1625107559684841473
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee emp){
//        String型的id自动转为long数据？？？？？？
        log.info(emp.toString());

        boolean flag = employeeService.updateById(emp);

        return flag ? R.success("状态修改成功") : R.error("状态修改失败");

    }

    /**
     *根据id返回对象
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable long id){
        Employee emp = employeeService.getById(id);
        if(null != emp){
            return R.success(emp);
        }
        return R.error("没有找到该用户");
    }



}
