package com.yunzhi.wechatService.controller;

import com.yunzhi.wechatService.service.WechatService;
import me.chanjar.weixin.common.error.WxErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/request")
public class WechatClientController {
  private final WechatService wechatService;

  WechatClientController(WechatService wechatService) {
    this.wechatService = wechatService;

  }
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @GetMapping("/getQrCode")
  public String getQrCode(@RequestParam String sceneStr) throws WxErrorException {
    return this.wechatService.getQrCode(sceneStr);
  }
}
