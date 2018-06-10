package com.mall.dao;

import com.mall.pojo.Engineer;

import java.util.List;

public interface EngineerMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Engineer record);

    int insertSelective(Engineer record);

    Engineer selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Engineer record);

    int updateByPrimaryKey(Engineer record);

    List<Engineer> selectEngineerAll();
}