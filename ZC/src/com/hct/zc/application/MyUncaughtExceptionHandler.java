package com.hct.zc.application;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * @todo 异常处理
 * @time 2014-6-3 下午4:04:56
 * @author liuzenglong163@gmail.com
 */

public class MyUncaughtExceptionHandler implements UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO 写入Log，上传到服务器

	}
}
