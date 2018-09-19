package com.baiyue.mapper;

import com.baiyue.entity.AdminUsers;

public interface AdminUsersMapper {
    int deleteByPrimaryKey(String phone);

    int insert(AdminUsers record);

    int insertSelective(AdminUsers record);

    AdminUsers selectByPrimaryKey(String phone);

    int updateByPrimaryKeySelective(AdminUsers record);

    int updateByPrimaryKey(AdminUsers record);
}