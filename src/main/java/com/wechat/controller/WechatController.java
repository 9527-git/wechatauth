package com.wechat.controller;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;

@Controller
@RequestMapping("/wechat")
@Slf4j
public class WechatController {

    @Autowired
    private WxMpService wxMpService;

    /**
     * 打开微信端，用户进行授权
     * 获取授权的url：
     * https://m30011.jp.cdjxt.net/wechat/authorize?returnUrl=m30011.jp.cdjxt.net
     * @param returnUrl 授权页面设置的回调域名
     * @return
     */
    @GetMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl){
        String url = "https://m30011.jp.cdjxt.net/wechat/userInfo";
        String redirectURL = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAuth2Scope.SNSAPI_USERINFO, URLEncoder.encode(returnUrl));
        log.info("【微信网页授权】获取code,redirectURL={}", redirectURL);
        return "redirect:" + redirectURL;
    }

    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code,
                         @RequestParam("state") String returnUrl) throws Exception {
        log.info("【微信网页授权！！！！！！！】code={}", code);
        log.info("【微信网页授权】state={}", returnUrl);
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken;
        try {
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);

        } catch (WxErrorException e) {
            log.info("【微信网页授权】{}", e);
            throw new Exception(e.getError().getErrorMsg());
        }
        String openId = wxMpOAuth2AccessToken.getOpenId();
        WxMpUser wxMpUser = this.wxMpService.getUserService()
                .userInfo(openId, "zh_CN");
        log.info("用户信息：：",wxMpUser.toString());
        log.info("【微信网页授权】openId={}", openId);
        return "redirect:" + returnUrl + "?openid=" + openId;
    }
}
