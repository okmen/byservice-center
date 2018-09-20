package com.baiyue.mapper;

import com.baiyue.entity.AdminUsers;

public interface AdminUsersMapper {
    int deleteByPrimaryKey(String phone);

    int insert(AdminUsers record);

    int insertSelective(AdminUsers record);
    /**
     * 根据手机号查询用户
     * @author max
     * @date:   2018年9月20日
     * @Desc :
     * @param phone
     * @return
     */
    AdminUsers selectByPrimaryKey(String phone);

    int updateByPrimaryKeySelective(AdminUsers record);

    int updateByPrimaryKey(AdminUsers record);
    
}