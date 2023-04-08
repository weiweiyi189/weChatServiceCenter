package com.yunzhi.wechatService.service;

import com.netflix.discovery.util.StringCache;
import com.yunzhi.wechatService.config.RequestWrapper;
import com.yunzhi.wechatService.entity.WeChatUser;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.rmi.UnexpectedException;
import java.util.List;
import java.util.Locale;


@Service
public class WechatServiceImpl implements WechatService {

  @Autowired
  private DiscoveryClient discoveryClient;

  private final WeChatMpService wxMpService;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());


  public WechatServiceImpl(WeChatMpService wxMpService) {
    this.wxMpService = wxMpService;
  }



  /**
   * 获取对应client的url
   * @param httpServletRequest
   * @return client url
   * @throws IOException
   */
  @Override
  public String selectClientUrl(HttpServletRequest httpServletRequest) throws IOException {
    // 获取场景值
    String requestBody =  new RequestWrapper(httpServletRequest).getBodyString();

    WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);
    String  sceneStr = "";
    if (inMessage.getEventKey().startsWith("qrscene_")) {
      sceneStr = inMessage.getEventKey().substring("qrscene_".length());
    } else {
      sceneStr = inMessage.getEventKey();
    }
    // 根据场景值获取client name
    int dashIndex = sceneStr.indexOf("-");
    String clientName = sceneStr.substring(0, dashIndex);
    StringCache.intern(clientName.toUpperCase(Locale.ROOT));

    // 从注册中心获取该client
    List<ServiceInstance> serviceInstances = discoveryClient.getInstances(clientName);
    if(CollectionUtils.isEmpty(serviceInstances)) {
      throw new UnexpectedException("未找到对应" + clientName + "的客户端");
    }

    ServiceInstance si = serviceInstances.get(0);
    return "http://" + si.getHost() + ":" + si.getPort();
  }

  @Override
  public String getQrCode(String sceneStr) throws WxErrorException {
    WxMpQrCodeTicket wxMpQrCodeTicket = this.wxMpService.getQrcodeService().qrCodeCreateTmpTicket(sceneStr, 10 * 60);
    return this.wxMpService.getQrcodeService().qrCodePictureUrl(wxMpQrCodeTicket.getTicket());
  }
}
