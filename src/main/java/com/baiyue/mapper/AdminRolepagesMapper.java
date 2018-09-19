package com.baiyue.mapper;

import com.baiyue.entity.AdminRolepages;

public interface AdminRolepagesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AdminRolepages record);

    int insertSelective(AdminRolepages record);

    AdminRolepages selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AdminRolepages record);

    int updateByPrimaryKey(AdminRolepages record);
}