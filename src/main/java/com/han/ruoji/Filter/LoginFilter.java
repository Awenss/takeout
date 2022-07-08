package com.han.ruoji.Filter;

import com.alibaba.fastjson.JSON;
import com.han.ruoji.common.BaseContext;
import com.han.ruoji.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName ="logincheck",urlPatterns = "/*")
public class LoginFilter implements Filter {

    //路径匹配，支持通配符
    public static final AntPathMatcher PATTERN = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response= (HttpServletResponse) servletResponse;
        HttpServletRequest request= (HttpServletRequest) servletRequest;

        String requestURI=request.getRequestURI();

        log.info("请求为:::{}",request.getRequestURI());

        //1.不去的拦截路径
        String[] urls=new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/login",
                "/user/sendMsg"

        };

        //2.判断本次请求是否处理
        boolean check = check(urls, requestURI);

        //3.如果不需要处理，放行
        if(check){
            log.info("本次请求{}不需要处理"+requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //4.---------1   如果已登录,直接放行------员工登录
        //管理端-----拦截器

        if( request.getSession().getAttribute("employee")!=null){

            Long empId = (Long) request.getSession().getAttribute("employee");

            BaseContext.setCurrentId(empId);

            log.info("{}已登录----放行",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //4-------------2   用户登录-----------拦截器

        if( request.getSession().getAttribute("user")!=null){

            Long userId = (Long) request.getSession().getAttribute("user");

            BaseContext.setCurrentId(userId);

            log.info("{}已登录----放行",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

            //5.未登录返回客户端数据
            log.info("用户未登录拦截:::{}",requestURI);
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        //        filterChain.doFilter(request,response);
    }

    //内部方法
    //检查拦截到的路径是否匹配放行的路径
    public boolean check(String[] urls,String requestURL){
        for (String url : urls){
            boolean match= PATTERN.match(url,requestURL);
            if(match){
                return true;
            }
        }
        return false;
    }
}
