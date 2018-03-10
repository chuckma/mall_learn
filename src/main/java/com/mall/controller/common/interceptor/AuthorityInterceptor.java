package com.mall.controller.common.interceptor;

import com.google.common.collect.Maps;
import com.mall.common.Const;
import com.mall.common.ServerResponse;
import com.mall.pojo.User;
import com.mall.util.CookieUtil;
import com.mall.util.JsonUtil;
import com.mall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Charles Date:2018/2/19
 */

@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("preHandle");
        // 请求中Controller 中的方法名
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        // 解析HandlerMethod
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();

        // 解析参数 ，具体的参数key以及value 是什么
        StringBuffer requestParamBuffer = new StringBuffer();
        Map paramMap = request.getParameterMap();

        Iterator it = paramMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String mapKey = (String)entry.getKey();

            String mapValue = StringUtils.EMPTY;
            // request 这个参数的map ，里边value  返回的是一个String[]
            Object obj = entry.getValue();
            if (obj instanceof String[]) {
                String[] strs = (String[]) obj;
                mapValue = Arrays.toString(strs);
            }
            requestParamBuffer.append(mapKey).append("=").append(mapValue);
        }

        if (StringUtils.equals(className,"UserManageController") && StringUtils.equals(methodName, "login")){
            log.info("权限拦截器拦截到请求，className：{}，methodName:{}", className, methodName);
            return true;
        }

        log.info("权限拦截器拦截到请求，className：{}，methodName:{}，param:{}", className, methodName,requestParamBuffer.toString());

        User user = null;

        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isNotEmpty(loginToken)) {
            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userJsonStr, User.class);
        }

        if (user == null || (user.getRole().intValue() != Const.Role.ROLE_ADMIN)) {
            // 返回false ，即不会调用controller里面的方法
            response.reset();// 这里要reset ，否则会报getWriter() has already been called for this response
            response.setCharacterEncoding("UTF-8");// 这里设置编码 否则会乱码
            response.setContentType("application/json;charset=UTF-8");// 这里设置返回值类型，因为返回值是接送


            PrintWriter out = response.getWriter();


            // 上传由于富文本的控件要求， 要特殊处理返回值，这里面区分是否登录和是否有权限
            if (user == null) {
                if (StringUtils.equals(className, "ProductManageController") && StringUtils.equals(methodName, "richtextImgUpload")) {
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "请登录管理员");
                    out.print(JsonUtil.obj2String(resultMap));
                } else {
                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截，用户未登录")));
                }
            } else {
                if (StringUtils.equals(className, "ProductManageController") && StringUtils.equals(methodName, "richtextImgUpload")) {
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "无权限操作");
                    out.print(JsonUtil.obj2String(resultMap));
                } else {
                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截，用户无权限操作")));
                }
            }
            out.flush();
            out.close();
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("afterCompletion");
    }
}
