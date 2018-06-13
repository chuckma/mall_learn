package com.mall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.pojo.Engineer;
import com.mall.pojo.User;
import com.mall.service.IEngineerService;
import com.mall.util.CookieUtil;
import com.mall.util.JsonUtil;
import com.mall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author Lucas Ma
 * @Date 2018/6/10 下午10:08
 * <p>
 * test
 */

@Controller
@RequestMapping("manage/engineer")
public class EngineerManagerController {

    @Autowired
    private IEngineerService iEngineerService;

    /**
     * 查询操作
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        //
        return this.iEngineerService.engineerList(pageNum, pageSize);
    }

    /**
     * 添加操作
     *
     * @param engineer
     * @return
     */
    @RequestMapping(value = "add_engineer.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> addEngineer(Engineer engineer) {
        return iEngineerService.addEngineer(engineer);
    }

    /**
     * 删除操作
     *
     * @param httpServletRequest
     * @param userName
     * @return
     */
    @RequestMapping("del_engineer.do")
    @ResponseBody
    public ServerResponse<String> delEngineer(HttpServletRequest httpServletRequest, String userName) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登陆");
        }

        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iEngineerService.deleteEngineer(userName);
    }

}
