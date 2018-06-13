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

    public ServerResponse<String> addEngineer(Engineer engineer) {
        int resultCount = engineerMapper.insert(engineer);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("添加失败");
        }
        return ServerResponse.createBySuccessMessage("添加成功");
    }


    public ServerResponse<String> deleteEngineer(String userName) {
        int res = engineerMapper.deleteByUserName(userName);
        if (res == 0) {
            return ServerResponse.createByErrorMessage("删除失败!");
        }
        return ServerResponse.createBySuccessMessage("删除成功！");
    }


    public ServerResponse<Engineer> getEngineer(Integer userId) {
        Engineer engineer = engineerMapper.selectByPrimaryKey(userId);
        if (engineer == null) {
            return ServerResponse.createByErrorMessage("抱歉，没有该用户信息");
        }
        return ServerResponse.createBySuccess(engineer);
    }

}
