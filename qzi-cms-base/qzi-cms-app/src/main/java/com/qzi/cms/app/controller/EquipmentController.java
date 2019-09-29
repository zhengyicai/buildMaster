package com.qzi.cms.app.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonParser;
import com.qzi.cms.common.annotation.SystemControllerLog;
import com.qzi.cms.common.enums.RespCodeEnum;
import com.qzi.cms.common.exception.CommException;
import com.qzi.cms.common.po.*;
import com.qzi.cms.common.resp.RespBody;
import com.qzi.cms.common.service.RedisService;
import com.qzi.cms.common.util.HttpClientManager;
import com.qzi.cms.common.util.LogUtils;
import com.qzi.cms.common.util.SoundwavUtils;
import com.qzi.cms.common.util.ToolUtils;
import com.qzi.cms.common.vo.HomeUserCommunityVo;
import com.qzi.cms.common.vo.UseEquipmentNowStateVo;
import com.qzi.cms.common.vo.UseEquipmentVo;
import com.qzi.cms.common.vo.UseResidentVo;
import com.qzi.cms.server.mapper.*;
import com.qzi.cms.server.service.app.UseCommunityResidentService;
import com.qzi.cms.server.service.web.EquipmentService;
import com.qzi.cms.server.service.web.ResidentService;
import com.tls.tls_sigature.tls_sigature;
import org.omg.PortableInterceptor.SUCCESSFUL;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2019/2/25.
 */



@RestController
@RequestMapping("/equipment")
public class EquipmentController {
    @Resource
    private UseRoomCardMapper useRoomCardMapper;
    @Resource
    private UseEquipmentMapper useEquipmentMapper;

    @Resource
    private UseCommunityMapper useCommunityMapper;

    @Resource
    private UseBannerMapper useBannerMapper;


    @Resource
    private UseCardEquipmentMapper useCardEquipmentMapper;

    @Resource
    private UseUserCardEquipmentMapper useUserCardEquipmentMapper;

    @Resource
    private UseResidentMapper useResidentMapper;

    @Resource
    private UseCommunityResidentMapper useCommunityResidentMapper;



    @Resource
    private UseEquipmentPortMapper useEquipmentPortMapper;

    @Resource
    private  UseEquipmentNowStateMapper useEquipmentNowStateMapper;


    @Resource
    private  UseResidentCardMapper useResidentCardMapper;


    @Resource
    private UseResidentUnlockRecordMapper useResidentUnlockRecordMapper;

    @Resource
    private UseResidentEquipmentMapper useResidentEquipmentMapper;


    @Resource
    private UseUnlockEquRecordMapper useUnlockEquRecordMapper;


    @Resource
    private RedisService redisService;


    public  DatagramSocket  socket = null;

    @Resource
    private EquipmentService equipmentService;





    private String imagepath = "/data/page/uploadImages/";
    private   String appid = "wx7e2cbe88acd91b11";
    private   String miniappid = "wxedddefb4077ec474";
    private   String appsecret = "addb33ee9e17d40c22586c44d034d78a";



    public final static long TENCENT_SDKAPPID = 1400222290;//腾讯云appid
    public final static String TENCENT_PRIVSTR = "-----BEGIN PRIVATE KEY-----\n" +
            "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgC4Qh0iMCqSuiu0PB\n" +
            "jrgeLclBQ3rheHxzE4aDhFUCPfihRANCAATyshmhzWr5QeySThqnWn4B9AiyqyaR\n" +
            "mKfQ0pdzRGHa3Plyc9BXZpfI30fDlq7FJxZx5eYZEWZ7J3Shj2pdr1f8\n" +
            "-----END PRIVATE KEY-----\n";//腾讯云私钥


    /**
     * 获取设备列表
     * @return
     */
    @GetMapping("/getlist")
    public RespBody getlist(HomeUserCommunityVo homeUserCommunityVo){
        RespBody respBody = new RespBody();
        try {

            List<UseEquipmentVo> list = useEquipmentMapper.appFindUseEquipmentNo(homeUserCommunityVo.getDefaultRoomId(),homeUserCommunityVo.getRoomId().substring(0,10));
            List<UseEquipmentVo> list1 = useEquipmentMapper.appFindUseEquipmentNo1(homeUserCommunityVo.getDefaultRoomId(),homeUserCommunityVo.getRoomId().substring(0,6));


            if(list1.size()>0){
                for(UseEquipmentVo epo:list1){
                    list.add(epo);
                }
            }


            respBody.add(RespCodeEnum.SUCCESS.getCode(), "获取设备列表成功",list);

        } catch (Exception ex) {
            respBody.add(RespCodeEnum.ERROR.getCode(), "获取设备列表失败");
            LogUtils.error("获取设备列表失败！",ex);
        }
        return respBody;
    }

    /**
     * 获取卡号列表
     * @return
     */
    @GetMapping("/getCardlist")
    public RespBody getCardlist(HomeUserCommunityVo homeUserCommunityVo){
        RespBody respBody = new RespBody();
        try {
            respBody.add(RespCodeEnum.SUCCESS.getCode(), "获取卡号列表成功",useCardEquipmentMapper.findCardList(homeUserCommunityVo.getDefaultRoomId(),homeUserCommunityVo.getEquipmentId()));
        } catch (Exception ex) {
            respBody.add(RespCodeEnum.ERROR.getCode(), "获取卡号列表失败");
            LogUtils.error("获取卡号列表失败！",ex);
        }
        return respBody;
    }


    @PostMapping("/updateCardState")
    public RespBody updateCardState(@RequestBody HomeUserCommunityVo homeUserCommunityVo){
        RespBody respBody = new RespBody();
        try {
            useCardEquipmentMapper.updateCardEquipment(homeUserCommunityVo.getCardId(),homeUserCommunityVo.getEquipmentId(),homeUserCommunityVo.getLinkState());
            respBody.add(RespCodeEnum.SUCCESS.getCode(), "发卡成功");
        } catch (Exception ex) {
            respBody.add(RespCodeEnum.ERROR.getCode(), "发卡失败");
            LogUtils.error("发卡失败！",ex);
        }
        return respBody;

    }


    //获取小区对应的设备
    @GetMapping("/getCommunity")
    public RespBody getCommunity(String  communityId){
        RespBody respBody = new RespBody();

        try {
            List<UseEquipmentVo> useEquipmentPoList =   useEquipmentMapper.communityIdList(communityId);
            respBody.add(RespCodeEnum.SUCCESS.getCode(), "获取卡号列表成功",useEquipmentPoList);
        } catch (Exception ex) {
            respBody.add(RespCodeEnum.ERROR.getCode(), "发卡失败");
            LogUtils.error("发卡失败！",ex);
        }
        return respBody;
    }

    //获取该设备的房卡列表

    @GetMapping("/getUserCardList")
    public RespBody getUserCardList(String  equipmentId){
        RespBody respBody = new RespBody();

        try {
            respBody.add(RespCodeEnum.SUCCESS.getCode(), "获取卡号列表成功",useUserCardEquipmentMapper.findCardList(equipmentId));
        } catch (Exception ex) {
            respBody.add(RespCodeEnum.ERROR.getCode(), "发卡失败");
            LogUtils.error("发卡失败！",ex);
        }
        return respBody;
    }


    @PostMapping("/updateUserCardState")
    public RespBody updateUserCardState(@RequestBody HomeUserCommunityVo homeUserCommunityVo){
        RespBody respBody = new RespBody();
        try {
            useUserCardEquipmentMapper.updateUserCardEquipment(homeUserCommunityVo.getCardId(),homeUserCommunityVo.getLinkState());
            respBody.add(RespCodeEnum.SUCCESS.getCode(), "发卡成功");
        } catch (Exception ex) {
            respBody.add(RespCodeEnum.ERROR.getCode(), "发卡失败");
            LogUtils.error("发卡失败！",ex);
        }
        return respBody;

    }


    /**
     * 添加人脸开锁记录（带开锁图片）
     */
    @ResponseBody
    @RequestMapping(value = "/addEquRecord",method = RequestMethod.POST)
    public RespBody addEquRecord(MultipartFile file, UseUnlockEquRecordPo po, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IllegalStateException, IOException {
        RespBody respBody=new RespBody();

        if (file!=null) {// 判断上传的文件是否为空
            String path=null;// 文件路径
            String type=null;// 文件类型
            String fileName=file.getOriginalFilename();// 文件原名称
            System.out.println("上传的文件原名称:"+fileName);
            // 判断文件类型
            type=fileName.indexOf(".")!=-1?fileName.substring(fileName.lastIndexOf(".")+1, fileName.length()):null;
            if (type!=null) {// 判断文件类型是否为空
                if ("GIF".equals(type.toUpperCase())||"PNG".equals(type.toUpperCase())||"JPG".equals(type.toUpperCase())) {




                    // 项目在容器中实际发布运行的根路径
                    String realPath=request.getSession().getServletContext().getRealPath("/");
                    // 自定义的文件名称
                    String trueFileName=String.valueOf(System.currentTimeMillis())+fileName;
                    // 设置存放图片文件的路径


                    path = imagepath+/*System.getProperty("file.separator")+*/trueFileName;
                    //path = realPath+/*System.getProperty("file.separator")+*/trueFileName;

                    System.out.println("存放图片文件的路径:"+path);
                    // 转存文件到指定的路径
                    file.transferTo(new File(path));


                    Runtime.getRuntime().exec("chmod 777 -R " +imagepath+trueFileName);
                    //添加设备记录

                    UseUnlockEquRecordPo recordPo = new UseUnlockEquRecordPo();
                    recordPo.setId(ToolUtils.getUUID());
                    recordPo.setWxId(po.getWxId());
                    recordPo.setEquipmentNo(po.getEquipmentNo());
                    recordPo.setState(po.getState());
                    recordPo.setCreateTime(new Date());
                    recordPo.setImgUrl(trueFileName);
                    recordPo.setRemark(po.getRemark());
                    useUnlockEquRecordMapper.insert(recordPo);


                    respBody.add(RespCodeEnum.SUCCESS.getCode(), "添加成功",trueFileName);
                }else {
                    System.out.println("不是我们想要的文件类型,请按要求重新上传");
                    respBody.add(RespCodeEnum.ERROR.getCode(), "文件类型不对，请重新上传");
                    return respBody;
                }
            }else {
                System.out.println("文件类型为空");
                respBody.add(RespCodeEnum.ERROR.getCode(), "文件类型为空");
                return respBody;
            }
        }else {
            System.out.println("没有找到相对应的文件");
            respBody.add(RespCodeEnum.ERROR.getCode(), "没有找到相对应的文件");
            return respBody;
        }
        return respBody;
    }









    /**
     * 添加人脸记录数据
     */
    @ResponseBody
    @RequestMapping(value = "/photoUpload",method = RequestMethod.POST)
    public RespBody photoUpload(MultipartFile file, UseResidentVo useResidentVo, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IllegalStateException, IOException {
        RespBody respBody=new RespBody();

        if (file!=null) {// 判断上传的文件是否为空
            String path=null;// 文件路径
            String type=null;// 文件类型
            String fileName=file.getOriginalFilename();// 文件原名称
            System.out.println("上传的文件原名称:"+fileName);
            // 判断文件类型
            type=fileName.indexOf(".")!=-1?fileName.substring(fileName.lastIndexOf(".")+1, fileName.length()):null;
            if (type!=null) {// 判断文件类型是否为空
                if ("GIF".equals(type.toUpperCase())||"PNG".equals(type.toUpperCase())||"JPG".equals(type.toUpperCase())) {





                    // 项目在容器中实际发布运行的根路径
                    String realPath=request.getSession().getServletContext().getRealPath("/");
                    // 自定义的文件名称
                    String trueFileName=String.valueOf(System.currentTimeMillis())+fileName;
                    // 设置存放图片文件的路径


                    path=imagepath+/*System.getProperty("file.separator")+*/trueFileName;
                   // path=realPath+trueFileName;  //本地服务器图片目录
                    System.out.println("存放图片文件的路径:"+path);
                    // 转存文件到指定的路径
                    file.transferTo(new File(path));
                    Runtime.getRuntime().exec("chmod 777 -R " +imagepath+trueFileName);
                    UseResidentPo wxUseResidentPo = useResidentMapper.findWxId(useResidentVo.getWxId());
                    if(wxUseResidentPo!=null){


                        wxUseResidentPo.setImgUrl(trueFileName);
                        wxUseResidentPo.setMobile(useResidentVo.getMobile());
                        wxUseResidentPo.setName(useResidentVo.getName());
                        wxUseResidentPo.setCreateTime(new Date());
                        wxUseResidentPo.setIdentityNo(useResidentVo.getIdentityNo());
                        useResidentMapper.updateByPrimaryKey(wxUseResidentPo);

                        List<UseResidentEquipmentPo> epo =  useResidentEquipmentMapper.findResidentState(wxUseResidentPo.getId());
                        if(epo !=null){
                            for(UseResidentEquipmentPo epo1:epo){
                                useEquipmentNowStateMapper.update("20",epo1.getEquipmentId());
                            }
                        }
                        //respBody.add(RespCodeEnum.ERROR.getCode(), "该微信已绑定过小区");

                        return respBody;
                    }


                    //添加用户
                    UseResidentPo useResidentPo = new UseResidentPo();
                    String id = ToolUtils.getUUID();
                    useResidentPo.setId(id);
                    useResidentPo.setWxId(useResidentVo.getWxId());
                    useResidentPo.setCreateTime(new Date());
                    useResidentPo.setFingerUrl("");
                    useResidentPo.setIdentityId("");

                    DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    try {

                        if("10".equals(useResidentVo.getResidentType())){
                            Date myDate2 = dateFormat2.parse("2099-12-31 23:59:59");
                            useResidentPo.setLastTime(myDate2);
                        }else{
                           Date   date=new Date(); //取时间
                            Calendar   calendar = new GregorianCalendar();
                            calendar.setTime(date);
                            calendar.add(calendar.DATE,1); //把日期往后增加一天,整数  往后推,负数往前移动
                            date=calendar.getTime(); //这个时间就是日期往后推一天的结果
                            useResidentPo.setLastTime(date);
                        }


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    useResidentPo.setResidentType(useResidentVo.getResidentType());
                    useResidentPo.setState("10");
                    useResidentPo.setIdentityNo(useResidentVo.getIdentityNo());
                    useResidentPo.setMobile(useResidentVo.getMobile());
                    useResidentPo.setName(useResidentVo.getName());
                    useResidentPo.setWxId(useResidentVo.getWxId());
                    useResidentPo.setImgUrl(trueFileName);
                    useResidentMapper.insert(useResidentPo);



                    //添加用户和小区的关联
                    UseCommunityResidentPo residentPo = new UseCommunityResidentPo();
                    residentPo.setCommunityId(useResidentVo.getCommunityId());
                    residentPo.setResidentId(id);
                    residentPo.setState("10");
                    useCommunityResidentMapper.insert(residentPo);




                    respBody.add(RespCodeEnum.SUCCESS.getCode(), "添加成功");
                }else {
                    System.out.println("不是我们想要的文件类型,请按要求重新上传");
                    respBody.add(RespCodeEnum.ERROR.getCode(), "文件类型不对，请重新上传");
                    return respBody;
                }
            }else {
                System.out.println("文件类型为空");
                respBody.add(RespCodeEnum.ERROR.getCode(), "文件类型为空");
                return respBody;
            }
        }else {
            System.out.println("没有找到相对应的文件");
            respBody.add(RespCodeEnum.ERROR.getCode(), "没有找到相对应的文件");
            return respBody;
        }
        return respBody;
    }


    /**
     * 用户设备列表
     */


    @GetMapping("/userEquipment")
    public RespBody userEquipment(String  wxid){
        RespBody respBody = new RespBody();
        List<UseEquipmentVo > list =   useResidentEquipmentMapper.findWxId(wxid);
        if(list == null){
            respBody.add(RespCodeEnum.ERROR.getCode(),"没有设备授权");
        }else{
            respBody.add(RespCodeEnum.SUCCESS.getCode(),"设备授权列表",list);
        }
        return respBody;
    }




    @GetMapping("/findResident")
    public RespBody findResident(String  wxid){
        RespBody respBody = new RespBody();
        UseResidentVo vo =   useResidentMapper.findWxIds(wxid);
        if(vo == null){
            respBody.add(RespCodeEnum.ERROR.getCode(),"该用户没有绑定");
        }else{


            respBody.add(RespCodeEnum.SUCCESS.getCode(),"用户数据获取成功",vo);
        }
        return respBody;
    }






    /**
     * 远程开锁
     */

    @PostMapping("/onlineUnlock")
    public void onlineUnlock(UseEquipmentPortPo po) throws  Exception {

        UseEquipmentPortPo  portPo =   useEquipmentPortMapper.findOne(po.getEquipmentNo());

        UseResidentPo usePo =  useResidentMapper.findWxId(po.getId());

        UseResidentVo useResidentVo = new UseResidentVo();
        useResidentVo.setMobile(usePo.getMobile());
        useResidentVo.setWxId(usePo.getWxId());
        useResidentVo.setCmd("unlock");
        useResidentVo.setDeviceId(po.getEquipmentId());

        useResidentVo.setEquipmentNo(po.getEquipmentNo());

        byte[] bs = JSON.toJSONString(useResidentVo).getBytes();//要发的信息内容

        InetAddress desIp = InetAddress.getByName(portPo.getIps());
        DatagramPacket p = new DatagramPacket(bs, bs.length, desIp, Integer.parseInt(portPo.getPort()));

        //创建数据报套接字，UDPA的端口设置为10086
        //DatagramSocket  = new DatagramSocket(9999);

        if(socket == null){
           sentPort();
           return;
        }
        //UDPA给UDPB发送数据报
        socket.send(p);

        System.out.println("发送udp"+desIp+":"+portPo.getPort()+""+p);
        //关闭socket_A套接字
        //Thread.sleep(1000);
        //socket.close();


    }


    /**
     * 发起通话
     */

    @GetMapping("/onlineStartPlay")
    public void onlineStartPlay(UseEquipmentPortPo po) throws  Exception {

        UseEquipmentPortPo  portPo =   useEquipmentPortMapper.findOne(po.getEquipmentNo());




        UseResidentVo useResidentVo = new UseResidentVo();

        if("0".equals(po.getId())){
            useResidentVo.setCmd("startPlay0");
        }else{
            useResidentVo.setCmd("startPlay1");
        }



        useResidentVo.setDeviceId(po.getEquipmentId());

        useResidentVo.setEquipmentNo(po.getEquipmentNo());

        byte[] bs = JSON.toJSONString(useResidentVo).getBytes();//要发的信息内容

        InetAddress desIp = InetAddress.getByName(portPo.getIps());
        DatagramPacket p = new DatagramPacket(bs, bs.length, desIp, Integer.parseInt(portPo.getPort()));

        //创建数据报套接字，UDPA的端口设置为10086
        //DatagramSocket  = new DatagramSocket(9999);

        if(socket == null){
            sentPort();
            return;
        }
        //UDPA给UDPB发送数据报
        socket.send(p);

        System.out.println("发送udp"+desIp+":"+portPo.getPort()+""+p);
        //关闭socket_A套接字
        //Thread.sleep(1000);
        //socket.close();


    }


    /**
     * 发起通话
     */

    @GetMapping("/onlineStopPlay")
    public void onlineStopPlay(UseEquipmentPortPo po) throws  Exception {

        UseEquipmentPortPo  portPo =   useEquipmentPortMapper.findOne(po.getEquipmentNo());

        UseResidentVo useResidentVo = new UseResidentVo();
        useResidentVo.setCmd("stopPlay");
        useResidentVo.setDeviceId(po.getEquipmentId());
        useResidentVo.setEquipmentNo(po.getEquipmentNo());
        byte[] bs = JSON.toJSONString(useResidentVo).getBytes();//要发的信息内容

        InetAddress desIp = InetAddress.getByName(portPo.getIps());
        DatagramPacket p = new DatagramPacket(bs, bs.length, desIp, Integer.parseInt(portPo.getPort()));

        //创建数据报套接字，UDPA的端口设置为10086
        //DatagramSocket  = new DatagramSocket(9999);

        if(socket == null){
            sentPort();
            return;
        }
        //UDPA给UDPB发送数据报
        socket.send(p);

        System.out.println("发送udp"+desIp+":"+portPo.getPort()+""+p);
        //关闭socket_A套接字
        //Thread.sleep(1000);
        //socket.close();


    }


    /**
     * 获取开锁记录
     */

    @GetMapping("/getRecord")
    public RespBody getRecord(String  wxId){
        RespBody respBody = new RespBody();

        try {
            respBody.add(RespCodeEnum.SUCCESS.getCode(), "获取开锁列表成功",useUnlockEquRecordMapper.findAll1(wxId));
        } catch (Exception ex) {
            respBody.add(RespCodeEnum.ERROR.getCode(), "获取开锁列表失败");
            LogUtils.error("获取开锁列表失败！",ex);
        }
        return respBody;
    }

//
//    public void doData(String value){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                value
//            }
//        }).start();
//    }




    //获取微信assess_token
    private String getAccessToken() {


        String url="https://api.weixin.qq.com/cgi-bin/token?"+"grant_type=client_credential&appid="+appid+"&secret="+appsecret;
        try {

            String result = HttpClientManager.getUrlData(url);

            JSONObject pa=JSONObject.parseObject(result);
            if(pa  !=null){
                redisService.putString("access_token",pa.getString("access_token") , 7000).equalsIgnoreCase("ok");
            }else{

            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }



    @PostMapping("/sentWeixinText")
    public RespBody sentWeixin(@RequestBody WxObjectVo vo) throws Exception {
        RespBody respBody = new RespBody();

        WxMessage wx = new WxMessage();

        WxContent content = new WxContent();
        content.setContent(vo.getContent());

        wx.setTouser(vo.getWxId());
        wx.setMsgtype("text");
        wx.setText(content);

        String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+redisService.getString("access_token");
        System.out.println("test"+ JSON.toJSONString(wx)) ;
        respBody.add(RespCodeEnum.SUCCESS.getCode(), "操作结果", HttpClientManager.postUrlData(url,JSON.toJSONString(wx)));
        return respBody;
    }


    @PostMapping("/sentMiniText")
    public RespBody sentMiniText(@RequestBody WxObjectVo vo) throws Exception {
        RespBody respBody = new RespBody();

        List<UseResidentVo> list =   useResidentEquipmentMapper.findResidentId( vo.getEquNo());

        UseEquipmentPo use =   useEquipmentMapper.findEquNo(vo.getEquNo());
        String str = "";
        if(use!=null){
            UseCommunityPo useCommunityPo =   useCommunityMapper.findOne(use.getCommunityId());
          if(useCommunityPo!=null){
              str = useCommunityPo.getCommunityName();
          }
        }


        if(list.size()>0){
            for(int i = 0;i<list.size();i++){
                WxMessage wx = new WxMessage();

                WxContent content = new WxContent();
                content.setContent("<a data-miniprogram-appid='"+miniappid+"' data-miniprogram-path='pages/webrtc-room/oneroom/oneroom?roomId="+ vo.getEquNo()+"' href='http://www.qq.com'>"+ str+vo.getEquNo().substring(6,8)+" 号机有新的报警呼叫，点击进入视频通话</a>");
                System.out.print("<a data-miniprogram-appid='"+miniappid+"' data-miniprogram-path='pages/webrtc-room/oneroom/oneroom?roomId="+ vo.getEquNo()+"' href='http://www.qq.com'>"+ str+vo.getEquNo().substring(6,8)+" 号机有新的报警呼叫，点击进入视频通话</a>");
                wx.setTouser(list.get(i).getWxId());
                wx.setMsgtype("text");
                wx.setText(content);

                String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+redisService.getString("access_token");
                System.out.println("test"+ JSON.toJSONString(wx)) ;
                HttpClientManager.postUrlData(url,JSON.toJSONString(wx));



                UseUnlockEquRecordPo useUnlockEquRecordPo = new UseUnlockEquRecordPo();
                useUnlockEquRecordPo.setId(ToolUtils.getUUID());
                useUnlockEquRecordPo.setEquipmentNo(vo.getEquNo());
                useUnlockEquRecordPo.setWxId(list.get(i).getWxId());
                useUnlockEquRecordPo.setCreateTime(new Date());
                useUnlockEquRecordPo.setState("10");
                useUnlockEquRecordPo.setImgUrl("");
                useUnlockEquRecordPo.setRemark("");
                useUnlockEquRecordMapper.insert(useUnlockEquRecordPo);

                try {
                    Thread.sleep(200);     //设置暂停的时间，5000=5秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }






        respBody.add(RespCodeEnum.SUCCESS.getCode(), "操作结果","ok");
        return respBody;
    }








    @PostMapping("/sentWeixinNews")
    public RespBody sentWeixinNews(@RequestBody WxObjectVo vo) throws Exception {
        RespBody respBody = new RespBody();

        WxNews wx = new WxNews();

        WxNewsContents wxNewsContents = new WxNewsContents();
        wxNewsContents.setDescription(vo.getDescription());  //备注详情
        wxNewsContents.setPicurl(vo.getUrl());
        wxNewsContents.setUrl(vo.getUrl());
        wxNewsContents.setTitle(vo.getContent());

        WxNewsList list1 = new WxNewsList();
        List<WxNewsContents> newsContentsList = new ArrayList<WxNewsContents>();
        newsContentsList.add(wxNewsContents);

        list1.setArticles(newsContentsList);

        wx.setTouser(vo.getWxId());
        wx.setMsgtype("news");
        wx.setNews(list1);

        String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+redisService.getString("access_token");
        System.out.println("test"+ JSON.toJSONString(wx)) ;
        respBody.add(RespCodeEnum.SUCCESS.getCode(), "操作结果", HttpClientManager.postUrlData(url,JSON.toJSONString(wx)));
        return respBody;
    }






    //获取设备是否需要同步数据
    @GetMapping("/sentPort")
    public RespBody sentPort() throws  Exception{
        RespBody respBody = new RespBody();


        if(socket != null){
            return respBody;
        }


        //刷新assess_token
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
          @Override
          public void run() {
                System.out.println("assess_token:"+getAccessToken()) ;
          }
        }, 1000,7000000);// 设定指定的时间time,此处为2000毫秒



        new Thread(new Runnable() {
            @Override
            public void run() {
                try {


                socket = new DatagramSocket(7000);
                byte[] buf = new byte[1024];

                while(true){
                    //建立udp的服务 ，并且要监听一个端口。
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length); // 1024
                        //调用udp的服务接收数据
                        socket.receive(datagramPacket); //receive是一个阻塞型的方法，没有接收到数据包之前会一直等待。 数据实际上就是存储到了byte的自己数组中了。
                        System.out.println("接收端接收到的数据："+ new String(buf,0,datagramPacket.getLength(),"UTF-8")); // getLength() 获取数据包存储了几个字节。
                        System.out.println("接收端接收到的数据："+datagramPacket.getData()); // getLength() 获取数据包存储了几个字节。
                        System.out.println("receive阻塞了我，哈哈"+datagramPacket.getAddress()+":"+datagramPacket.getPort());








                        //设置数据
                         String mess =      new String(buf,0,datagramPacket.getLength(),"UTF-8");






                         //10：判断已经接通，20：未接通
                        if(mess.indexOf("startPlay")!=-1){
                            redisService.putString(mess.substring(0,8)+"trtc","10",72000).equalsIgnoreCase("ok");
                        }else if(mess.indexOf("stopPlay")!=-1){
                            redisService.putString(mess.substring(0,8)+"trtc","20",72000).equalsIgnoreCase("ok");

                        }else{


                            if(redisService.getString(mess.substring(0,8)+"trtc")!=null){
                                if(mess.indexOf("videostop")!=-1){
                                    redisService.putString(mess.substring(0,8)+"trtc","40",72000).equalsIgnoreCase("ok");
                                }

                            }else{
                                redisService.putString(mess.substring(0,8)+"trtc","30",72000).equalsIgnoreCase("ok");
                            }


                         //   redisService.putString(mess.substring(0,8)+"trtc","20",72000).equalsIgnoreCase("ok");
                            redisService.putString(mess.substring(0,8),mess.substring(9,mess.length()) , 72000).equalsIgnoreCase("ok");
                            //doData(datagramPacket.getData());
                            //UseEquipmentPortPo portPo = new UseEquipmentPortPo();
                            useEquipmentPortMapper.update(String.valueOf(datagramPacket.getAddress()).substring(1,String.valueOf(datagramPacket.getAddress()).length()),datagramPacket.getPort()+"",mess.substring(0,8));

                            UseEquipmentNowStatePo nowStatePo =   useEquipmentNowStateMapper.findOne(mess.substring(0,8));
                            if(nowStatePo == null){
                                respBody.add(RespCodeEnum.SUCCESS.getCode(), "error");
                                byte[] bs = "error".getBytes();//要发的信息内容
                                //UDPA与UDPB的ip均为本机ip，故设置不同的端口号
                                InetAddress desIp = InetAddress.getByName(datagramPacket.getAddress().toString().substring(1,datagramPacket.getAddress().toString().length()));

                                System.out.println("desIp"+desIp);
                                DatagramPacket p = new DatagramPacket(bs, bs.length, desIp, datagramPacket.getPort());
                                socket.send(p);
                            }else{
                                respBody.add(RespCodeEnum.SUCCESS.getCode(), nowStatePo.getState());

                                UseEquipmentNowStateVo nowStateVo = new UseEquipmentNowStateVo();
                                nowStateVo.setEquipmentNo(nowStatePo.getEquipmentNo());
                                nowStateVo.setState(nowStatePo.getState());
                                nowStateVo.setCmd("heartack");

                                byte[] bs = JSON.toJSONString(nowStateVo).getBytes();//要发的信息内容
                                //UDPA与UDPB的ip均为本机ip，故设置不同的端口号
                                InetAddress desIp = InetAddress.getByName(datagramPacket.getAddress().toString().substring(1,datagramPacket.getAddress().toString().length()));

                                System.out.println("desIp"+desIp);
                                DatagramPacket p = new DatagramPacket(bs, bs.length, desIp, datagramPacket.getPort());
                                socket.send(p);
                            }

                        }





                        //关闭资源
                      //  socket.close();



                }
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();



        return respBody;


    }

    @PostMapping("/sentUnlockSuccess")
    public RespBody sentUnlockSuccess(@RequestBody UseResidentUnlockRecordPo po){
        RespBody respBody = new RespBody();
        try {
            UseResidentUnlockRecordPo unlockRecordPo = new UseResidentUnlockRecordPo();

            unlockRecordPo.setId(ToolUtils.getUUID());
            unlockRecordPo.setWxId(po.getWxId());
            unlockRecordPo.setEquipmentNo(po.getEquipmentNo());
            unlockRecordPo.setState(po.getState());
            unlockRecordPo.setCreateTime(new Date());
            unlockRecordPo.setDeviceId(po.getDeviceId());
            useResidentUnlockRecordMapper.insert(unlockRecordPo);
            respBody.add(RespCodeEnum.SUCCESS.getCode(), "添加开锁列表成功");

        } catch (Exception ex) {
            respBody.add(RespCodeEnum.ERROR.getCode(), "参数查找成功");
            LogUtils.error("参数查找成功！",ex);
        }
        return respBody;
    }


    @GetMapping("/getUnlockSuccess")
    public RespBody getUnlockSuccess(String deviceId ) throws  Exception{
        RespBody respBody = new RespBody();
        UseResidentUnlockRecordPo po = useResidentUnlockRecordMapper.deviceId(deviceId);
        if(po!=null){
            respBody.add(RespCodeEnum.SUCCESS.getCode(), "查找所有住户房卡数据成功", po);
        }else{
            respBody.add(RespCodeEnum.ERROR.getCode(), "查找所有住户房卡数据成功", po);
        }
        return respBody;
    }


    @GetMapping("/getRedis")
   public RespBody getRedis(String message){
       RespBody respBody = new RespBody();

        String mess =   message;
        //"00000301,dfdf,正文,温度,水压,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1";
       // redisService.putString(mess.substring(0,8),mess.substring(9,mess.length()) , 72000).equalsIgnoreCase("ok");

        if(mess.indexOf("startPlay")!=-1){
            redisService.putString("00000301trtc","10",72000).equalsIgnoreCase("ok");
        }else if(mess.indexOf("stopPlay")!=-1){
            redisService.putString("00000301trtc","20",72000).equalsIgnoreCase("ok");

        }

        //redisService.putString("00000301trtc","10",72000).equalsIgnoreCase("ok");
       //redisService.putString("00000301","dfdf,正文,温度,水压,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1" , 7000).equalsIgnoreCase("ok");

       return respBody;
   }

    @GetMapping("/getRedisEqu")
    public RespBody getRedisEqu(String equNo){
        RespBody respBody = new RespBody();


        if(redisService.getString(equNo+"trtc")!=null){

        }else{
            redisService.putString(equNo+"trtc","30",72000).equalsIgnoreCase("ok");
        }
        respBody.add(RespCodeEnum.SUCCESS.getCode(), "获取redis成功",redisService.getString(equNo+"trtc"));

        return respBody;
    }


    @GetMapping("/getRedisEqu1")
    public RespBody getRedisEqu1(String equNo){
        RespBody respBody = new RespBody();
        respBody.add(RespCodeEnum.SUCCESS.getCode(), "获取redis成功",redisService.getString(equNo));

        return respBody;
    }




    //获取设备是否需要同步数据(弃用)
    @GetMapping("/sentUnlock")
    public RespBody sentUnlock() throws  Exception{
        RespBody respBody = new RespBody();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //建立udp的服务 ，并且要监听一个端口。
                    DatagramSocket  socket = null;
                    try {
                        socket = new DatagramSocket(6000);
                        byte[] buf = new byte[1024];
                        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length); // 1024
                        //调用udp的服务接收数据

                        socket.receive(datagramPacket); //receive是一个阻塞型的方法，没有接收到数据包之前会一直等待。 数据实际上就是存储到了byte的自己数组中了。
                        System.out.println("接收端接收到的数据："+ new String(buf,0,datagramPacket.getLength())); // getLength() 获取数据包存储了几个字节。



                        UseResidentUnlockRecordPo unlockRecordPo = new UseResidentUnlockRecordPo();
                        String[] recordList = new String(buf,0,datagramPacket.getLength()).split(",",-1);
                        unlockRecordPo.setId(ToolUtils.getUUID());
                        unlockRecordPo.setWxId(recordList[0]);
                        unlockRecordPo.setEquipmentNo(recordList[1]);
                        unlockRecordPo.setState("10");
                        unlockRecordPo.setCreateTime(new Date());
                        useResidentUnlockRecordMapper.insert(unlockRecordPo);




                        //UseEquipmentPortPo portPo = new UseEquipmentPortPo();
                        //useEquipmentPortMapper.update(String.valueOf(datagramPacket.getAddress()).substring(1,String.valueOf(datagramPacket.getAddress()).length()),datagramPacket.getPort()+"",new String(buf,0,datagramPacket.getLength()));



                        //	UseEquipmentNowStatePo nowStatePo =   useEquipmentNowStateMapper.findOne(new String(buf,0,datagramPacket.getLength()));
                        //
                        //	respBody.add(RespCodeEnum.SUCCESS.getCode(), nowStatePo.getState());
                        //


                        //关闭资源
                        socket.close();


                        byte[] bs = "10".getBytes();//要发的信息内容
                        //UDPA与UDPB的ip均为本机ip，故设置不同的端口号
                        InetAddress desIp = InetAddress.getByName(datagramPacket.getAddress().toString().substring(1,datagramPacket.getAddress().toString().length()));

                        //数据报包，UDPB的端口为10010
                        DatagramPacket p = new DatagramPacket(bs, bs.length, desIp, datagramPacket.getPort());
                        //创建数据报套接字，UDPA的端口设置为10086
                        DatagramSocket socket_A = new DatagramSocket(9999);
                        //UDPA给UDPB发送数据报
                        socket_A.send(p);
                        //关闭socket_A套接字
                        socket_A.close();

                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //准备空的数据包用于存放数据。

                }
            }
        }).start();



        return respBody;


    }


    /**
     * 查询用户
     * @param equipmentNo
     * @return
     * @throws Exception
     */

    @GetMapping("/getResident")
    public RespBody getResident(String equipmentNo ) throws  Exception{
        RespBody respBody = new RespBody();
        List<UseResidentVo> relist =  useResidentEquipmentMapper.findResidentId(equipmentNo);
        if(relist!=null){
            for(UseResidentVo vo1:relist){
                vo1.setCardPos(useResidentCardMapper.findResidentCardNoIds(vo1.getId()));
            }
        }
        respBody.add(RespCodeEnum.SUCCESS.getCode(), "查找所有住户房卡数据成功", relist);
        return respBody;
    }



    @GetMapping("/getCommunitys")
    public RespBody getCommunitys(){
        RespBody respBody = new RespBody();


        respBody.add(RespCodeEnum.SUCCESS.getCode(), "查找小区数据成功", useCommunityMapper.FindAllList());
        return respBody;
    }

    @GetMapping("/getCommunityList")
    public RespBody getCommunityList(String wxId){
        RespBody respBody = new RespBody();

        UseResidentPo po =   useResidentMapper.findWxId(wxId);
        if(po!=null){
           UseCommunityResidentPo useCommunityResidentPo =    useCommunityResidentMapper.existsCRResident(po.getId());
           if(useCommunityResidentPo!=null){

               respBody.add(RespCodeEnum.SUCCESS.getCode(), "查找小区数据成功",useResidentEquipmentMapper.selectResidnet(useCommunityResidentPo.getResidentId()));
           }
        }

        return respBody;
    }


    @GetMapping("/getCommunityList1")
        public RespBody getCommunityList1(String wxId){
            RespBody respBody = new RespBody();

            UseResidentPo po =   useResidentMapper.findWxId(wxId);
            if(po!=null){
               UseCommunityResidentPo useCommunityResidentPo =    useCommunityResidentMapper.existsCRResident(po.getId());
               if(useCommunityResidentPo!=null){
                   respBody.add(RespCodeEnum.SUCCESS.getCode(), "查找小区数据成功", useEquipmentMapper.findCommunity(useCommunityResidentPo.getCommunityId()));
               }
            }

            return respBody;
        }


     @PostMapping("/saveCommunityList")
     public RespBody saveCommunityList(@RequestBody UseEquipmentVo equipmentVo){
            RespBody respBody = new RespBody();


            String userId = "";

            UseResidentPo useEquipmentPo =   useResidentMapper.findWxId(equipmentVo.getId());
            userId = useEquipmentPo.getId();





            if(equipmentVo.getEquipments().size()>0){
                useResidentEquipmentMapper.deleteResident1(userId);
            }


            for(int i=0;i<equipmentVo.getEquipments().size();i++){

                UseResidentEquipmentPo po1 = new UseResidentEquipmentPo();
                po1.setId(ToolUtils.getUUID());
                po1.setResidentId(userId);
                po1.setCommunityId(equipmentVo.getCommunityId());
                po1.setEquipmentId(equipmentVo.getEquipments().get(i));
                po1.setState("30");
                
                useResidentEquipmentMapper.insert(po1);
            }


            return respBody;
        }

    @GetMapping("/getCommunityListSelect")
      public RespBody getCommunityListSelect(String wxId){
          RespBody respBody = new RespBody();
        UseResidentPo po =   useResidentMapper.findWxId(wxId);
          if(po!=null){
              respBody.add(RespCodeEnum.SUCCESS.getCode(), "查找用户设备数据成功",  useResidentEquipmentMapper.selectResidnet1(po.getId()));

          }else{
              respBody.add(RespCodeEnum.ERROR.getCode(), "查找用户设备数据失败");
          }
          return respBody;
      }




    @GetMapping("/getBannerList")
    public RespBody getBannerList(String communityId){
        RespBody respBody = new RespBody();


        respBody.add(RespCodeEnum.SUCCESS.getCode(), "查找小区数据成功", useBannerMapper.findBannerslist(communityId));
        return respBody;
    }















    @PostMapping("/add")
    	@SystemControllerLog(description="新增设备信息")
    	public RespBody add(@RequestBody UseEquipmentVo equipmentVo){
    		RespBody respBody = new RespBody();
    		try {






    	        UseEquipmentPo po = useEquipmentMapper.findEquNo(equipmentVo.getEquNo());
    	        if(po!=null){

                    tls_sigature.GenTLSSignatureResult result = tls_sigature.GenTLSSignatureEx(TENCENT_SDKAPPID ,po.getEquNo(), TENCENT_PRIVSTR);
                     String urlSig = result.urlSig;

                    respBody.add(RespCodeEnum.SUCCESS.getCode(),"该设备已注册", urlSig);
                    return respBody;
                }

                equipmentService.add(equipmentVo);
                tls_sigature.GenTLSSignatureResult result = tls_sigature.GenTLSSignatureEx(TENCENT_SDKAPPID ,equipmentVo.getEquNo(), TENCENT_PRIVSTR);
                String urlSig = result.urlSig;

    			respBody.add(RespCodeEnum.SUCCESS.getCode(),"注册成功", urlSig);
    		} catch (CommException ex) {
    			respBody.add(RespCodeEnum.ERROR.getCode(), "云之讯调用异常");
    			LogUtils.error("云之讯调用异常！",ex);
    		} catch (Exception ex) {
    			respBody.add(RespCodeEnum.ERROR.getCode(), "设备据保存失败");
    			LogUtils.error("设备据保存失败！",ex);
    		}
    		return respBody;
    	}











}
