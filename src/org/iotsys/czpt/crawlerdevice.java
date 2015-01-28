package org.iotsys.czpt;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.parser.*;
import org.jsoup.helper.*;
import org.jsoup.select.*;





/***
 * 
 * @author alex.liuli
 *
 */

import org.apache.commons.logging.Log;  
import org.apache.commons.logging.LogFactory;  

public class crawlerdevice {
	
	/*
	 * *********************************************************************
	 * ���Գ���
	 * *********************************************************************
	 */
	
	private static Log logger = LogFactory.getLog(crawlerdevice.class);
	 
	public static void main(String[] args) throws ParseException, IOException, InterruptedException {

		int MAX_RECNUM = 2000;

		/****
		 * 1.��ȡ ����ƽ̨�б�
		 */

		HttpClientContext mClientContext = HttpClientContext.create();

		CloseableHttpClient httpclient = HttpClients.createDefault();

		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(1000).setConnectTimeout(1000).build();


		
		HashMap<String,String> ObjMap = new HashMap<String,String>();
		
		HttpGet httpget1 = null;
		
		HttpResponse response1 = null;
		
		int iRecordNum =0;
		
		URI szSrcURI = null;
		
		// 
		int iStartNum = 1;
		
		
		String szLocalUA =new String ("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:35.0) Gecko/20100101 Firefox/35.0");
		
		
	/*******************************************************
	 * 3.��ȡ ����ն��б�
	 * 
	 */
	iStartNum =1;
	int iEndNum = 2000;
	iRecordNum =0; 
	System.out.println("begin .. grasp pages about  Car Terminal Detail:");
	for (int i = iStartNum; i < iEndNum; i++)
	{	
		String id = new Integer(i).toString();
	
		try {
			// http://www.ctis.cn/lwlk/terminal/query.action?rt=read&visitType=detail&entity.id=1083
				
			szSrcURI = new URIBuilder().setScheme("http")
					.setHost("www.ctis.cn")
					.setPath("/lwlk/terminal/query.action")
					.setParameter("rt", "read")
					.setParameter("visitType", "detail")
					.setParameter("entity.id", id).build();
			httpget1 = new HttpGet(szSrcURI);

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			continue;
		}

		httpget1.setConfig(requestConfig);

		httpget1.setHeader("User-Agent",szLocalUA);
	
		try {
		
			response1 = httpclient.execute(httpget1, mClientContext);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			httpget1.releaseConnection();	
			
			continue;
		}

		try {
			
			 int rspStatus = HttpStatus.SC_OK;
			 if (null !=response1)
			 {
				 rspStatus = response1.getStatusLine().getStatusCode();
			 }
			 else
			 {
				System.out.println("null is response" + i);
				httpget1.releaseConnection();	
				continue;
			 }
			 
			
			// ���״̬
			if (HttpStatus.SC_OK !=rspStatus)
			{
				System.out.println(response1.getStatusLine() +":ID="+ (new Integer(i).toString()));
				httpget1.releaseConnection();	
				continue;
			}
			
			HttpEntity entity2 = response1.getEntity();

			if (null == entity2) {
				System.out.println("entity null" + i);
				httpclient.close();
				continue;
			}

			ContentType contentType = ContentType.getOrDefault(entity2);
			Charset charset = contentType.getCharset();
			
			/***
			 * 1.1 �ֽ�ÿ������
			 * --------------------------------------------------
			 * --------------------
			 */

			// ��ȡHTTP Body

			long len = entity2.getContentLength();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(entity2.getContent(),charset) );
			StringBuffer buffer = new StringBuffer();
			String line=null;
			
			try 
			{
				while ((line = in.readLine()) != null)
				{
					buffer.append(line);
				}						
			}
			catch (IOException e) 
			{
				httpclient.close();
				e.printStackTrace();
				System.out.println("getContent() failed");
				continue;
			}
			
			
			String szPageContent = buffer.toString();
			/***
			 * 
			 */
			
			CarDevice cardevice = new CarDevice(szPageContent,i);
			if (cardevice.GetHashMap().isEmpty())
			{
				
			}
			else					
			{
				
				System.out.println("saving URL:" +szSrcURI.toString());
				/***
				 * 2. ���浽���ݿ⣨����£�
				 */
				iRecordNum = iRecordNum +cardevice.Save2DB();
			}
			Thread.sleep(400);			
	
		}
		finally {
			// httpclient.close();
			httpget1.releaseConnection();	
		}
		
									
		}
	
	System.out.println(" Insert tb_cardevice record num=" +iRecordNum);
	System.out.println("Ending  .. grasp pages about  Car Terminal Detail:");
	

	/****
	 * 4.���ն���Ϣ���浽���ݿ��С������£�
	 * 
	 */
	} // end of Main() body
	
	
	
}  // class end brace
	
	
