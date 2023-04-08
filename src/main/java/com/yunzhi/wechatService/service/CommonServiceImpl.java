package com.yunzhi.wechatService.service;


import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CommonServiceImpl {

  static public String addParam(String url , Map<String, String> variables) {
    String requestUrl = url;
    String symbol = "?";

    // 添加url参数格式
    for (Map.Entry<String, String> entry : variables.entrySet()) {
      requestUrl = requestUrl + symbol + entry.getKey() + "={" + entry.getKey() + "}";
      symbol = "&";
    }

    return requestUrl;
  }
}
