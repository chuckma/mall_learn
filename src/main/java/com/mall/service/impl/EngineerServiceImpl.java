package com.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.common.ServerResponse;
import com.mall.dao.EngineerMapper;
import com.mall.pojo.Engineer;
import com.mall.service.IEngineerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Lucas Ma
 * @Date 2018/6/10 下午10:17
 */
@Service("iEngineerService")
public class EngineerServiceImpl implements IEngineerService {

    @Autowired
    private EngineerMapper engineerMapper;


    public ServerResponse<PageInfo> engineerList(int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<Engineer> engineerList = this.engineerMapper.selectEngineerAll();
        PageInfo pageInfoRet = new PageInfo(engineerList);
        return ServerResponse.createBySuccess(pageInfoRet);

    }

}
