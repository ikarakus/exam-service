package com.exam.config;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserHandlerInterceptor extends HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        if(request.getMethod().matches(RequestMethod.OPTIONS.name())) {
            return true;
        }
  //      request.setAttribute("username", "4b16327f-a26c-4752-91bc-2908e6bd409d"); // temporary user Id
        return true;
    }


}
