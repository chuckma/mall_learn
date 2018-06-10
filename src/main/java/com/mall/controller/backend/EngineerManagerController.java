package com.mall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mall.common.ServerResponse;
import com.mall.service.IEngineerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        //
        return this.iEngineerService.engineerList(pageNum, pageSize);
    }
}
