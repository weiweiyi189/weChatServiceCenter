package com.yunzhi.wechatService.service;


import com.yunzhi.wechatService.entity.WeChatUser;
import me.chanjar.weixin.common.error.WxErrorException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface WechatService {



  String selectClientUrl(HttpServletRequest httpServletRequest) throws IOException;

  /**
   * 获取二维码
   * @param sceneStr
   * @return
   * @throws WxErrorException
   */
  String getQrCode(String sceneStr) throws WxErrorException;
}
