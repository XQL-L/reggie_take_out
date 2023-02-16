package com.itheima.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否登录
 */
@Slf4j
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request= (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
//        获取URI
        String uri = request.getRequestURI();
//        设置放行的uri
        String[] uris = {
          "/employee/login","/employee/logout","/backend/**","/front/**","/user/sendMsg","/user/login"
        };
//      判断是否放行
        boolean check = check(uris, uri);
//      放行
        if(check){
            filterChain.doFilter(request,response);
//            这里return 指的是不执行放行后的逻辑，直接return退出
            return ;
        }
//       1） 判断员工是否登录
        if(request.getSession().getAttribute("employee") != null){
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
//            log.info("过滤器中获取的ID====> {}",BaseContext.getCurrentId().toString());
            filterChain.doFilter(request,response);
            return ;
        }
 //       2） 判断员工是否登录
        if(request.getSession().getAttribute("user") != null){
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
//            log.info("过滤器中获取的ID====> {}",BaseContext.getCurrentId().toString());
            filterChain.doFilter(request,response);
            return ;
        }
        log.info("拦截到请求===>{}",uri);

//        未登录，拦截
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;





    }

    private boolean check(String[] uris, String requestUri){
        for (String uri : uris) {
            boolean match = PATH_MATCHER.match(uri, requestUri);
            if(match){
                return true;
            }
        }
        return false;

    }
}
