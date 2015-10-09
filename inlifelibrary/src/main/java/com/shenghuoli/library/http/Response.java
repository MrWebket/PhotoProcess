
package com.shenghuoli.library.http;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

/**
 * 请求的model<p>
 * Note:请求的实体data部分自己在外部解析
 * 
 * @author Hope
 */
public class Response {
    
    /** 是否是正确的返回 */
    public boolean isResponseOk = false;
    
    /**
     * 是否已经处理<p>
     * 主要是让客户端实现抽象类的可以可以选择是否不回传回调
     */
    public boolean isWorked = false;

    /**
     * 成功的返回
     */
    public static final int CODE_SUCCESS = 200;
    
    /**
     * 失败的返回-自定义
     */
    public static final int CODE_FAIL = -1;
    
    /**
     * 默认的请求编号
     */
    public static final int REQUEST_CODE_DEFAULT = -0x865198;
    
    /** 默认的请求类型 */
    public static final int REQUEST_TYPE_DEFAULT = 0x0;
    
    /** 上传文件 */
    public static final int REQUEST_TYPE_UPLOAD = 0x1;
    
    public int statusCode;  //服务端返回code

    public int httpMethod = HttpMethod.GET; //网络请求类型
    
    public int requestCode = REQUEST_CODE_DEFAULT; // 记录请求编号
    
    public String requestUrl; //网络请求地址
    
    public HttpEntity entity;  //网络请求参数
    
    public int responseCode; // 返回code
    
    public String error;
    
    public String data;
    
    public Map<String, String> headers;
    
    public HttpResponse httpResponse;
    
    public int requestType = REQUEST_TYPE_DEFAULT;
    
    public Class<?> cls;  //需要解析成的对象
    
    public Object objData;
    
    public int reConnectionCount = 0;  //重连接次数
    
    public Response() {
        
    }

    public Response(String error, String data, int requestCode, int responseCode) {
        this.error = error;
        this.data = data;
        this.requestCode = requestCode;
        this.responseCode = responseCode;
    }
}
