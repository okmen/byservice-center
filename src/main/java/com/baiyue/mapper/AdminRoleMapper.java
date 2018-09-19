package com.baiyue.mapper;

import com.baiyue.entity.AdminRole;

public interface AdminRoleMapper {
    int deleteByPrimaryKey(Integer roleid);

    int insert(AdminRole record);

    int insertSelective(AdminRole record);

    AdminRole selectByPrimaryKey(Integer roleid);

    int updateByPrimaryKeySelective(AdminRole record);

    int updateByPrimaryKey(AdminRole record);
}