package com.yunzhi.wechatService.service;

import com.yunzhi.wechatService.config.WxMpConfig;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class WeChatMpService extends WxMpServiceImpl {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private WxMpConfig wxMpConfig;

  @PostConstruct
  public void init() {
    final WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
    // 设置微信公众号的appid
    config.setAppId(this.wxMpConfig.getAppid());
    // 设置微信公众号的app corpSecret
    config.setSecret(this.wxMpConfig.getAppSecret());
    // 设置微信公众号的token
    config.setToken(this.wxMpConfig.getToken());
    // 设置消息加解密密钥
    config.setAesKey(this.wxMpConfig.getAesKey());
    super.setWxMpConfigStorage(config);
  }

}
