package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    /**
     * 发送手机验证码--http://localhost/user/sendMsg
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
//        获取手机号
        String phone = user.getPhone();
        if(null != phone){
//        生成随机的四位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code==={}",code);
//        调用阿里云提供的短信服务API完成发送短信
//            SMSUtils.sendMessage("阿里云短信测试","SMS_154950909",phone,code);
//            SMSUtils.sendMessage("瑞吉外卖","SMS_270335108",phone,code);

//        需要将生成的验证码保存到Session
            session.setAttribute(phone,code);
            return R.success("手机验证码发送成功");
        }
        return R.error("验证码发送失败");
    }

    /**
     * 用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<String> login(@RequestBody Map map, HttpSession session){//Map或者DTO都可以接受前端JSON数据
        log.info(map.toString());
//        获取手机号
        String phone = (String)map.get("phone");
//        获取验证码
        String code = map.get("code").toString();
//        从Session中获取保存到验证码
        String codeInSession = (String)session.getAttribute(phone);
//        进行验证码比对
        if(null == code ||  !code.equals(codeInSession)){
            return R.error("验证码错误");
        }
//        如果比对成功，说明登录成功

//        判断当前手机号对应的用户是否为新用户，如果是新用户则自动完成注册
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(User::getPhone,phone);
        User user = userService.getOne(qw);
        if(null == user) {
            user = new User();
            user.setPhone(phone);
            user.setStatus(1);
            userService.save(user);//走完这一步user，MyBatisPlus就给user填充了ID
        }
        log.info(user.toString());
        session.setAttribute("user",user.getId());

        return R.success("登录成功");
    }


}
