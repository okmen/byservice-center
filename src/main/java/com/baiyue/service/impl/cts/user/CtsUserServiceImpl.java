package com.baiyue.service.impl.cts.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baiyue.entity.AdminUsers;
import com.baiyue.mapper.AdminUsersMapper;
import com.baiyue.services.cts.user.ICtsUserService;
import com.utils_max.ParseUtils;
import com.utils_max.ResultMsg;
import com.utils_max.encrypt.MD5Encrypt;
import com.utils_max.enums.ResultStatusEnums;

@Service("cts_userService")
public class CtsUserServiceImpl implements ICtsUserService{
	@Autowired
	private AdminUsersMapper adminUserMapper;
	
	/**
	 * cts用户登陆
	 */
	@Override
	public ResultMsg login(String usernameOrPhone,String pwd){
		ResultMsg result=new ResultMsg();
		if(ParseUtils.isEmpty(usernameOrPhone)||ParseUtils.isEmpty(pwd)){
			result.setStatus(ResultStatusEnums.error);
			result.setMsg("用户名/密码不能为空！");
			return result;
		}
		AdminUsers user= adminUserMapper.selectByPrimaryKey(usernameOrPhone);
		if(user!=null){
			if(MD5Encrypt.encrypt(pwd).equals(user.getPassword())){
				result.setStatus(ResultStatusEnums.success);
				result.setData(user);
			}else{
				result.setStatus(ResultStatusEnums.error);
				result.setMsg("密码不正确"); 
			}
		}else{
			result.setStatus(ResultStatusEnums.error);
			result.setMsg("用户不存在"); 
		}
		return result;
	}
}
