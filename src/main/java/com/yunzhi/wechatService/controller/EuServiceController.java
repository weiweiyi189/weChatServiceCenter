package com.yunzhi.wechatService.controller;


import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;

@Controller
@RequestMapping("/eurekacenter")
public class EuServiceController {

  @Qualifier("eurekaClient")
  @Autowired
  private EurekaClient eurekaClient;

  @GetMapping("/services")
  @ResponseBody
  public List<Application> getServices() {
    return eurekaClient.getApplications().getRegisteredApplications();
  }
}
