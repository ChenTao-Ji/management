package com.example.management.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SessionFilter implements Filter {

    protected Logger log = LoggerFactory.getLogger(SessionFilter.class);

    // 不登陆也可以访问的资源
    private static Set<String> GreenUrlSet = new HashSet<String>();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String uri = request.getRequestURI();
        servletResponse.setCharacterEncoding("UTF-8");//设置响应编码格式
        servletResponse.setContentType("text/html;charset=UTF-8");//设置响应编码格式
        if (uri.endsWith(".js")
                || uri.endsWith(".css")
                || uri.endsWith(".jpg")
                || uri.endsWith(".gif")
                || uri.endsWith(".png")
                || uri.endsWith(".ico")) {
            log.debug("security filter, pass, " + request.getRequestURI());
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        System.out.println("request uri is : " + uri);
        //不处理指定的action, jsp
        if (GreenUrlSet.contains(uri) || uri.contains("/verified/")) {
            log.debug("security filter, pass, " + request.getRequestURI());
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        Integer id = (Integer) request.getSession().getAttribute(WebConfiguration.LOGIN_KEY);
        if (StringUtils.isEmpty(id)) {
            String html = "<script type=\"text/javascript\">window.location.href=\"/toLogin\"</script>";
            servletResponse.getWriter().write(html);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        GreenUrlSet.add("/toRegister");
        GreenUrlSet.add("/toLogin");
        GreenUrlSet.add("/login");
        GreenUrlSet.add("/loginOut");
        GreenUrlSet.add("/register");
        GreenUrlSet.add("/verified");
    }

    @Override
    public void destroy() {

    }
}
