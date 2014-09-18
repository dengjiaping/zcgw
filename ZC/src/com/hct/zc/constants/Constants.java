package com.hct.zc.constants;

import java.io.File;

import com.hct.zc.utils.SDCardUtil;

public interface Constants {

	String PREFERENCE_FILE = "zc_preference";

	String ZC_ROOT_DIR = "ZCAdisor/";

	String WEB_DIR = SDCardUtil.getSDPath() + File.separator + ZC_ROOT_DIR
			+ "Trust";

}
