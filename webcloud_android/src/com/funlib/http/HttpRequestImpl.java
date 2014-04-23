package com.funlib.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;

import com.funlib.http.upload.CountMultipartEntity;
import com.funlib.http.upload.CountMultipartEntity.ProgressListener;
import com.funlib.log.Log;
import com.funlib.utily.FlowController;

/**
 * httpclient请求。
 * 1.支持get,post两种请求方式
 * 2.支持普通请求响应字符串数据，上传文件、流，下载文件
 * 3.支持向请求头和url中追加sessionid信息，如果sessionid被缓存存在的情况下
 * 
 * 注意：在处理文件下载时，在使用完流对象后要注意调用HttpEntity.consumeEntity()方法，否则请求被阻塞
 * 
 * @author zoubangyue
 * @version [版本号, 2012-2-14]
 */
public class HttpRequestImpl implements HttpRequest {
    
    private static String TAG = "HttpRequest";
        
    private static DefaultHttpClient httpClient;
    
    private HttpEntity httpEntity;
    
    private HttpResponse httpResp;
    
    private HttpGet httpGet;
    
    private HttpPost httpPost;
    
    private StatusLine status;
    
    private Context ctx;
    
    private ReturnData data = new ReturnData();;
    
    static {
        httpClient = HttpClientFactory.getHttpClient();
    }
    
    public HttpRequestImpl(Context ctx) {
        this.ctx = ctx;
    }
    
    /**
     * get请求，获取字符串数据。
     * 
     * @param url
     *            地址
     * @param params
     *            请求参数
     * @return
     */
    @Override
    public ReturnData doGetRequest(String url, Map<String, String> params) {
        try {
            //url = SessionCenter.addSessionToUrl(url);
            httpGet = new HttpGet(url + "?" + HttpUtils.getParamData(params));
            SessionCenter.putSessionToHeader(url, httpGet);
            httpResp = httpClient.execute(httpGet);
            status = httpResp.getStatusLine();
            data.httpStatus = status.getStatusCode();
            data.cacheHeader = SessionCenter.parserCacheHeader(httpResp);
            int sc = status.getStatusCode();
            if (sc == HttpStatus.SC_OK) {
                httpEntity = httpResp.getEntity();
                String charSet = EntityUtils.getContentCharSet(httpEntity);
                data.content = EntityUtils.toString(httpEntity, charSet);
                data.status = ReturnData.SC_OK;
                data.contentLength = httpEntity.getContentLength();
                SessionCenter.parserJSESSIONID(httpResp);
                Log.d(TAG, "200状态，status=" + sc);
            } else if (sc >= 300 && sc < 400) {
                data.status = ReturnData.FAIL;
                Log.e(TAG, "300状态，status=" + sc);
            } else if (sc >= 400 && sc < 500) {
                data.status = ReturnData.SC_CLIENT_ERROR;
                Log.e(TAG, "400状态，status=" + sc);
            } else if (sc >= 500) {
                data.status = ReturnData.SC_SERVER_ERROR;
                Log.e(TAG, "500状态，status=" + sc);
            } else {
                data.status = ReturnData.FAIL;
                Log.e(TAG, "其他状态，status" + sc);
            }
        } catch (Exception e) {
            Log.e(TAG, "请求数据失败," + url, e);
            data.status = ReturnData.FAIL;
        } finally {
            if (data.status == ReturnData.SC_OK)
                FlowController.count(FlowController.FLOW_UP, data.contentLength);
        }
        return data;
    }
    
    /**
     * post请求，获取字符串数据。
     * 
     * @param url
     *            地址
     * @param params
     *            请求参数
     * @return
     */
    @Override
    public ReturnData doPostRequest(String url, Map<String, String> params) {
        try {
            Log.d(TAG, "url= " + url + " --- params=" + params);
        	//url = SessionCenter.addSessionToUrl(url);
            List<NameValuePair> vp = HttpUtils.getNameValuePair(params);
            SessionCenter.putSessionToHeader(url, httpPost);
            httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(vp, HTTP.UTF_8));
            httpResp = httpClient.execute(httpPost);
            status = httpResp.getStatusLine();
            data.httpStatus = status.getStatusCode();
            data.cacheHeader = SessionCenter.parserCacheHeader(httpResp);
            int sc = status.getStatusCode();
            if (sc == HttpStatus.SC_OK) {
                httpEntity = httpResp.getEntity();
                String charSet = EntityUtils.getContentCharSet(httpEntity);
                data.content = EntityUtils.toString(httpEntity, charSet);
                data.status = ReturnData.SC_OK;
                data.contentLength = httpEntity.getContentLength();
                httpEntity.consumeContent();
                SessionCenter.parserJSESSIONID(httpResp);
                Log.d(TAG, "200状态，status=" + sc);
            } else if (sc >= 300 && sc < 400) {
                data.status = ReturnData.FAIL;
                Log.e(TAG, "300状态，status=" + sc);
            } else if (sc >= 400 && sc < 500) {
                data.status = ReturnData.SC_CLIENT_ERROR;
                Log.e(TAG, "400状态，status=" + sc);
            } else if (sc >= 500) {
                data.status = ReturnData.SC_SERVER_ERROR;
                Log.e(TAG, "500状态，status=" + sc);
            } else {
                data.status = ReturnData.FAIL;
                Log.e(TAG, "其他状态，status" + sc);
            }
        } catch (Exception e) {
            Log.e(TAG, "请求数据失败," + url, e);
            data.status = ReturnData.FAIL;
        } finally {
            if (data.status == ReturnData.SC_OK)
                FlowController.count(FlowController.FLOW_UP, data.contentLength);
            try {
                //请求后立即释放资源
                httpPost.abort();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }
    
    /**
     * post请求,上传文件列表，并获取字符串数据。
     * 
     * @param url
     *            地址
     * @param map
     *            请求参数
     * @return
     */
    @Override
    public ReturnData doPostUploadFileRequest(String url, Map<String, String> params, ProgressListener listener,
        File[] files, String[] fileNames) {
        try {
            //url = SessionCenter.addSessionToUrl(url);
            httpPost = new HttpPost(url);
            SessionCenter.putSessionToHeader(url, httpPost);
            CountMultipartEntity entity =
                new CountMultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, "*****", Charset.forName(HTTP.UTF_8),
                    listener);
            Set<String> keys = params.keySet();
            for (String key : keys) {
                StringBody paramValue = new StringBody(params.get(key), Charset.forName(HTTP.UTF_8));
                entity.addPart(key, paramValue);
            }
            int size = 0;
            for (File file : files) {
                FileBody fileValue = new FileBody(file);
                if (size < 1) {
                    entity.addPart("file", fileValue);
                } else
                    entity.addPart("file" + size, fileValue);
                size++;
            }
            httpPost.setEntity(entity);
            httpResp = httpClient.execute(httpPost);
            status = httpResp.getStatusLine();
            data.httpStatus = status.getStatusCode();
            data.cacheHeader = SessionCenter.parserCacheHeader(httpResp);
            int sc = status.getStatusCode();
            if (sc == HttpStatus.SC_OK) {
                httpEntity = httpResp.getEntity();
                String charSet = EntityUtils.getContentCharSet(httpEntity);
                data.content = EntityUtils.toString(httpEntity, charSet);
                data.status = ReturnData.SC_OK;
                data.contentLength = httpEntity.getContentLength();
                SessionCenter.parserJSESSIONID(httpResp);
                httpEntity.consumeContent();
                Log.d(TAG, "200状态，status=" + sc);
            } else if (sc >= 300 && sc < 400) {
                data.status = ReturnData.FAIL;
                Log.e(TAG, "300状态，status=" + sc);
            } else if (sc >= 400 && sc < 500) {
                data.status = ReturnData.SC_CLIENT_ERROR;
                Log.e(TAG, "400状态，status=" + sc);
            } else if (sc >= 500) {
                data.status = ReturnData.SC_SERVER_ERROR;
                Log.e(TAG, "500状态，status=" + sc);
            } else {
                data.status = ReturnData.FAIL;
                Log.e(TAG, "其他状态，status" + sc);
            }
        } catch (Exception e) {
            data.status = ReturnData.FAIL;
            Log.e(TAG, "请求数据失败," + url, e);
            //e.printStackTrace();
        } finally {
            if (data.status == ReturnData.SC_OK)
                FlowController.count(FlowController.FLOW_UP, data.contentLength);
        }
        return data;
    }
    
    /**
     * post请求,上传流列表，并获取字符串数据。
     * 
     * @param url
     *            地址
     * @param map
     *            请求参数
     * @return
     */
    @Override
    public ReturnData doPostUploadStreamRequest(String url, Map<String, String> params, ProgressListener listener,
        InputStream[] iss, String[] fileNames) {
        try {
            url = SessionCenter.addSessionToUrl(url);
            httpPost = new HttpPost(url);
            SessionCenter.putSessionToHeader(url, httpPost);
            MultipartEntity entity =
                new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, "*****", Charset.forName(HTTP.UTF_8));
            Set<String> keys = params.keySet();
            for (String key : keys) {
                StringBody paramValue = new StringBody(params.get(key), Charset.forName(HTTP.UTF_8));
                entity.addPart(key, paramValue);
            }
            int size = 0;
            for (InputStream is : iss) {
                InputStreamBody isb = new InputStreamBody(is, fileNames[size]);
                if (size < 1) {
                    entity.addPart("file", isb);
                } else
                    entity.addPart("file" + size, isb);
                size++;
            }
            httpPost.setEntity(entity);
            httpResp = httpClient.execute(httpPost);
            status = httpResp.getStatusLine();
            data.httpStatus = status.getStatusCode();
            data.cacheHeader = SessionCenter.parserCacheHeader(httpResp);
            int sc = status.getStatusCode();
            if (sc == HttpStatus.SC_OK) {
                httpEntity = httpResp.getEntity();
                String charSet = EntityUtils.getContentCharSet(httpEntity);
                data.content = EntityUtils.toString(httpEntity, charSet);
                data.status = ReturnData.SC_OK;
                data.contentLength = httpEntity.getContentLength();
                SessionCenter.parserJSESSIONID(httpResp);
                httpEntity.consumeContent();
                Log.d(TAG, "200状态，status=" + sc);
            } else if (sc >= 300 && sc < 400) {
                data.status = ReturnData.FAIL;
                Log.e(TAG, "300状态，status=" + sc);
            } else if (sc >= 400 && sc < 500) {
                data.status = ReturnData.SC_CLIENT_ERROR;
                Log.e(TAG, "400状态，status=" + sc);
            } else if (sc >= 500) {
                data.status = ReturnData.SC_SERVER_ERROR;
                Log.e(TAG, "500状态，status=" + sc);
            } else {
                data.status = ReturnData.FAIL;
                Log.e(TAG, "其他状态，status" + sc);
            }
        } catch (Exception e) {
            data.status = ReturnData.FAIL;
            Log.e(TAG, "请求数据失败," + url, e);
            //e.printStackTrace();
        } finally {
            if (data.status == ReturnData.SC_OK)
                FlowController.count(FlowController.FLOW_UP, data.contentLength);
        }
        return data;
    }
    
    /**
     * post请求，下载文件流。
     * 注意流处理完毕之后必须要调用consumeResponse方法。
     * @param url
     *            地址
     * @param params
     *            请求参数
     * @param dataType
     *            需要的响应数据类型
     * @return
     */
    @Override
    public ReturnData doDownloadRequest(String url, Map<String, String> params, int dataType) {
        try {
            //url = SessionCenter.addSessionToUrl(url);
            //List<NameValuePair> vp = HttpUtils.getNameValuePair(params);
            httpPost = new HttpPost(url);
            //httpPost.setEntity(new UrlEncodedFormEntity(vp, HTTP.UTF_8));
            //SessionCenter.putSessionToHeader(url, httpPost);
            httpResp = httpClient.execute(httpPost);
            httpEntity = httpResp.getEntity();
            status = httpResp.getStatusLine();
            data.httpStatus = status.getStatusCode();
            data.cacheHeader = SessionCenter.parserCacheHeader(httpResp);
            data.dataType = dataType;
            int sc = status.getStatusCode();
            if (sc == HttpStatus.SC_OK) {
                httpEntity = httpResp.getEntity();
                data.status = ReturnData.SC_OK;
                data.contentLength = httpEntity.getContentLength();
                data.entity = httpEntity;
                if (dataType == ReturnData.STRING) {
                    String charSet = EntityUtils.getContentCharSet(httpEntity);
                    data.content = EntityUtils.toString(httpEntity, charSet);
                    httpEntity.consumeContent();
                } else if (dataType == ReturnData.STREAM) {
                    data.is = httpEntity.getContent();
                    data.entity = httpEntity;
                } else if (dataType == ReturnData.BYTEARRAY) {
                    data.bytes = EntityUtils.toByteArray(httpEntity);
                    httpEntity.consumeContent();
                } else {
                    httpEntity.consumeContent();
                    throw new RuntimeException("传入数据类型不支持");
                }
                SessionCenter.parserJSESSIONID(httpResp);
                Log.d(TAG, "200状态，status=" + sc);
            } else if (sc >= 300 && sc < 400) {
                data.status = ReturnData.FAIL;
                Log.e(TAG, "300状态，status=" + sc);
            } else if (sc >= 400 && sc < 500) {
                data.status = ReturnData.SC_CLIENT_ERROR;
                Log.e(TAG, "400状态，status=" + sc);
            } else if (sc >= 500) {
                data.status = ReturnData.SC_SERVER_ERROR;
                Log.e(TAG, "500状态，status=" + sc);
            } else {
                data.status = ReturnData.FAIL;
                Log.e(TAG, "其他状态，status" + sc);
            }
        } catch (Exception e) {
            data.status = ReturnData.FAIL;
            Log.e(TAG, "请求数据失败," + url, e);
            //e.printStackTrace();
        } finally {
            if (data.status == ReturnData.SC_OK)
                FlowController.count(FlowController.FLOW_UP, data.contentLength);
        }
        return data;
    }
    
    /**
     * get请求，下载文件流。
     * 注意流处理完毕之后必须要调用consumeResponse方法。
     * @param url
     *            地址
     * @param params
     *            请求参数
     * @param dataType
     *            需要的响应数据类型
     * @return
     */
    @Override
    public ReturnData doGetDownloadRequest(String url, Map<String, String> params, int dataType) {
        try {
            //url = SessionCenter.addSessionToUrl(url);
            //List<NameValuePair> vp = HttpUtils.getNameValuePair(params);
            httpGet = new HttpGet(url);
            //httpPost.setEntity(new UrlEncodedFormEntity(vp, HTTP.UTF_8));
            //SessionCenter.putSessionToHeader(url, httpPost);
            httpResp = httpClient.execute(httpGet);
            httpEntity = httpResp.getEntity();
            status = httpResp.getStatusLine();
            data.httpStatus = status.getStatusCode();
            data.cacheHeader = SessionCenter.parserCacheHeader(httpResp);
            data.dataType = dataType;
            int sc = status.getStatusCode();
            if (sc == HttpStatus.SC_OK) {
                httpEntity = httpResp.getEntity();
                data.status = ReturnData.SC_OK;
                data.contentLength = httpEntity.getContentLength();
                data.entity = httpEntity;
                if (dataType == ReturnData.STRING) {
                    String charSet = EntityUtils.getContentCharSet(httpEntity);
                    data.content = EntityUtils.toString(httpEntity, charSet);
                    httpEntity.consumeContent();
                } else if (dataType == ReturnData.STREAM) {
                    data.is = httpEntity.getContent();
                    data.entity = httpEntity;
                } else if (dataType == ReturnData.BYTEARRAY) {
                    data.bytes = EntityUtils.toByteArray(httpEntity);
                    httpEntity.consumeContent();
                } else {
                    httpEntity.consumeContent();
                    throw new RuntimeException("传入数据类型不支持");
                }
                SessionCenter.parserJSESSIONID(httpResp);
                Log.d(TAG, "200状态，status=" + sc);
            } else if (sc >= 300 && sc < 400) {
                data.status = ReturnData.FAIL;
                Log.e(TAG, "300状态，status=" + sc);
            } else if (sc >= 400 && sc < 500) {
                data.status = ReturnData.SC_CLIENT_ERROR;
                Log.e(TAG, "400状态，status=" + sc);
            } else if (sc >= 500) {
                data.status = ReturnData.SC_SERVER_ERROR;
                Log.e(TAG, "500状态，status=" + sc);
            } else {
                data.status = ReturnData.FAIL;
                Log.e(TAG, "其他状态，status" + sc);
            }
        } catch (Exception e) {
            data.status = ReturnData.FAIL;
            Log.e(TAG, "请求数据失败," + url, e);
            //e.printStackTrace();
        } finally {
            if (data.status == ReturnData.SC_OK)
                FlowController.count(FlowController.FLOW_UP, data.contentLength);
        }
        return data;
    }
    
    @Override
    public void cancel() {
        if (httpPost != null) {
            httpPost.abort();
        }
        if (httpGet != null) {
            httpGet.abort();
        }
    }
    
    public void addHeader(String name, String value) {
        if (httpPost != null) {
            httpPost.addHeader(name, value);
        }
        if (httpGet != null) {
            httpPost.addHeader(name, value);
        }
    }
    
    public void consumeContent() {
        if (httpEntity != null) {
            try {
                httpEntity.consumeContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
