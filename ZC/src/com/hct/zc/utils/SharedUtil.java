package com.hct.zc.utils;

import android.app.Activity;

import com.hct.zc.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMWXHandler;

/**
 * 
 * 第三方分享用到的.
 * 
 * @time 2014年5月9日 下午4:21:04
 * @author jie.liu
 */
public class SharedUtil {
	// 首先在您的Activity中添加如下成员变量
	private static final UMSocialService sController = UMServiceFactory
			.getUMSocialService("com.umeng.share", RequestType.SOCIAL);

	private static boolean sHaveInitailed = false;

	private static boolean sQQInitialed = false;

	// private static final String IMAGE_URL = HttpUrl.PREFIX
	// + "images/share/sharepic2.png";

	private static int IMAGE_URL = R.drawable.ic_launcher;

	private static String sQQImgUrl;

	public static void initSharedPane(Activity context) {
		if (sHaveInitailed == true) {
			return;
		}

		sharePlatOrder(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
				SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA,
				SHARE_MEDIA.TENCENT, SHARE_MEDIA.SMS);
		sHaveInitailed = true;
	}

	private static void sharePlatOrder(SHARE_MEDIA... medias) {
		sController.getConfig().setPlatforms(medias);
		sController.getConfig().setPlatformOrder(medias);
	}

	/**
	 * 
	 * @todo TODO
	 * @time 2014年5月28日 下午5:28:01
	 * @author jie.liu
	 * @param context
	 * @param content
	 *            是由一段文字加一个url组成的
	 */
	public static void openShare(Activity context, String productName,
			String content, String imgUrl) {
		sQQImgUrl = imgUrl;
		int index = content.indexOf("http");

		String textContent = getTextContent(content, index);
		// 跳转链接
		String urlContent = getUrlContent(content, index);

		initWeiXinAndQQ(context, productName, textContent, urlContent);
		initSinaWB(context, content, urlContent);
		initTencentWB(context, content, urlContent);
		initSMS(context, content);

		// 设置分享图片, 参数2为图片的url地址
		// sController.setShareMedia(new UMImage(context, IMAGE_URL));
		// 设置分享内容
		sController.setShareContent(content);
		// 关闭微博定位功能
		sController.getConfig().setDefaultShareLocation(false);
		// 是否只有已登录用户才能打开分享选择页
		sController.openShare(context, false);
	}

	private static String getTextContent(String content, int index) {
		String textContent = null;
		// 取到链接钱的内容
		if (index != -1) {
			textContent = content.substring(0, index);
		} else {
			textContent = content;
		}
		return textContent;
	}

	private static String getUrlContent(String content, int index) {
		String urlContent = null;
		if (index != -1) {
			urlContent = content.substring(index);
		}
		return urlContent;
	}

	private static void initWeiXinAndQQ(Activity context, String title,
			String textContent, String urlContent) {
		String appID = context.getResources().getString(R.string.weixin_app_id);
		// 添加微信平台，参数1为当前Activity, 参数2为用户申请的AppID, 参数3为点击分享内容跳转到的目标url
		UMWXHandler wxHandler = sController.getConfig().supportWXPlatform(
				context, appID, urlContent);
		// 设置分享标题
		wxHandler.setWXTitle(title);
		// 支持微信朋友圈, 朋友圈只显示title，不显示分享的内容
		UMWXHandler circleHandler = sController.getConfig()
				.supportWXCirclePlatform(context, appID, urlContent);
		circleHandler.setCircleTitle(textContent);

		initQQ(context, title, textContent, urlContent);
	}

	/**
	 * 
	 * @todo 初始化QQ平台分享 QQ分享内容为音乐，视频的时候，其形式必须为url;图片支持url和本地图片类型。
	 *       为了防止QQ授权出现不可预测的问题
	 *       ，请保证添加QQ平台只执行一次（只调用一次mController.getConfig().supportQQPlatform）！
	 * @time 2014年6月6日 上午9:19:40
	 * @author liuzenglong163@gmail.com
	 * @param context
	 * @param content
	 */
	private static void initQQ(Activity context, String title, String content,
			String url) {
		String appId = context.getResources().getString(R.string.qq_app_id);
		if (sQQInitialed == false) {
			sController.getConfig().supportQQPlatform(context, appId, url);
		}
		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent.setShareContent(content);
		qqShareContent.setTitle(title);
		qqShareContent.setShareImage(new UMImage(context, sQQImgUrl));
		qqShareContent.setTargetUrl(url);
		sController.setShareMedia(qqShareContent);
		sQQInitialed = true;
		initQZone(context, title, content, url, appId);
	}

	/**
	 * 
	 * @todo QQ空间分享
	 * @time 2014年6月6日 上午11:05:58
	 * @author liuzenglong163@gmail.com
	 * @param context
	 * @param content
	 * @param url
	 * @param appId
	 */
	private static void initQZone(Activity context, String title,
			String content, String url, String appId) {
		QZoneShareContent qzoneShareContent = new QZoneShareContent();
		qzoneShareContent.setShareContent(content);
		qzoneShareContent.setTitle(title);
		// qzoneShareContent.setShareImage(new UMImage(context, IMAGE_URL));
		qzoneShareContent.setTargetUrl(url);
		sController.getConfig().setSsoHandler(
				new QZoneSsoHandler(context, appId));
		sController.setShareMedia(qzoneShareContent);
	}

	private static void initSinaWB(Activity context, String content, String url) {
		SinaShareContent sinaShareContent = new SinaShareContent();
		sinaShareContent.setShareContent(content);
		// sinaShareContent.setShareImage(new UMImage(context, IMAGE_URL));
		sinaShareContent.setTargetUrl(url);
		sController.setShareMedia(sinaShareContent);
		// sController.getConfig().setSinaSsoHandler(new SinaSsoHandler());
	}

	private static void initTencentWB(Activity context, String content,
			String url) {
		TencentWbShareContent tencentShareContent = new TencentWbShareContent();
		tencentShareContent.setShareContent(content);
		// tencentShareContent.setShareImage(new UMImage(context, IMAGE_URL));
		tencentShareContent.setTargetUrl(url);
		sController.setShareMedia(tencentShareContent);
		sController.getConfig().setTencentWBSsoHandler(
				new TencentWBSsoHandler());
	}

	/**
	 * 
	 * @todo 短信分享
	 * @time 2014年6月6日 上午11:05:46
	 * @author liuzenglong163@gmail.com
	 * @param context
	 * @param content
	 */
	private static void initSMS(Activity context, String content) {
		SmsShareContent smsShareContent = new SmsShareContent();
		smsShareContent.setShareContent(content);
		sController.setShareMedia(smsShareContent);
	}
}
