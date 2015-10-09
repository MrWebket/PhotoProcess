package com.hope.photoprocess.contants;

/**
 * 工程基础配置文件
 * 
 * @author Hope
 */
public class AppConfig {
	
	public static final long ONE_DAY_MILLISECOND = 1000*60*60*24;
	
	/**
	 * 数据库时间格式：yyyy-MM-dd HH:mm:ss
	 */
	public static final String DB_FORMAT_DATE = "yyyy-MM-dd HH:mm:ss";

	/** 是否是生厂模式 */
	public static final Boolean PRODUCTION_MODEL = true;

	/** 服务器网络ip端口地址 */
	public static final String IP_PORT = ""; // 开发环境

	/** 拍照保存的文件路径 */
	public static final String PHOTO_PATH = "PHOTO";
}
