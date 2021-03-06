/* 
 * 文件名：EquipmentController.java  
 * 版权：Copyright 2016-2017 炎宝网络科技  All Rights Reserved by
 * 修改人：邱深友  
 * 创建时间：2017年7月27日
 * 版本号：v1.0
*/
package com.qzi.cms.web.controller;

import javax.annotation.Resource;

import com.qzi.cms.common.po.UseCommunityPo;
import com.qzi.cms.common.po.UseEquipmentNowStatePo;
import com.qzi.cms.common.po.UseEquipmentPortPo;
import com.qzi.cms.common.po.UseResidentPo;
import com.qzi.cms.common.service.RedisService;
import com.qzi.cms.common.util.ToolUtils;
import com.qzi.cms.common.vo.CommunityAdminVo;
import com.qzi.cms.common.vo.UseLockRecordVo;
import com.qzi.cms.server.mapper.UseCommunityMapper;
import com.qzi.cms.server.mapper.UseEquipmentNowStateMapper;
import com.qzi.cms.server.mapper.UseEquipmentPortMapper;
import com.qzi.cms.server.mapper.UseResidentMapper;
import com.qzi.cms.server.service.web.CommunityService;
import com.qzi.cms.server.service.web.WebLockRecordService;
import com.tls.tls_sigature.tls_sigature;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qzi.cms.common.annotation.SystemControllerLog;
import com.qzi.cms.common.enums.RespCodeEnum;
import com.qzi.cms.common.exception.CommException;
import com.qzi.cms.common.resp.Paging;
import com.qzi.cms.common.resp.RespBody;
import com.qzi.cms.common.util.LogUtils;
import com.qzi.cms.common.vo.UseEquipmentVo;
import com.qzi.cms.server.service.web.EquipmentService;

import java.util.Date;
import java.util.List;

/**
 * 设备管理控制器
 * @author qsy
 * @version v1.0
 * @date 2017年7月27日
 */
@RestController
@RequestMapping("/equipment")
public class EquipmentController {
	@Resource
	private EquipmentService equipmentService;

	@Resource
	private CommunityService communityService;

	@Resource
	private UseCommunityMapper useCommunityMapper;


	@Resource
	private WebLockRecordService webLockRecordService;

	@Resource
	private UseResidentMapper useResidentMapper;


	@Resource
	private RedisService redisService;






	public final static long TENCENT_SDKAPPID = 1400222290;//腾讯云appid
	public final static String TENCENT_PRIVSTR = "-----BEGIN PRIVATE KEY-----\n" +
			"MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgC4Qh0iMCqSuiu0PB\n" +
			"jrgeLclBQ3rheHxzE4aDhFUCPfihRANCAATyshmhzWr5QeySThqnWn4B9AiyqyaR\n" +
			"mKfQ0pdzRGHa3Plyc9BXZpfI30fDlq7FJxZx5eYZEWZ7J3Shj2pdr1f8\n" +
			"-----END PRIVATE KEY-----\n";//腾讯云私钥





	@GetMapping("/findCommunitys")
	public RespBody findCommunitys(){
		RespBody respBody = new RespBody();
		try {
			//查找数据并返回
			respBody.add(RespCodeEnum.SUCCESS.getCode(), "获取用户小区信息成功",equipmentService.findCommunitys());
		} catch (Exception ex) {
			respBody.add(RespCodeEnum.ERROR.getCode(), "获取用户小区信息异常");
			LogUtils.error("获取用户小区信息异常！",ex);
		}
		return respBody;
	}
	
	@GetMapping("/findBuildings")
	public RespBody findBuildings(String communityId){
		RespBody respBody = new RespBody();
		try {
			//保存返回数据
			respBody.add(RespCodeEnum.SUCCESS.getCode(), "查找楼栋数据成功", equipmentService.findBuildings(communityId));
		} catch (Exception ex) {
			respBody.add(RespCodeEnum.ERROR.getCode(), "查找楼栋数据失败");
			LogUtils.error("查找楼栋数据失败！",ex);
		}
		return respBody;
	}
	
	@GetMapping("/findUnits")
	public RespBody findUnits(String unitNo,String buildingId){
		RespBody respBody = new RespBody();
		try {
			//保存返回数据
			respBody.add(RespCodeEnum.SUCCESS.getCode(), "查找单元数据成功", equipmentService.findUnits(unitNo,buildingId));
		} catch (Exception ex) {
			respBody.add(RespCodeEnum.ERROR.getCode(), "查找单元数据失败");
			LogUtils.error("查找单元数据失败！",ex);
		}
		return respBody;
	}
	
	@GetMapping("/findAll")
	public RespBody findAll(Paging paging,String criteria){
		RespBody respBody = new RespBody();
		try {
			//保存返回数据

		  List<UseEquipmentVo> list =  equipmentService.findAll(paging,criteria);
		  for(UseEquipmentVo vo:list){
			  if(vo.getLastTime()!=null){
				  if(vo.getLastTime()>60){
				  				  vo.setNowDateStatus("离线");
				  			  }else{
				  				  vo.setNowDateStatus("在线");
				   }
			  }
		  }

			respBody.add(RespCodeEnum.SUCCESS.getCode(), "查找所有设备数据成功", list);
			//保存分页对象
			paging.setTotalCount(equipmentService.findCount(criteria));
			respBody.setPage(paging);
		} catch (Exception ex) {
			respBody.add(RespCodeEnum.ERROR.getCode(), "查找所有设备数据失败");
			LogUtils.error("查找所有设备数据失败！",ex);
		}
		return respBody;
	}






	@GetMapping("/findCommunityAll")
	public RespBody findCommunityAll(){
		RespBody respBody = new RespBody();
		try {

			respBody.add(RespCodeEnum.SUCCESS.getCode(), "查找所有设备数据成功", useCommunityMapper.FindAll1());
		} catch (Exception ex) {
			respBody.add(RespCodeEnum.ERROR.getCode(), "查找所有设备数据失败");
			LogUtils.error("查找所有设备数据失败！",ex);
		}
		return respBody;
	}

	@GetMapping("/getRedisEqu")
	public RespBody getRedisEqu(String equNo){
		RespBody respBody = new RespBody();
		respBody.add(RespCodeEnum.SUCCESS.getCode(), "获取redis成功",redisService.getString(equNo));

		return respBody;
	}


	@GetMapping("/findCommunityIdAll")
	public RespBody findCommunityIdAll(Paging paging,String criteria,String communityId){
		RespBody respBody = new RespBody();
		try {
			//保存返回数据

			List<UseEquipmentVo> list =  equipmentService.findCommunityIdAll(paging,criteria,communityId);
			for(UseEquipmentVo vo:list){
				if(vo.getLastTime()!=null){
					if(vo.getLastTime()>60){
						vo.setNowDateStatus("离线");
					}else{
						vo.setNowDateStatus("在线");
					}
				}
			}

			respBody.add(RespCodeEnum.SUCCESS.getCode(), "查找所有设备数据成功", list);
			//保存分页对象
			paging.setTotalCount(equipmentService.findCommunityIdCount(criteria,communityId));
			respBody.setPage(paging);
		} catch (Exception ex) {
			respBody.add(RespCodeEnum.ERROR.getCode(), "查找所有设备数据失败");
			LogUtils.error("查找所有设备数据失败！",ex);
		}
		return respBody;
	}


	@GetMapping("/findOne")
	public RespBody findOne(String id){
		RespBody respBody = new RespBody();
		try {
			//保存返回数据

			respBody.add(RespCodeEnum.SUCCESS.getCode(), "查找所有设备数据成功", communityService.findOne(id));

		} catch (Exception ex) {
			respBody.add(RespCodeEnum.ERROR.getCode(), "查找所有设备数据失败");
			LogUtils.error("查找所有设备数据失败！",ex);
		}
		return respBody;
	}






	@GetMapping("/getUserSig")
	@SystemControllerLog(description="新增设备UserSig")
	public String getUserSig(String memberId) {
	tls_sigature.GenTLSSignatureResult result = tls_sigature.GenTLSSignatureEx(TENCENT_SDKAPPID ,memberId, TENCENT_PRIVSTR);
			String urlSig = result.urlSig;
			return urlSig;
	}



	@GetMapping("/getWxId")
	@SystemControllerLog(description="新增设备UserSig1")
	public String getWxId(String mobile) {

		UseResidentPo po =  useResidentMapper.findMobile(mobile);
		if(po!=null){
			return po.getWxId();
		}else{

		}
		return "";


	}





	@PostMapping("/add")
	@SystemControllerLog(description="新增设备信息")
	public RespBody add(@RequestBody UseEquipmentVo equipmentVo){
		RespBody respBody = new RespBody();
		try {

			//判断当前的设备号是否超过小区设定的总数
			//			UseCommunityPo communityPo =  communityService.findOne(equipmentVo.getCommunityId());
			//
			//			int count = equipmentService.findCommunityCount(equipmentVo.getCommunityId());
			//			 if((count+1)>  communityPo.getMasterNum()){
			//				 respBody.add(RespCodeEnum.ERROR.getCode(), "设备已超出该小区设置的主机数");
			//				 return respBody;
			//			 }



			equipmentService.add(equipmentVo);








			respBody.add(RespCodeEnum.SUCCESS.getCode(), "设备数据保存成功");
		} catch (CommException ex) {
			respBody.add(RespCodeEnum.ERROR.getCode(), "云之讯调用异常");
			LogUtils.error("云之讯调用异常！",ex);
		} catch (Exception ex) {
			respBody.add(RespCodeEnum.ERROR.getCode(), "设备据保存失败");
			LogUtils.error("设备据保存失败！",ex);
		}
		return respBody;
	}
	
	@PostMapping("/delete")
	@SystemControllerLog(description="删除设备")
	public RespBody delete(@RequestBody UseEquipmentVo equipmentVo){
		RespBody respBody = new RespBody();
		try {
			equipmentService.delete(equipmentVo);
			respBody.add(RespCodeEnum.SUCCESS.getCode(), "设备删除成功");
		} catch (Exception ex) {
			respBody.add(RespCodeEnum.ERROR.getCode(), "设备删除失败");
			LogUtils.error("设备删除失败！",ex);
		}
		return respBody;
	}


	@PostMapping("/update")
	@SystemControllerLog(description="修改设备")
	public RespBody update(@RequestBody UseEquipmentVo equipmentVo){
		RespBody respBody = new RespBody();
		try {
			equipmentService.update(equipmentVo);
			respBody.add(RespCodeEnum.SUCCESS.getCode(), "修改设备成功");
		} catch (Exception ex) {
			respBody.add(RespCodeEnum.ERROR.getCode(), "修改设备失败");
			LogUtils.error("修改设备失败！",ex);
		}
		return respBody;
	}




	/**
	 * equipmentId and nowState
	 * @param equipmentVo
	 * @return
	 */
	@PostMapping("/nowStatus")
	@SystemControllerLog(description="当前设备门磁状态")
	public RespBody nowStatus(@RequestBody UseEquipmentVo equipmentVo){
			RespBody respBody = new RespBody();
			try {
				equipmentService.update(equipmentVo);
				respBody.add(RespCodeEnum.SUCCESS.getCode(), "设备门磁状态修改成功");
			} catch (Exception ex) {
				respBody.add(RespCodeEnum.ERROR.getCode(), "设备门磁状态修改成功");
				LogUtils.error("设备删除失败！",ex);
			}
			return respBody;
		}

	@GetMapping("/lockFindAll")
	public RespBody lockFindAll(Paging paging,String criteria,String sysUserId,String communityId){
		RespBody respBody = new RespBody();
		try {
			//保存返回数据

			UseLockRecordVo vo = new UseLockRecordVo();
			vo.setMobile(criteria);
			vo.setUserId(sysUserId);
			vo.setCommunityId(communityId);

			List<UseLockRecordVo> list =  webLockRecordService.findAll(vo,paging);


			respBody.add(RespCodeEnum.SUCCESS.getCode(), "查找所有开锁记录成功", list);
			//保存分页对象
			paging.setTotalCount(webLockRecordService.findCount(vo));
			respBody.setPage(paging);
		} catch (Exception ex) {
			respBody.add(RespCodeEnum.ERROR.getCode(), "查找所有开锁记录失败");
			LogUtils.error("查找所有开锁记录失败！",ex);
		}
		return respBody;
	}



}
