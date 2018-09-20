package com.baiyue.service.impl.cts.user;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baiyue.entity.AdminFunctionpages;
import com.baiyue.entity.AdminRole;
import com.baiyue.entity.AdminRolepages;
import com.baiyue.entity.AdminUsers;
import com.baiyue.mapper.AdminFunctionpagesMapper;
import com.baiyue.mapper.AdminRoleMapper;
import com.baiyue.mapper.AdminRolepagesMapper;
import com.baiyue.mapper.AdminUsersMapper;
import com.baiyue.services.cts.user.ICtsUserService;
import com.baiyue.vo.result.LoginSuccessCtsUserInfo;
import com.utils_max.ParseUtils;
import com.utils_max.ResultMsg;
import com.utils_max.encrypt.MD5Encrypt;
import com.utils_max.enums.ResultStatusEnums;
import com.utils_max.redis.RedisUtil;

@Service("cts_userService")
public class CtsUserServiceImpl implements ICtsUserService{
	private Logger logger = Logger.getLogger(CtsUserServiceImpl.class);
	
	@Autowired
	private AdminUsersMapper adminUserMapper;
	@Autowired
	private AdminRoleMapper roleMapper;
	@Autowired
	private AdminFunctionpagesMapper pageMapper;
	@Autowired
	private AdminRolepagesMapper rolePageMapper;
	
	
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
				result.setMsg("login success!"); 
				//登陆成功返回登陆相应用户信息
				LoginSuccessCtsUserInfo loginUser=new LoginSuccessCtsUserInfo();
				loginUser.setPhone(user.getPhone());
				loginUser.setUsername(user.getUsername()); 
				loginUser.setRealname(user.getRealname());
				loginUser.setRoleId(user.getRoleid()); 
				if(user.getRoleid()!=null&&user.getRoleid().intValue()>0){
					System.out.println("用户角色:"); 
					AdminRole role= roleMapper.selectByPrimaryKey(user.getRoleid());
					if(role!=null){
						loginUser.setRoleName(role.getRolename());
						List<AdminFunctionpages>pageListAll=pageMapper.findList(1);//获取所有已经注册的页面
						logger.info("所有注册页面共："+pageListAll==null?0:pageListAll.size()+"条");
						List<AdminRolepages> rolePagelist=rolePageMapper.findList(user.getRoleid());
						logger.info("角色【"+loginUser.getRoleName()+"】获取权力页面："+rolePagelist==null?0:rolePagelist.size()+"条");
					}
				}
				String token=UUID.randomUUID().toString();
				System.out.println("token:"+token); 
				try {
//					RedisUtil.setObject(token, loginUser, 7200);
				} catch (Exception e) {
					// TODO: handle exception
					logger.error("redis问题："+e.getMessage()); 
				}
				
				Map<String, Object> resultData=new HashMap<>();
				resultData.put("accessToken", token);
				resultData.put("userInfo", loginUser);
				result.setData(resultData);
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
	
	/**
	 * 新增角色
	 * @author max
	 * @date:   2018年9月20日
	 * @Desc :
	 * @param param
	 * @param username
	 * @return
	 */
	@Override
	public ResultMsg addRole(AdminRole param,String username){
		ResultMsg result=new ResultMsg();
		result.setStatus(ResultStatusEnums.error); 
		if(param==null||ParseUtils.isEmpty(param.getRolename())){
			result.setMsg("角色名称不能为空！");
			return result;
		}
		if(ParseUtils.isEmpty(username)){
			result.setMsg("操作人不能为空！");
			return result;
		}
		param.setCreatetime(new Date());
		param.setUpdatetime(new Date());
		param.setIsadmin(0);//是否超级管理员（默认都不是超级管理员）
		int i=roleMapper.insert(param);
		if(i>0){
			result.setStatus(ResultStatusEnums.success);
			result.setMsg("操作成功！");
		}else{
			result.setMsg("操作失败！");
			return result;
		}
		return result;
	}
	/**
	 * cts用户新增 
	 * @author max
	 * @date:   2018年9月20日
	 * @Desc :
	 * @param param （ps password 参数需md5加密后传过来）
	 * @param username
	 * @return
	 */
	@Override
	public ResultMsg addUser(AdminUsers param,String username){
		ResultMsg result=new ResultMsg();
		result.setStatus(ResultStatusEnums.error); 
		if(param==null||ParseUtils.isEmpty(param.getUsername())||ParseUtils.isEmpty(param.getPassword())||ParseUtils.isEmpty(param.getRoleid())||ParseUtils.isEmpty(param.getRealname())||ParseUtils.isEmpty(param.getPhone())){
			result.setMsg("参数缺失！"); 
			return result;
		}
		if(ParseUtils.isEmpty(username)){
			result.setMsg("操作人不能为空！");
			return result;
		}
		//检查用户是否再存
		AdminUsers user=adminUserMapper.selectByPrimaryKey(param.getPhone());
		if(user!=null){
			result.setMsg("手机号["+param.getPhone()+"]已经注册！");
			return result;
		}
		int i=adminUserMapper.insert(param);
		if(i>0){
			result.setStatus(ResultStatusEnums.success);
			result.setMsg("操作成功！");
		}else{
			result.setMsg("操作失败！");
			return result;
		}
		return result;
	}
	
}
