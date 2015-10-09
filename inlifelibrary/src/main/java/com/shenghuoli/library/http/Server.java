
package com.shenghuoli.library.http;

import com.shenghuoli.library.utils.LogUtil;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map.Entry;


/**
 * http请求辅助类
 * 
 * @author Hope
 */
class Server {
    private static final int CONNECTION_SO_TIMEOUT = 30 * 1000;
    private static final int CONNECTION_TIMEOUT = 15 * 1000; // 15秒超时
    
    private static final int CONNECTION_TIMEOUT_UPLOAD = 30000; // 30秒超时
    private static final int CONNECTION_SO_TIMEOUT_UPLOAD = 60000;

    private Server() {
    	
	}

    public static Response request(Response response) throws Exception {
        HttpResponse httpResponse = exchange(response);
        response.httpResponse = httpResponse;
        
        int responseCode = httpResponse.getStatusLine().getStatusCode();
        response.statusCode = responseCode;
        
        LogUtil.info(Server.class, "http response code " + responseCode);
        
        /* 若状态码为200 ok */
        if (response.statusCode == HttpStatus.SC_OK) {
        	Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding"); 
        	
        	String strResponse = null;
            /* 读返回数据 */
        	if (contentEncoding != null && "gzip".equalsIgnoreCase(contentEncoding.getValue())) { // 是否支持GZIP
        		strResponse = ZipUtil.uncompress(EntityUtils.toByteArray(httpResponse.getEntity()));
        	} else {
        		strResponse = EntityUtils.toString(httpResponse.getEntity(), "UTF_8");
        	}
            response.data = strResponse;
        } else {
            response.error = httpResponse.getStatusLine().getReasonPhrase();
        }
        
        return response;
    }
    
    /**
     * 上传文件
     */
    public static Response requestUpload(Response response) throws Exception {
        String result = null;

        HttpClient client = new DefaultHttpClient();

        InputStream is = null;
        HttpPost request = new HttpPost(response.requestUrl);
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT_UPLOAD);
        HttpConnectionParams.setSoTimeout(params, CONNECTION_SO_TIMEOUT_UPLOAD);
        request.setParams(params);

        try {
            request.setEntity(response.entity);
            HttpResponse httpResponse = client.execute(request);

            int responseCode = httpResponse.getStatusLine().getStatusCode();
            response.statusCode = responseCode;
            
            if (responseCode == HttpStatus.SC_OK) {
                is = httpResponse.getEntity().getContent();
                result = inStream2String(is);
            } else {
                response.error = httpResponse.getStatusLine().getReasonPhrase();
            }
        } catch (Exception e) {
            response.error = e.getMessage();
        }
        
        response.data = result;

        return response;
    }

    /**
     * 获取请求获取到的数据
     * 
     * @param response
     * @return
     * @throws HttpResponseException
     * @throws IOException
     */
    private static HttpResponse exchange(Response response) throws HttpResponseException, IOException {
        final String url = response.requestUrl;
        LogUtil.info(Server.class, url);
        
        HttpRequestBase httpRequest = null;
        switch (response.httpMethod) {
            case HttpMethod.GET:
                httpRequest = new HttpGet(url);
                break;
            case HttpMethod.POST:
                HttpPost httpPost = new HttpPost(url);

                /* 添加请求参数到请求对象 */
                if (response.entity != null) {
                    httpPost.setEntity(response.entity);
                }
                
                httpRequest = httpPost;
                break;
            case HttpMethod.PUT:
                HttpPut httpPut = new HttpPut(url);

                /* 添加请求参数到请求对象 */
                if (response.entity != null) {
                    httpPut.setEntity(response.entity);
                }
                
                httpRequest = httpPut;
                break;
            default:
                httpRequest = new HttpGet(response.requestUrl);
                break;
        }
        
        //添加请求头
        if(response.headers != null && response.headers.size() > 0){
            for (Entry<String, String> entry: response.headers.entrySet()) {
                httpRequest.addHeader(entry.getKey(), entry.getValue());
            }
        }
        
        // 取得HttpClient对象
        HttpClient httpclient = new DefaultHttpClient();

        HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), CONNECTION_TIMEOUT); // 设置连接超时
        HttpConnectionParams.setSoTimeout(httpclient.getParams(), CONNECTION_SO_TIMEOUT); // 设置获取超时

        return httpclient.execute(httpRequest);
    }

    /**
     * 上传文件
     */
    public static String requestUpload(String urlPath, String filePath, String newName) throws Exception {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        URL url = new URL(urlPath);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        /* 允许Input、Output，不使用Cache */
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        /* 设置传送的method=POST */
        con.setRequestMethod("POST");
        /* setRequestProperty */

        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");
        con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        /* 设置DataOutputStream */
        DataOutputStream ds = new DataOutputStream(con.getOutputStream());
        ds.writeBytes(twoHyphens + boundary + end);
        ds.writeBytes("Content-Disposition: form-data; " + "name=\"file1\";filename=\"" + newName
                + "\"" + end);
        ds.writeBytes(end);

        /* 取得文件的FileInputStream */
        FileInputStream fStream = new FileInputStream(filePath);
        /* 设置每次写入1024bytes */
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int length = -1;
        /* 从文件读取数据至缓冲区 */
        while ((length = fStream.read(buffer)) != -1) {
            /* 将资料写入DataOutputStream中 */
            ds.write(buffer, 0, length);
        }
        ds.writeBytes(end);
        ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

        /* close streams */
        fStream.close();
        ds.flush();

        /* 取得Response内容 */
        InputStream is = con.getInputStream();
        int ch;
        StringBuffer b = new StringBuffer();
        while ((ch = is.read()) != -1) {
            b.append((char) ch);
        }
        /* 关闭DataOutputStream */
        ds.close();
        return b.toString();
    }
    
    // 将输入流转换成字符串
    private static String inStream2String(InputStream is) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = -1;
        while ((len = is.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        return new String(baos.toByteArray(), "utf-8");
    }
}
