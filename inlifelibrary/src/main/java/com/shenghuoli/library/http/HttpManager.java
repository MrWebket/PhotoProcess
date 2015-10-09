package com.shenghuoli.library.http;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shenghuoli.library.utils.AsyncTaskEx;
import com.shenghuoli.library.utils.LogUtil;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * http交互请求类
 * 
 * @author Hope
 */
public abstract class HttpManager {
    private static final String ERROR_PARSE = "无法解析数据";
    private static final String ERROR_NOT_NETWORK = "网络出问题了";
    private static final String ERROR_FAIL_CONNECTION = "网络出问题了";
    private static final String ERROR_OTHER = "出现错误了";
    private static final String TAG = "HttpManager";

    private OnHttpListener listener;
    
    /** 请求头 */
    private Map<String, String> mHeaders = null;

    private int requestCode = Response.REQUEST_CODE_DEFAULT; // 标记数据返回tag
    
    /**
     * 给网络请求附加一个监听
     * @param listener 网络请求回调
     */
    public HttpManager(OnHttpListener listener) {
        this.listener = listener;
    }

    /**
     * 执行异步请求
     * 
     * @param rawPath 相对url路径
     * @param cls 请求返回实体
     * @param urlVariables 请求参数
     */
    public final Response doGet(String rawPath, Class<?> cls, Object... urlVariables) {
        rawPath = buildURL(rawPath, urlVariables);
        return executeAsync(HttpMethod.GET, rawPath, cls, null);
    }

    /**
     * 执行异步请求
     * @param rawPath 相对url路径
     * @param body 请求body
     */
    public final Response doPost(String rawPath, Class<?> cls, List<NameValuePair> params) {
        HttpEntity entity = null;
        if(params != null){
            try {
                entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            } catch (UnsupportedEncodingException e) {}
        }
        
        return executeAsync(HttpMethod.POST, rawPath, cls, entity);
    }
    
    /**
     * 执行异步请求
     * @param rawPath 相对url路径
     * @param cls 解析class
     * @param params 请求参数
     */
    public final Response doPost(String rawPath, Class<?> cls, JSONObject params) {
        HttpEntity entity = null;
        if(params != null){
            try {
                entity = new StringEntity(params.toString(), HTTP.UTF_8);
            } catch (UnsupportedEncodingException e) {}
        }
        
        return executeAsync(HttpMethod.POST, rawPath, cls, entity);
    }
    
    /**
     * 上传
     * @param rawPath
     * @param filePath
     * @param newName
     */
    public final void doUpload(String rawPath, Class<?> cls, String filePath, String newName, Object... urlVariables){
        rawPath = buildURL(rawPath, urlVariables);
        new HttpUploadTask().execute(rawPath, filePath, newName, requestCode);
        requestCode = Response.REQUEST_CODE_DEFAULT; // 使用完tag每次默认恢复
    }
    
    /**
     * 执行异步请求 文件上传
     * @param rawPath 相对url路径
     * @param mult 请求body
     * @param cls 请求返回实体 如果不想内部帮忙解析 直接传入null
     */
    public final Response doUpload(String rawPath, Class<?> cls, MultipartEntity mult){
        Response response = new Response();
        response.httpMethod = HttpMethod.POST;
        response.requestCode = requestCode;
        response.requestUrl = rawPath;
        response.cls = cls;
        response.entity = mult;
        response.requestType = Response.REQUEST_TYPE_UPLOAD;
        doRequest(response);
        
        return response;
    }

    /**
     * 执行异步请求
     *
     * @param method 请求方式
     * @param rawPath 请求url
     * @param cls 解析 class
     * @param entity 请求Entity
     * @return
     */
    private Response executeAsync(int method, String rawPath, Class<?> cls, HttpEntity entity) {
        LogUtil.error(getClass(), rawPath);
        
        Response response = new Response();
        response.httpMethod = method;
        response.requestCode = requestCode;
        response.requestUrl = rawPath;
        response.cls = cls;
        response.entity = entity;
        doRequest(response);
        
        return response;
    }
    
    /**
     * 执行异步请求
     * @param response 请求实体
     */
    public void doRequest(Response response){
        doRequest(response, response.requestCode);
    }
    
    /**
     * 执行异步请求
     * @param response 请求实体
     * @param requestCode 请求标志
     */
    public void doRequest(Response response, int requestCode){
        response.requestCode = requestCode;
        response.headers = mHeaders;
        new HttpTask().execute(response);
        requestCode = Response.REQUEST_CODE_DEFAULT; // 使用完code每次默认恢复
    }
    
    private class HttpTask extends AsyncTaskEx<Response, Void, Response> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Response doInBackground(Response... params) {
            Response response = params[0];
            
            try {
                switch (response.requestType) {
                    case Response.REQUEST_TYPE_DEFAULT:
                        response = Server.request(response);
                        break;
                    case Response.REQUEST_TYPE_UPLOAD:
                        response = Server.requestUpload(response);  //这里请求头还没支持
                        break;
                }
                
                response = onResponse(response);
            } catch (JSONException e) {
                response.error = ERROR_PARSE;
            } catch (HttpHostConnectException e) {
                response.error = ERROR_NOT_NETWORK;
            } catch (IOException e) {
                response.error = ERROR_FAIL_CONNECTION;
            } catch (RuntimeException e) {
                response.error = ERROR_OTHER;
            } catch (Exception e) {
                response.error = ERROR_OTHER;
            }
            
            return response;
        }

        @Override
        protected void onPostExecute(Response result) {
            super.onPostExecute(result);
            callBack(result);
        }
    }
    
    /**
     * 数据解析
     * @param response
     * @return
     */
    private Response onResponse(Response response){
        LogUtil.info(getClass(), response.data);
        
        if(response.statusCode == Response.CODE_SUCCESS){
            //对于Response进行二次解析
            response = parseResponse(response);
        }
        
        //说明没有错误  那么就去解析对象去
        if(response.error == null){
            if(response.cls != null){
                Gson gson = new GsonBuilder().create();
                response.objData = gson.fromJson(response.data, response.cls);
            }else{
                response.objData = response.data;
            }
        }
        
        return response;
    }
    
    /**
     * 返回具体
     * @param result
     */
    private void callBack(Response result){
        if (listener != null && !result.isWorked) {
            if(result.isResponseOk){
                listener.onResponse(result.objData, result.responseCode, result.requestCode);
            }else{
                if (result.error != null) {
                    LogUtil.error(getClass(), result.error);
                }
                
                listener.onFailure(result.error, result.responseCode, result.requestCode);
            }
        }
    }
    
    /**
     * 对于Response进行二次处理
     * @param data
     * @return 状态码 OK 200
     */
    public abstract Response parseResponse(Response response);
    
    private class HttpUploadTask extends AsyncTaskEx<Object, Void, Response> {
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Response doInBackground(Object... params) {
            Response response = new Response();
            response.requestUrl = (String)params[0];
            String filePath = (String)params[1];
            String newName = (String)params[2];
            response.requestCode = (Integer)params[3];
            
            try {
                response.data = Server.requestUpload(response.requestUrl, filePath, newName);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            
            response = onResponse(response);
            
            return response;
        }

        @Override
        protected void onPostExecute(Response result) {
            super.onPostExecute(result);
            callBack(result);
        }
    }
    
    /**
     * 构建一个新的http url
     * 
     * @param path
     * @param urlVariables
     * @return
     */
    public String buildURL(String path, Object... urlVariables) {
        if (urlVariables != null) {
            UriTemplate uriTemplate = new UriTemplate(path);
            URI expanded = uriTemplate.expand(urlVariables);

            return expanded.toString();
        }

        return path;
    }
    
    /**
     * 添加请求头
     * 
     * @param key
     * @param value
     */
    public void addHeader(String key, String value){
        if(mHeaders == null){
            mHeaders = new HashMap<String, String>();
        }
        
        mHeaders.put(key, value);
    }

    /**
     * 为本次的网络请求设置一个标记
     */
    public void addRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    /**
     * 设置网络请求回调监听
     */
    public void setListener(OnHttpListener listener) {
        this.listener = listener;
    }
}
