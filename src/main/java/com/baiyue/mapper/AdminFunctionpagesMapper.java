package com.baiyue.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baiyue.entity.AdminFunctionpages;

public interface AdminFunctionpagesMapper {
    int deleteByPrimaryKey(Integer pageid);

    int insert(AdminFunctionpages record);

    int insertSelective(AdminFunctionpages record);

    AdminFunctionpages selectByPrimaryKey(Integer pageid);

    int updateByPrimaryKeySelective(AdminFunctionpages record);

    int updateByPrimaryKey(AdminFunctionpages record);
    /**
     * 获取页面注册列表
     * @author max
     * @date:   2018年9月20日
     * @Desc :
     * @param status
     * @return
     */
    List<AdminFunctionpages> findList(@Param("status") Integer status);
}