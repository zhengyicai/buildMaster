package com.qzi.cms.app.controller;

/**
 * Created by Administrator on 2019/3/7.
 */


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.qzi.cms.common.enums.RespCodeEnum;
import com.qzi.cms.common.resp.RespBody;
import com.qzi.cms.common.service.RedisService;
import com.qzi.cms.common.util.HttpClientManager;


import com.qzi.cms.common.util.LogUtils;
import com.qzi.cms.common.vo.UseCommunityVo;
import com.qzi.cms.server.mapper.UseCommunityMapper;
import com.qzi.cms.server.mapper.UseEquipmentMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;


/**
 * 注册控制器
 * @author qsy
 * @version v1.0
 * @date 2017年7月31日
 */
@RestController
@RequestMapping("/wx")
public class WxController {

    @Resource
    private UseCommunityMapper useCommunityMapper;

    private String appid = "wx7e2cbe88acd91b11";
    private String appsecret = "addb33ee9e17d40c22586c44d034d78a";
    private String url = "http://youmowx.zhcloudshare.com"; //回调接口
    private String pageUrl = "http://youmo.zhcloudshare.com/menzha/userAdd.html";  //返回页面
    private String authPageUrl = "http://youmo.zhcloudshare.com/menzha/equipmentList.html";  //访客授权页面


    @Resource
       private RedisService redisService; 


    @RequestMapping("loginInit.do")
    public void  loginInit(HttpServletRequest request, HttpServletResponse response)  throws  Exception {
        //回调地址,要跟下面的地址能调通(getWechatGZAccessToken.do)
        String backUrl=url+"/app/wx/getWechatGZAccessToken.do";








        /**
         *这儿一定要注意！！首尾不能有多的空格（因为直接复制往往会多出空格），其次就是参数的顺序不能变动
         **/
        //AuthUtil.APPID微信公众号的appId   scope是否需要授权用户信息   snsapi_base  只获取openId ，snsapi_userinfo：获取用户其他信息
        String url ="https://open.weixin.qq.com/connect/oauth2/authorize?appid=" +appid+
                "&redirect_uri=" + URLEncoder.encode(backUrl,"UTF-8")+
                "&response_type=code" +
                "&scope=snsapi_userinfo" +
                "&state=STATE#wechat_redirect";
            response.sendRedirect(url);
       // return  url;
    }



    @RequestMapping("getWechatGZAccessToken.do")
    public void getWechatGZAccessToken(HttpServletRequest request,HttpServletResponse response) throws Exception{
        //微信公众号的APPID和APPSECRET
        String code=request.getParameter("code");

        String url="https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appid+
                "&secret=" +appsecret+
                "&code=" +code+
                "&grant_type=authorization_code";
        String result = HttpClientManager.getUrlData(url);

        Map<String,Object> data = JSONObject.parseObject(result);
        String openid=data.get("openid").toString();
        //String unionId=data.get("unionId").toString();



        String token=data.get("access_token").toString();

      //  String result1 = HttpClientManager.getUrlData("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+appsecret);
        //Map<String,Object> data1 = JSONObject.parseObject(result1);
        //System.out.println("result+++++++++++++++++++++++++++++++++++++++++++"+result1);
        String token1=redisService.getString("access_token");
        System.out.println("token1+++++++++++++++++++++++++++++++++++++++++++"+token1);

        

        //获取信息
        String infoUrl="https://api.weixin.qq.com/cgi-bin/user/info?access_token=" +token1+
                "&openid=" +openid+
                "&lang=zh_CN";


//                           https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
//        				url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + access_token + "&openid=" + open_id
//        						+ "&lang=zh_CN";


        String infoResult = HttpClientManager.getUrlData(infoUrl);
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++"+infoResult);

        Map<String,Object> data2 = JSONObject.parseObject(infoResult);
        String openid1=data.get("openid").toString();



        //回调显示页面
        response.sendRedirect(pageUrl+"?openId="+openid);

        //return  infoResult;

    }



    @RequestMapping("authUserInit.do")
    public void  authUserInit(HttpServletRequest request, HttpServletResponse response)  throws  Exception {
        //回调地址,要跟下面的地址能调通(getWechatGZAccessToken.do)
        String backUrl=url+"/app/wx/getAuthWechatGZAccessToken.do";



        /**
         *这儿一定要注意！！首尾不能有多的空格（因为直接复制往往会多出空格），其次就是参数的顺序不能变动
         **/
        //AuthUtil.APPID微信公众号的appId   scope是否需要授权用户信息   snsapi_base  只获取openId ，snsapi_userinfo：获取用户其他信息
        String url ="https://open.weixin.qq.com/connect/oauth2/authorize?appid=" +appid+
                "&redirect_uri=" + URLEncoder.encode(backUrl,"UTF-8")+
                "&response_type=code" +
                "&scope=snsapi_userinfo" +
                "&state=STATE#wechat_redirect";
        response.sendRedirect(url);
        // return  url;
    }



    @RequestMapping("getAuthWechatGZAccessToken.do")
    public void getAuthWechatGZAccessToken(HttpServletRequest request,HttpServletResponse response) throws Exception{
        //微信公众号的APPID和APPSECRET
        String code=request.getParameter("code");

        String url="https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appid+
                "&secret=" +appsecret+
                "&code=" +code+
                "&grant_type=authorization_code";
        String result = HttpClientManager.getUrlData(url);

        Map<String,Object> data = JSONObject.parseObject(result);
        String openid=data.get("openid").toString();
        String token=data.get("access_token").toString();
        //获取基本信息
//        String infoUrl="https://api.weixin.qq.com/sns/userinfo?access_token=" +token+
//                "&openid=" +openid+
//                "&lang=zh_CN";


        String infoUrl="https://api.weixin.qq.com/cgi-bin/user/userinfo?access_token=" +token+
                       "&openid=" +openid+
                       "&lang=zh_CN";
        String infoResult = HttpClientManager.getUrlData(infoUrl);

        //回调显示页面
        response.sendRedirect(authPageUrl+"?openId="+openid);

        //return  infoResult;

    }

    @GetMapping("/wxFindAll")
    public RespBody wxFindAll(){
        RespBody respBody = new RespBody();
        try {

            respBody.add(RespCodeEnum.SUCCESS.getCode(), "参数查找成功",useCommunityMapper.wxFindAll());

        } catch (Exception ex) {
            respBody.add(RespCodeEnum.ERROR.getCode(), "查找个人消息记录数失败");
            LogUtils.error("查找个人消息记录数失败！",ex);
        }
        return respBody;
    }

}
