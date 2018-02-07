package com.mall.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Charles Date:2018/2/7
 * 处理全局异常类
 */
@Slf4j
@Component
public class ExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        log.error("{} Exception", request.getRequestURI(), ex);
        ModelAndView modelAndView = new ModelAndView(new MappingJacksonJsonView());

        // 当使用jackson2.x 时候，使用MappingJackson2JsonView，此处使用的是 1.9
        modelAndView.addObject("status", ResponseCode.ERROR.getCode());
        modelAndView.addObject("msg", "接口异常，详情请查看服务端日志信息");
        modelAndView.addObject("data", ex.toString());
        return modelAndView;
    }
}
