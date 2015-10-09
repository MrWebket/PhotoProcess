package com.shenghuoli.library.http;

/**
 * 监听接口,监听异步任务是否完成
 * @author Hope
 */
public interface OnHttpListener {

    /**
     * 异步任务结束之后传回数据
     * @param response  任何类型的数据 <p>
     * <b>Note:</b>具体业务模块具体做解析 
     * @param responseCode 服务端返回编号
     * @param requestCode 请求Code <p>
     * <b>Note:</b>仅用于标记跟踪
     */
    public void onResponse(Object response, int responseCode, int requestCode);
    
    /**
     * 网络请求失败回调
     * @param msg 错误提示语
     * @param responseCode 服务器Code
     * @param requestCode 请求Code
     */
    public void onFailure(String msg, int responseCode, int requestCode);
}
