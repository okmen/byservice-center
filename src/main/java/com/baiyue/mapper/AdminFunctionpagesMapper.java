package com.baiyue.mapper;

import com.baiyue.entity.AdminFunctionpages;

public interface AdminFunctionpagesMapper {
    int deleteByPrimaryKey(Integer pageid);

    int insert(AdminFunctionpages record);

    int insertSelective(AdminFunctionpages record);

    AdminFunctionpages selectByPrimaryKey(Integer pageid);

    int updateByPrimaryKeySelective(AdminFunctionpages record);

    int updateByPrimaryKey(AdminFunctionpages record);
}