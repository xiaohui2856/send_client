package com.rtl.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.SimpleLayout;

/**
 * httpclient 客户端，以json格式上传信息给server
 * 
 * @author xhlin 2015年7月23日
 */
public class Upload {

	private static Logger logger = Logger.getLogger(Upload.class);

	// private String propFile="/info.properties";
	// private static String serverURL =
	// "http://172.16.6.69/mis/reportImportDataSlt";

	private static String url;

	// 需要传输的3个参数
	private static String licenseKey;
	private static String dataType;
	private static String dataVersion;
	// private String datafile;

	private static String filePath; // 本机文件存放的路径

	private static String logDir; // 本机文件存放的路径

	public static int i = 3;

	/**
	 * 读取配置文件
	 */
	public void readInfo(String propFile) {
		Properties p = new Properties();
		try {
			InputStream in = new FileInputStream(propFile);
			p.load(in);
			in.close();
			if (p.containsKey("url")) {
				url = p.getProperty("url");
			}
			if (p.containsKey("licenseKey")) {
				licenseKey = p.getProperty("licenseKey");
			}
			if (p.containsKey("logDir")) {
				logDir = p.getProperty("logDir");
			}
			// if(p.containsKey("dataType")){
			// dataType = p.getProperty("dataType");
			// System.out.println("dataType="+dataType);
			// }
			// if(p.containsKey("dataVersion")){
			// dataVersion = p.getProperty("dataVersion");
			// System.out.println("dataVersion="+dataVersion);
			// }
			// if(p.containsKey("datafile")){
			// datafile = p.getProperty("datafile");
			// System.out.println("datafile="+datafile);
			// }
			// if(p.containsKey("filePath")){
			// filePath = p.getProperty("filePath");
			// System.out.println("filePath="+filePath);
			// }
		} catch (IOException ex) {
			// System.out.println(ex.getMessage());
			logger.error(ex.getMessage());
		}
	}

	private static String send(String message, InputStream fileIn, String url)
			throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(100000).setConnectTimeout(100000).build();// 设置请求和传输超时时间
		post.setConfig(requestConfig);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setCharset(Charset.forName("UTF-8"));// 设置编码
		ContentType contentType = ContentType.create("text/html", "UTF-8");
		builder.addPart("reqParam", new StringBody(message, contentType));
		builder.addPart("version", new StringBody("1.0", contentType));
		builder.addPart("dataFile", new InputStreamBody(fileIn, "file"));
		post.setEntity(builder.build());
		CloseableHttpResponse response = client.execute(post);
		InputStream inputStream = null;
		String responseStr = "", sCurrentLine = "";
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			inputStream = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "UTF-8"));
			while ((sCurrentLine = reader.readLine()) != null) {
				responseStr = responseStr + sCurrentLine;
			}
			return responseStr;
		}
		return null;
	}

	private String str2Json(Map<String, String> map) {
		StringBuffer json = new StringBuffer();
		json.append("{");
		Set<String> keys = map.keySet();
		int size = keys.size();
		int i = 1;
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			json.append("\"" + key + "\"");
			json.append(":" + "\"" + map.get(key) + "\"");
			if (i < size) {
				json.append(",");
				i++;
			}
		}
		json.append("}");
		return json.toString();
	}

	/*
	 * public static void main(String[] args) { Map<String, String> map=new
	 * HashMap<String, String>(); map.put("datatype", "1");
	 * map.put("dataVersion", "2"); map.put("datafile", "3");
	 * 
	 * Upload upload=new Upload(); String jsonString=upload.str2Json(map);
	 * System.out.println(jsonString); }
	 */

	private String getJson() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("dataType", dataType);
		map.put("dataVersion", dataVersion);
		map.put("licenseKey", licenseKey);

		Upload upload = new Upload();
		String jsonString = upload.str2Json(map);
		// System.out.println("json:"+jsonString);
		logger.info("json:" + jsonString);
		return jsonString;
	}

	public static void main(String[] args) throws Exception {

		if (args.length != 4) {
			// 例如：Jxj001 20150603 d:\rpt_rtl_0001.txt d:\confDir
			logger.error("传人的参数不是4个,应该分别为dataType，dataVersion，filePath，confDir");
			// System.out.println("传人的参数不是3个,应该分别为dataType，dataVersion，filePath");
			return;
		} else {
			Upload upload = new Upload();
			upload.readInfo(args[3]);

			Date date = new Date();
			DateFormat format1 = new SimpleDateFormat("yyyyMMdd");
			String todayDir = format1.format(date);

			File dir = new File(logDir + File.separator + todayDir);
			if (dir != null && !dir.exists()) {
				dir.mkdirs();
			}

//			SimpleLayout layout = new SimpleLayout();
			PatternLayout layout = new PatternLayout("%d %-5p %c - %m%n");

			FileAppender appender = null;

			try {

			appender = new FileAppender(layout,dir.getPath()+ File.separator+todayDir+ "client.log",true);

			} catch(Exception e) {
				logger.error("创建日志文件失败" );
			}

			logger.addAppender(appender);
			logger.setLevel(Level.INFO);

			InputStream fileIn;
			dataType = args[0];
			dataVersion = args[1];
			filePath = args[2];

			logger.info("dataType=" + dataType);
			logger.info("dataVersion=" + dataVersion);
			logger.info("filePath=" + filePath);

			try {
				File file = new File(filePath);
				if (!file.exists()) {
					// System.out.println("文件找不到，路径为:"+filePath);
					logger.error("文件找不到，路径为:" + filePath);
					return;
				}
				fileIn = new FileInputStream(filePath);
				String responseStr = send(upload.getJson(), fileIn, url);
				if (responseStr != null) {
					String[] values = responseStr.split(",");
					if ("ok".equals(values[0])) {
						System.out.println("0");
						logger.info("执行ok");
					} else {
						System.out.println("1");
						logger.info("执行失败 A：" + values[1]);
					}
				}else{
					System.out.println("1");
					logger.info("执行失败 B：服务端地址可能不通");
				}

			} catch (Exception e) {
				System.out.println("1");
				logger.error("执行失败 C：" + e.getMessage());
			}
		}

		logger.info("执行结束！");
		// System.out.println("执行结束！");

	}

}
