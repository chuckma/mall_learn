package com.mall.service;

import com.github.pagehelper.PageInfo;
import com.mall.common.ServerResponse;
import com.mall.pojo.Engineer;

public interface IEngineerService {

    ServerResponse<PageInfo> engineerList(int pageNum, int pageSize);

    ServerResponse<String> addEngineer(Engineer engineer);

    ServerResponse<String> deleteEngineer(String userName);

    ServerResponse<Engineer> getEngineer(Integer userId);
}
