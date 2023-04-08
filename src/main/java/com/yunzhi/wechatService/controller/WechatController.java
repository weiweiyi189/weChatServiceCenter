package com.yunzhi.wechatService.controller;


import com.yunzhi.wechatService.config.RequestWrapper;
import com.yunzhi.wechatService.service.CommonServiceImpl;
import com.yunzhi.wechatService.service.WeChatMpService;
import com.yunzhi.wechatService.service.WechatService;
import com.yunzhi.wechatService.service.WechatServiceImpl;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.rmi.UnexpectedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 用于转发微信的请求给client
 */
@RequestMapping("wechat")
@RestController
public class WechatController {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());


  @Autowired
  WeChatMpService weChatMpService;

  /**
   * client的接受微信请求的接口
   */
  static public String RequestUrl = "/wechat";

  private final WechatService wechatService;

  public WechatController(WechatService wechatService) {
    this.wechatService = wechatService;
  }


  /**
   * 对接 API，注意返回类型为void，不能为String。原样返回的数据需要直接使用HttpServletResponse
   * 微信官方说明：https://developers.weixin.qq.com/doc/offiaccount/Basic_Information/Access_Overview.html
   *
   * @param signature 微信加密签名，signature结合了开发者填写的 token 参数和请求中的 timestamp 参数、nonce参数。
   * @param timestamp 时间戳
   * @param nonce     这是个随机数
   * @param echostr   随机字符串，验证成功后原样返回
   */
  @GetMapping
  public void get(@RequestParam(required = false) String signature,
                  @RequestParam(required = false) String timestamp,
                  @RequestParam(required = false) String nonce,
                  @RequestParam(required = false) String echostr,
                  HttpServletResponse response) throws IOException {
    if (!this.weChatMpService.checkSignature(timestamp, nonce, signature)) {
      this.logger.warn("接收到了未通过校验的微信消息，这可能是token配置错了，或是接收了非微信官方的请求");
      return;
    }
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(echostr);
    response.getWriter().flush();
    response.getWriter().close();
  }

  /**
   * 当设置完微信公众号的接口后，微信会把用户发送的消息，扫码事件等推送过来
   *
   * @param signature 微信加密签名，signature结合了开发者填写的 token 参数和请求中的 timestamp 参数、nonce参数。
   * @param encType 加密类型（暂未启用加密消息）
   * @param msgSignature 加密的消息
   * @param timestamp 时间戳
   * @param nonce 随机数
   * @throws IOException
   */
  @PostMapping(produces = "text/xml; charset=UTF-8")
  public void api(HttpServletRequest httpServletRequest,
                  HttpServletResponse httpServletResponse,
                  @RequestParam("signature") String signature,
                  @RequestParam(name = "encrypt_type", required = false) String encType,
                  @RequestParam(name = "msg_signature", required = false) String msgSignature,
                  @RequestParam("timestamp") String timestamp,
                  @RequestParam("nonce") String nonce) throws IOException {
    if (!this.weChatMpService.checkSignature(timestamp, nonce, signature)) {
      this.logger.warn("接收到了未通过校验的微信消息，这可能是token配置错了，或是接收了非微信官方的请求");
      return;
    }

    // 获取客户端url
    String targetUrl = this.wechatService.selectClientUrl(httpServletRequest);

    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(targetUrl + RequestUrl)
        .queryParam("signature", signature)
        .queryParam("timestamp", timestamp)
        .queryParam("nonce", nonce);

    if (msgSignature != null) {
      builder.queryParam("msg_signature", msgSignature);
    }
    if (encType != null) {
      builder.queryParam("encrypt_type", encType);
    }

    URI uri = builder.build().encode().toUri();
    // 设置转发请求的参数
    HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
    connection.setDoOutput(true);
    connection.setDoInput(true);
    if (msgSignature != null) {
      connection.setRequestProperty("msg_signature", msgSignature);
    }
    if (encType != null) {
      connection.setRequestProperty("encrypt_type", encType);
    }

    // 将 httpServletRequest 中的数据写入 转发请求中
    OutputStream outputStream = connection.getOutputStream();
    String requestBody =  new RequestWrapper(httpServletRequest).getBodyString();
    outputStream.write(requestBody.getBytes(StandardCharsets.UTF_8));
    outputStream.flush();
    outputStream.close();

    // 获取client 的响应数据
    InputStream inputStream = connection.getInputStream();
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int bytesRead;
    while ((bytesRead = inputStream.read(buffer)) != -1) {
      byteArrayOutputStream.write(buffer, 0, bytesRead);
    }
    byte[] responseBytes = byteArrayOutputStream.toByteArray();
    inputStream.close();

    // 将响应数据返回给微信
    httpServletResponse.setContentType("text/xml; charset=UTF-8");
    httpServletResponse.setContentLength(responseBytes.length);
    httpServletResponse.getOutputStream().write(responseBytes);
    httpServletResponse.getOutputStream().flush();
    httpServletResponse.getOutputStream().close();
  }

}


