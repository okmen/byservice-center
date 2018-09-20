package com.baiyue.service.impl.cts.user;

import java.util.ArrayList;
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
import com.baiyue.vo.FunctionPageList;
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
					AdminRole role= roleMapper.selectByPrimaryKey(user.getRoleid());
					if(role!=null){
						loginUser.setRoleName(role.getRolename());
						loginUser.setPowerPageList(getRolePagelistPower(role.getRoleid())); 
					}
				}
				//设置redis 
				String token=UUID.randomUUID().toString();
				RedisUtil.setObject(token, loginUser, 7200);
				
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
	
	/**
	 * 获取cts角色的页面权限菜单联级
	 * @author max
	 * @date:   2018年9月20日
	 * @Desc :
	 * @param roleId
	 * @return
	 */
	public List<FunctionPageList> getRolePagelistPower(Integer roleId){
		List<AdminFunctionpages>pageListAll=pageMapper.findList(1);//获取所有已经注册的页面
		List<AdminRolepages> rolePagelist=rolePageMapper.findList(roleId);
		List<AdminFunctionpages> rolelst=new ArrayList<>();
		List<FunctionPageList> firstList=new ArrayList<>();
		if(rolePagelist!=null&&rolePagelist.size()>0){
			for (AdminRolepages item : rolePagelist) {
				for (AdminFunctionpages ii : pageListAll) {
					if(ii.getPageid().equals(item.getPageid())){
						rolelst.add(ii);
						if(ii.getPageid()==null||ii.getPageid()==0){
							FunctionPageList first=new FunctionPageList();
							first.setPageId(ii.getPageid());
							first.setPageName(ii.getPagename());
							first.setPagePath(ii.getPath());
							first.setIsEnable(1);
							firstList.add(first);
						}
					}
				}
			}
			
		}
		List<FunctionPageList> result= ff(firstList,rolelst);
		return result;
	}
	
	/**
	 * 菜单联级递归
	 * @author max
	 * @date:   2018年9月20日
	 * @Desc :
	 * @param resultlist
	 * @param list
	 * @return
	 */
	private List<FunctionPageList> ff(List<FunctionPageList> resultlist,List<AdminFunctionpages> list){
		if(resultlist!=null&&resultlist.size()>0){
			for (FunctionPageList result : resultlist) {
				List<FunctionPageList> sublist=new ArrayList<>();
				for (AdminFunctionpages item : list) {
					if(item.getPageid().equals(result.getPageId())){
						FunctionPageList ss=new FunctionPageList();
						ss.setPageId(item.getPageid());
						ss.setPageName(item.getPagename());
						ss.setPagePath(item.getPath());
						ss.setIsEnable(1);
						sublist.add(ss);
					}
				}
				if(sublist.size()>0){
					ff(sublist,list);
					result.setSubList(sublist); 
				}
			}
		}
		return resultlist;
	}
}
