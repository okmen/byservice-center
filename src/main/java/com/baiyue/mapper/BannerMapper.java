package com.baiyue.mapper;


import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.baiyue.entity.Banner;


@Repository
public interface BannerMapper {

	public int insert(Banner banner) throws Exception;

	public int updateBanner(Banner banner) throws Exception;

	public int deleteByPrimaryKey(@Param("id")Integer id) throws Exception;

}
