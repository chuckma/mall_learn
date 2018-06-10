package com.mall.service;

import com.github.pagehelper.PageInfo;
import com.mall.common.ServerResponse;

public interface IEngineerService {

    ServerResponse<PageInfo> engineerList(int pageNum, int pageSize);
}
