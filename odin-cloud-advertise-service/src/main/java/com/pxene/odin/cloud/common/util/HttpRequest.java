package com.pxene.odin.cloud.common.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.util.EntityUtils;

public class HttpRequest {

	private static final String HTTP_ACCEPT = "accept";

	private static final String HTTP_ACCEPT_DEFAULT = "*/*";

	private static final String HTTP_ACCEPT_JSON = "application/json;charset=UTF-8";

	private static final String HTTP_USER_AGENT = "user-agent";

	private static final String HTTP_USER_AGENT_VAL = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)";

	private static final String HTTP_CONNECTION = "connection";

	private static final String HTTP_CONNECTION_VAL = "Keep-Alive";

	private static final String HTTP_CONTENT_TYPE = "Content-Type";

	/**
	 * 向指定URL发送GET方法的请求
	 *
	 * @param url	 发送请求的URL
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String sendGet(String url) {
		String result = "";
		BufferedReader in = null;
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty(HTTP_ACCEPT, HTTP_ACCEPT_DEFAULT);
			connection.setRequestProperty(HTTP_CONNECTION, HTTP_CONNECTION_VAL);
			connection.setRequestProperty(HTTP_USER_AGENT, HTTP_USER_AGENT_VAL);
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet()) {
				System.out.println(key + "--->" + map.get(key));
			}
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream(),"UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 *
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendPost(String url, String param) {
		CloseableHttpClient client = HttpClients.createDefault();
		try {
			HttpPost post = new HttpPost(url);
			post.setHeader(HTTP_CONTENT_TYPE, HTTP_ACCEPT_JSON);
			post.setEntity(new StringEntity(param, Charset.forName("UTF-8")));
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 *
	 * @param url
	 *            发送请求的 URL
	 * @param params
	 *            请求参数
	 * @return 所代表远程资源的响应结果
	 */

	public static String sendPost(String url, Map<String,String> params) {
		CloseableHttpClient client = HttpClients.createDefault();
		try {
			HttpPost post = new HttpPost(url);
			post.setHeader(HTTP_CONTENT_TYPE, "application/x-www-form-urlencoded");
			List<NameValuePair> formParams = new ArrayList<NameValuePair>(); //构建POST请求的表单参数
	        for(Map.Entry<String,String> entry : params.entrySet()){
	        	formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
	        }
			post.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 判断给定的ULR是否重定向了
	 * @param url
	 *         目标地址ULR
	 * @return
	 *         -1 ：URL无法访问
	 *          0 ：URL没有重定向
	 *          1 ：URL重定向了
	 */
     public static int checkAbsoluteUrl(String url){
         int reCode = 0;
         if(StringUtils.isBlank(url)){
            return reCode;
         }
         CloseableHttpClient httpclient = null;
         HttpGet httpGet = null;
         HttpContext httpContext = new BasicHttpContext();
         RequestConfig defaultRequestConfig = RequestConfig.custom()
                 .setSocketTimeout(10000).build();
         httpclient = HttpClients.createDefault();
         httpGet = new HttpGet(url);
         httpGet.setConfig(defaultRequestConfig);
         try {
             httpclient.execute(httpGet, httpContext);
         } catch (Exception e) {
             reCode = -1;
             return reCode;
         }
         HttpUriRequest req = (HttpUriRequest) httpContext
                 .getAttribute(HttpCoreContext.HTTP_REQUEST);
         HttpHost currentHost = (HttpHost) httpContext
                 .getAttribute(HttpCoreContext.HTTP_TARGET_HOST);

         String targetUrl = (req.getURI().isAbsolute()) ? req.getURI().toString() : (
                 currentHost.toURI() + req.getURI());
         if(targetUrl.endsWith("/")){
             targetUrl = targetUrl.substring(0,targetUrl.length()-1);
         }
         if(url.endsWith("/")){
             url = url.substring(0,url.length()-1);
         }
         if(!url.equals(targetUrl)){
             reCode = 1;
         }
         return reCode;
    }
}