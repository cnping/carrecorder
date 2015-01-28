package org.iotsys.czpt;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;


import java.io.IOException;
import java.lang.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.parser.*;
import org.jsoup.helper.*;
import org.jsoup.select.*;
import org.apache.commons.logging.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
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
import org.iotsys.dao.BaseDao;


/***********
 * @author :alex.liu(marker.liu@foxmail.com)
 * @sine: 2014-12-31
 * @description:
 *    (1)����URL:
 *       http://www.ctis.cn/lwlk/terminal/query.action?rt=read&visitType=detail&entity.id=1083
 *    (2) �־û��洢
 *      ���ն�������Ϣ�洢�����ݿ�(MySQL)
 *    (3) ͳ�Ʒ�������ʵ�֣�
 *      ���ն˵�BOM�������б���з��������ڲ�ƷԤ�С��ɱ������;�Ʒ������
 *
 */

public class CarDevice {

	/***
	 * ��ҳ�ֶ����������ݿ���ֶ�ӳ�䡣
	 */
	private HashMap<String,String>   m_Ch2SegMap = new HashMap<String,String>(){
		{
			// �ɼ� URI
			// http://www.ctis.cn/lwlk/terminal/query.action?rt=read&visitType=detail&entity.id=1083

			// �ն˻�����Ϣ

			put("id", "id");

			// ��Ʒ���� ������ʻ��¼��
			// '��Ʒ����', `cpmc`
			put("��Ʒ����", "cpmc");

			// �ն��ͺ� KS880-BD
			put("�ն��ͺ�", "zdbm");

			// �������� �������ϵ��ӿƼ����޹�˾
			put("��������", "cjmc");

			// ���ұ�� 70967
			put("���ұ��", "cjbh");

			// ��Ӧ���� �͡�Σ����
			put("��Ӧ����", "sycx");

			// ���������� ����ͨ�ŵ����뱱������Ӧ�ò�Ʒ�����ල��������
			put("����������", "jcjgmc");

			// ͨ������ ��7��
			put("ͨ������", "tgpc");

			// �߱���ѡ������ �����˵�����Ƶ��Ϣ��ͨ������ʱͣ�����ѡ���Ϣ����
			put("�߱���ѡ������", "jbkxglx");

			// ���߱���ѡ������ ��
			put("���߱���ѡ������", "bjbkxglx");

			// ������������ �������ϵ��ӿƼ����޹�˾
			put("������������", "sccjmc");

			// ��λģʽ GPS/����˫ģ
			put("��λģʽ", "dwms");

			// ͨ�ŷ�ʽ GSM
			put("ͨ�ŷ�ʽ", "txfs");

			// ������Ϣ
			// ��������ʡ ����ʡ
			put("��������ʡ", "cjszsf");

			// ���������� ������
			put("����������", "cjszcs");

			// ������ϵ�� ������
			put("������ϵ��", "cjlxr");

			// ������ϵ�绰 0591-83332828
			put("������ϵ�绰", "cjlxdh");

			// ���Ҵ��� 0591-83337878
			put("���Ҵ���", "cjfaxno");

			// ������������ 350007
			put("������������", "cjyzbm");

			// ����Email 26097208@qq.com
			put("����Email", "cjemailaddr");

			// ���ҵ�ַ ����ʡ�����в�ɽ����ɽ��ϼ��241-2��
			put("���ҵ�ַ", "cjdz");

			// ά������ ά�������б�
			put("ά������", "wxwd");

			// �ն���ϸ���
			// ΢�������ͺ� LPC1765
			put("΢�������ͺ�", "wclqxh");

			// ΢���������� NXP
			put("΢����������", "wclqcj");

			// ���ݴ洢�ͺ� FM25CL SST25VF
			put("���ݴ洢�ͺ�", "sjccxh");

			// ���ݴ洢���� RAMTRON SST
			put("���ݴ洢����", "sjcccj");

			// ���Ƕ�λģ���ͺ� TD3020C
			put("���Ƕ�λģ���ͺ�", "wxdwmkxh");

			// ���Ƕ�λģ�鳧�� ��ݸ̩��΢����
			put("���Ƕ�λģ�鳧��", "wxdwmkcj");

			// ͨ��ģ���ͺ� SIM900A
			put("ͨ��ģ���ͺ�", "txmkxh");

			// ����汾 �������ϵ��ӿƼ����޹�˾��V1.58��
			put("����汾", "rjbb");

			// �ⲿ�豸�ӿ� USB�ӿڡ�CAN���߽ӿڡ�RS232�ӿ�(2��)
			put("�ⲿ�豸�ӿ�", "wbsbjk");

			// ��Ϣ�� ��ģ��
			// ��ʻԱ���ʶ�� �й���ͨ(VRD01 NZ)
			put("��ʻԱ���ʶ��", "jsysfsb");

			// �����˵� �й���ͨ(VRD01 NZ)
			put("�����˵�", "dzyd");

			// ����CAN�������� NXP��PCA825C251T��
			put("����CAN��������", "clzxsj");

			// ��������״̬ ��ͯ��CD4021BC��
			put("��������״̬", "clhzzt");

			// ͼ�� ������ľ��QVA-031101-0A4��
			put("ͼ��", "txmk");

			// ��Ƶ ϣķͨ��Ϣ�Ƽ����޹�˾��SIM900A��
			put("��Ƶ", "ypmk");

			// ��Ƶ
			put("��Ƶ", "spmk");

			// ��ʻ��¼��Ϣ "RAMTRON��FM25CL��SST��SST25VF��"
			put("��ʻ��¼��Ϣ", "xsjlxx");

			put("����", "reserved");

		}
	};

	// ��ҳ�У�������Щ�ؼ��֣�Ӧ������
	private ArrayList<String> m_SkipList = new ArrayList<String>() {
		{
			// �ն˻�����Ϣ
			add("�ն˻�����Ϣ");
			// ������Ϣ
			add("������Ϣ");

			// �ն���ϸ���
			add("�ն���ϸ���");

			// ��Ϣ�ɼ�ģ��
			add("��Ϣ�ɼ�ģ��");
		}
	};
	/***
	 * 
	 */
	
	private HashMap<String,String>   m_rKVMap  = new HashMap<String,String>();
	
	String m_szID = null;
	int    m_iID = 0;
	
	
	
	public HashMap<String,String> GetHashMap()
	{
		return m_rKVMap;
	}
	
	/***
	 * 
	 */
	public CarDevice(String szPageContent,int iID)
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
				
				if (true == m_SkipList.contains(szNameVal))
				{
					szLastText = "";
				}
				else
				{
					if (szLastText.length() == 0) 
					{
						szLastText = szNameVal;
					} 
					else
					{
						final String szcpmc = "��Ʒ����";
						String szKey = szLastText;
						String szVal = szNameVal;
						
						// �ֶΣ�ƽ̨���ƣ���ֵ���費Ϊ�ա�
						if ((szKey.equals(szcpmc)) && (szVal.isEmpty()))
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

	/***
	 * ���浽���ݿ���
	 */
	public int Save2DB()
	{
		String szSQL = "replace into tb_cardevice ";
		
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
