package com.yunzhi.wechatService.entity;


/**
 * 微信用户
 */
public class WeChatUser {

  private Long id;

  private String openid;

  private String appId;

  public WeChatUser() {
  }

  public WeChatUser(String openid, String appId) {
    this.openid = openid;
    this.appId = appId;
  }

  public void setOpenid(String openid) {
    this.openid = openid;
  }

  public String getOpenid() {
    return this.openid;
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }
}
