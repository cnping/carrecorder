package org.iotsys.czpt;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.io.IOException;
import java.lang.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import org.apache.commons.logging.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;


import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;

import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.*;

import org.iotsys.dao.BaseDao;


/**
 * 
 * @author:alex.liuyicai(marker.liu@foxmail.com)
 * @sine:2014-12-31
 * @description��
 *   ��1������URL����·���ϵͳƽ̨����
 *   
 *   ��2���־û�
 *   
 *   ��3��ͳ�Ʒ�������ʵ�֣�
 *     ��Խ��е�·���ƽ̨���з����ҡ���Ӫ��ҵ��λ����Ӫ��ҵ�Ƚ��з�����
 *
 */
public class CarMonitorPlat {

	/***
	 * �������������ݿ�����ֶ�ӳ���
	 * 
	 */
	private HashMap<String,String>   m_Ch2SegMap = new HashMap<String,String>(){
			{
		     put("id","id");
		     put("ƽ̨����","ptmc");
		     put("ƽ̨���","ptbm");
		     put("ƽ̨�汾","ptbb");
		     put("ƽ̨���","ptlx");
		     put("����������","jcjgmc");
		     put("��ⱨ����","jcbgbm");
		     put("�����������","sqjgmc");
		     put("���������ַ","sqjgdz");
		     put("�ʱ�","yzbm");
		     put("��ϵ��","lianxiren");
		     put("�ֻ�","shouji");
		     put("��ϵ�绰","lxdh");
		     put("�����ʼ�","emailaddr");
		     put("����","faxno");
		     put("�ڲ����ܲ���","nbzgbm");
		     put("���Ÿ�����","bmfzr");
		     put("����","reserver1");		
		
			}
			};
			
			
	private HashMap<String,String>   m_rKVMap  = new HashMap<String,String>();
	
	String m_szID = null;
	int    m_iID = 0;
	

	/**
	 * 
	 * @param szPageContent
	 */
	
	CarMonitorPlat(CarMonitorPlat  srcobj)
	{
		
		HashMap<String,String>  tmpMap = srcobj.GetHashMap();
		this.m_rKVMap.clear();
		
		Iterator<Entry<String,String>> entrySetIterator=tmpMap.entrySet().iterator();  
		
        while(entrySetIterator.hasNext()){  
            Entry<String,String> entry=entrySetIterator.next();  
            
            this.m_rKVMap.put(entry.getKey(),entry.getValue());
        }    
        
        this.m_szID = srcobj.GetIDString();
        this.m_iID  = new Integer(m_szID).intValue();
	}
	
    CarMonitorPlat(String szPageContent,int iID)
	{
		Document doc = Jsoup.parse(szPageContent);
		String szHtmlClsName = "w100";

		Elements tablelnks = doc.getElementsByClass(szHtmlClsName);
		m_rKVMap.clear();
		
		for (Element src : tablelnks) 
		{
			Elements tags = src.getElementsByTag("td");
			String szLastText = "";
			for (Element tt : tags)
			{
				final String szPTinfo = "ƽ̨������Ϣ";
				final String szJGinfo = "���������Ϣ";
				
				String szNameVal = tt.text();
				
				// �ֶ����Q�У�����ֵ��Ҫ����
				if (false == (szNameVal.equals(szPTinfo) || szNameVal.equals(szJGinfo))) 
				{
					// System.out.println("tags Text = " + tt.text());
					// �ֶε��������Q
					if (szLastText.length() == 0) 
					{
						szLastText = szNameVal;
					} 
					else
					{
						// �����ֶβ���Ϊ�գ�
						// ƽ̨����
						final String szPtmc = "ƽ̨����";
						
						String szKey = szLastText;
						String szVal = szNameVal;
						
						// �ֶΣ�ƽ̨���ƣ���ֵ���費Ϊ�ա�
						if ((szKey.equals(szPtmc)) && (szVal.isEmpty()))
						{
							break;
						}
					
						// �ֶ�ֵ��
						String szRealKey = m_Ch2SegMap.get(szKey);
						
						if ((null != szRealKey) && (false == szRealKey.isEmpty()))
						{
							// System.out.println("key:value="+szRealKey+":"+szVal);
							m_rKVMap.put(szRealKey, szVal);
						}
						
						szLastText = "";
					}
				} else {
					szLastText = "";
				}
			}
		}
		
		if (false == m_rKVMap.isEmpty())
		{
			m_szID = new Integer(iID).toString();
			m_rKVMap.put("id", m_szID);	
			m_iID = iID;
		}				
		
    }
	
	
	public HashMap<String,String> GetHashMap()
	{
		return m_rKVMap;
	}
	 
	public String GetIDString()
	{
		return m_szID;
	}
	
	/***
	 * 
	 */
	
	public int Save2DB()
	{
		String szSQL = "replace into tb_syspt ";
		
		String szSegList= new String ();

		String szValdot = new String ();
		
		ArrayList<String> list = new ArrayList<String>();
				
		Iterator<Entry<String,String>> entrySetIterator=m_rKVMap.entrySet().iterator();  

		int iSum=0;
			
		int iSegNum =0;
        while(entrySetIterator.hasNext())
        {  
            Entry<String,String> entry=entrySetIterator.next();
            if (0 == iSegNum)
            {
            	szSegList = "(" + 	 entry.getKey();
            	szValdot  = "(" +  "?";
            }
            else
            {
            	szSegList =szSegList + "," + entry.getKey();
            	szValdot  = szValdot + "," +  "?";
            }
            list.add(iSegNum,entry.getValue());
            
            iSegNum ++;
        }    
        /**
         * 
         */
        szSegList = szSegList +")";
        szValdot  = szValdot +")";   
        
        szSQL = szSQL + szSegList + " values " + szValdot;
        // System.out.println("SQL="+szSQL);
       
        BaseDao ssDao = new BaseDao();
        iSum = ssDao.executeUpdate(szSQL,list);		
        return iSum;
	}

}
