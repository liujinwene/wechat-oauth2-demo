package com.everhomes.wechat.controller;

import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.everhomes.util.GsonUtil;
import com.everhomes.util.HttpUtil;
import com.everhomes.wechat.resp.GetWechatAccessTokenResponse;
import com.everhomes.wechat.resp.GetWechatUserInfoResponse;
import com.everhomes.wechat.resp.User;

@Controller
@RequestMapping("/wechat")
public class WechatController {
	private final static Logger LOGGER = LoggerFactory.getLogger(WechatController.class);
	
	private static final String sessionUserName = "user";
	private static final String sourceUrlName = "sourceUrl";
	
	private static final String wechatAuthorizeUrlFormat = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=%s#wechat_redirect";
	private static final String wechatAccessTokenUrlFormat = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
	private static final String wechatGetUserinfoUrlFormat = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";

	private static final String homeUrlConf = "http://biz-beta.zuolin.com/wechat-oauth2";
	private static final String redirectUri = "/wechat/redirect";

	private static final String wechatAppId = "wxc78b52690ababcd4";
	private static final String wechatAppSecrect = "11a9a7384bc836cabea368cecd314e65";


	@RequestMapping("/getWechatOauth2")
	public void getWechatOauth2(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String sessionId = request.getSession().getId();

		String sourceUrl = URLEncoder.encode(request.getParameter(sourceUrlName), "UTF-8");

		//拼接redirect_uri
		String redirectUrlFormat = "%s?%s=%s";
		String redirectUrl = String.format(redirectUrlFormat, (homeUrlConf+redirectUri), sourceUrlName, sourceUrl);
		//拼接微信授权接口，填充app_id,redirect_uri
		String authorizeUrl = String.format(wechatAuthorizeUrlFormat, wechatAppId, URLEncoder.encode(redirectUrl, "UTF-8"), sessionId);
		response.sendRedirect(authorizeUrl);
	}

	@RequestMapping("/redirect")
	public void redirect(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String code = request.getParameter("code");
		String sourceUrl = URLDecoder.decode(request.getParameter(sourceUrlName), "UTF-8");

		GetWechatAccessTokenResponse accessTokenResponse = getWechatAccessToken(code);
		GetWechatUserInfoResponse userinfoResponse = getUserInfo(accessTokenResponse);
		
		//填充用户信息
		String defaultUserId = "1111";
		User user = new User(defaultUserId, userinfoResponse.getNickname(), userinfoResponse.getHeadimgurl());
		
		request.getSession().setAttribute(sessionUserName, user);
		response.sendRedirect(sourceUrl);
	}

	private GetWechatUserInfoResponse getUserInfo(GetWechatAccessTokenResponse accessTokenResponse) {
		//拉取用户信息(需scope为 snsapi_userinfo)
		String userinfoUri = String.format(wechatGetUserinfoUrlFormat, accessTokenResponse.getAccess_token(), accessTokenResponse.getOpenid());
		LOGGER.debug("userinfoUri=" + userinfoUri);
		
		String userinfoResponseStr = HttpUtil.get(userinfoUri);
		LOGGER.debug("beforeEncode-userinfoResponseStr=" + userinfoResponseStr);
		
		GetWechatUserInfoResponse userinfoResponse = GsonUtil.fromJson(userinfoResponseStr, GetWechatUserInfoResponse.class);
		if (userinfoResponse.getErrcode() != null) {
			LOGGER.error("get userInfo fail.errcode=" + userinfoResponse.getErrcode() + ",errmsg=" + userinfoResponse.getErrmsg());
			throw new RuntimeException("get userInfo fail");
		}
		return userinfoResponse;
	}

	private GetWechatAccessTokenResponse getWechatAccessToken(String code) {
		//通过code换取网页授权access_token
		String accessTokenUri = String.format(wechatAccessTokenUrlFormat, wechatAppId, wechatAppSecrect, code);
		LOGGER.debug("accessTokenUri=" + accessTokenUri);
		
		String accessTokenResponseStr = HttpUtil.get(accessTokenUri);
		LOGGER.debug("accessTokenResponseStr=" + accessTokenResponseStr);
		
		GetWechatAccessTokenResponse accessTokenResponse = GsonUtil.fromJson(accessTokenResponseStr, GetWechatAccessTokenResponse.class);
		if (accessTokenResponse.getErrcode()!=null) {
			LOGGER.error("get accessToken fail.errcode="+accessTokenResponse.getErrcode()+",errmsg="+accessTokenResponse.getErrmsg());
			throw new RuntimeException("get accessToken fail");
		}
		return accessTokenResponse;
	}

}
