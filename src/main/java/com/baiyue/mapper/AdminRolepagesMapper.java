package com.baiyue.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baiyue.entity.AdminRolepages;

public interface AdminRolepagesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AdminRolepages record);

    int insertSelective(AdminRolepages record);

    AdminRolepages selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AdminRolepages record);

    int updateByPrimaryKey(AdminRolepages record);
    /**
     * 获取角色的页面权力列表
     * @author max
     * @date:   2018年9月20日
     * @Desc :
     * @param roleid
     * @return
     */
    List<AdminRolepages> findList(@Param("roleid")Integer roleid);
}