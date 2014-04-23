package com.funlib.http;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.apache.http.HttpVersion;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.params.CookieSpecPNames;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.funlib.log.Log;


/**
 * 客户端所有http请求共用一个httpclient。
 * 该DefaultHttpClient实例是线程安全的可以放心使用。
 * 
 * @author zoubangyue
 */
public final class HttpClientFactory {
	/**读取数据超时时间，30秒*/
	public static final int SO_TIMEOUT = 30 * 1000;
	/**建立连接超时时间，10秒*/
	public static final int CONNECTION_TIMEOUT = 10 * 1000;
    
    private HttpClientFactory() {
    }
    
    static {
        Log.d("HttpClientFactory", "创建httpClient");
        httpClient = createHttpClient();
    }
    
    private static DefaultHttpClient httpClient;
    
    /** 使用线程安全的http客户端管理器，管理来自不同的请求使用一个httpClient。
     * 注册客户端的协议，字符编码，请求端口等。
     * @return
     * @throws KeyStoreException 
     * @throws IOException 
     * @throws CertificateException 
     * @throws NoSuchAlgorithmException 
     * @throws UnrecoverableKeyException 
     * @throws KeyManagementException 
     */
    private static DefaultHttpClient createHttpClient() {
        HttpParams params = null;
        ClientConnectionManager connMgr = null;
        //HttpClientConnectionManager httpConnMgr = null;
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            
            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            //不对https任何认证地址进行拦截，其实这样做是不安全的，但是客户端没办法
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            
            params = new BasicHttpParams();
            
            ConnManagerParams.setTimeout(params, 1000);  
            ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(10));  
            ConnManagerParams.setMaxTotalConnections(params, 30); 
            
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            HttpProtocolParams.setHttpElementCharset(params, HTTP.UTF_8);
            HttpProtocolParams.setUseExpectContinue(params, true);
            HttpProtocolParams.setUserAgent(params, "android,lbs");
            HttpConnectionParams.setSocketBufferSize(params, 8192);
            params.setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
            params.setParameter(HttpConnectionParams.SO_TIMEOUT, SO_TIMEOUT);
            //设置cookie代理
            params.setParameter(ClientPNames.COOKIE_POLICY,CookiePolicy.BROWSER_COMPATIBILITY);
            //有太多的web站点是用CGI脚本去实现的，而导致只有将所有的Cookies都放入Request header才可以正常的工作。这种情况下最好设 置http.protocol.single-cookie-header参数为true。
            params.setParameter(CookieSpecPNames.SINGLE_COOKIE_HEADER, true);
            //old
            SchemeRegistry schReg = new SchemeRegistry();
            //支持的两种schme:http,https
            schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            //schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
            schReg.register(new Scheme("https", sf, 443));
            //支持线程安全
            connMgr = new ThreadSafeClientConnManager(params, schReg);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
        	e.printStackTrace();
        }
        if (connMgr != null && params != null)
            return new DefaultHttpClient(connMgr, params);
        else
            return new DefaultHttpClient(params);//此对象不是线程安全的
    }
    
    /** 关闭httpClient。
     */
    public static void shutdownHttpClient() {
        if (httpClient != null && httpClient.getConnectionManager() != null) {
            httpClient.getConnectionManager().shutdown();
        }
        httpClient = null;
    }
    
    public static DefaultHttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = createHttpClient();
            httpClient.setCookieStore(new BasicCookieStore());
        }
        return httpClient;
    }
}
