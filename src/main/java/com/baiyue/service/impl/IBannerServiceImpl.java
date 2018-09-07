package com.baiyue.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baiyue.entity.Banner;
import com.baiyue.mapper.BannerMapper;
import com.baiyue.services.IBannerService;



@Service("bannerService")
public class IBannerServiceImpl implements IBannerService{
	@Autowired
	private BannerMapper bannerDao;

	@Override
	public int insert(Banner banner) throws Exception {
		return bannerDao.insert(banner);
	}

	@Override
	public int updateBanner(Banner banner) throws Exception {
		return bannerDao.updateBanner(banner);
	}

	@Override
	public int deleteByPrimaryKey(Integer id) throws Exception {
		System.out.println(id); 
		return bannerDao.deleteByPrimaryKey(id);
	}


	/**
	 * 根据id获取轮播图
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Banner selectByPrimaryKey(Integer id) throws Exception{
	
		return null;
	}

}
