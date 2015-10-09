package com.shenghuoli.library.constants;

public class BaseConstants {
	//生厂模式
	public static final Boolean PRODUCTION_MODEL = true;
	//测试员测试模式  不统计数量 但是统计错误
	public static final Boolean DEBUG_MODEL = true;
	/** 模拟数据 */
	public static final Boolean DEBUG_TEST_DATA = true;
	
	/** 广播的tag 广播内部发起的http请求 不应该给提示 */
    public static final int BROADCAST_TAG = 0x2000;
    /** 广播相对地址 */
    public static final String BROADCASE_ADDRESS = ".broadcast";
    /** 广播意图key */
    public static final String BROADCASE_INTENT = "intent";
    /** 广播 进度条状态显示key */
    public static final String BROADCASE_TYPE_STATE = "state";
    /** 广播 进度条value */
    public static final int BROADCASE_INTENT_HTTP = 0x9000;
    /** 广播 push显示value */
    public static final int BROADCASE_INTENT_PUSH = 0x9001;
    /** 广播 退出value */
    public static final int BROADCASE_INTENT_EXIT = 0x9002;
    
    /**密码最小长度*/
    public static final int PWD_MIN_LEN = 6;
    /**密码最大长度*/
    public static final int PWD_MAX_LEN = 12;
    
}
