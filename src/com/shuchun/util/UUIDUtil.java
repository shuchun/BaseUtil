package com.shuchun.util;

import java.text.DateFormat;
import java.util.*;

public class UUIDUtil {
	
	/**
	 * ����һ��UUID
	 * @return		UUID
	 */
	public static String getUUID(){
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	/**
	 * ����ָ��������UUID
	 * @param number		UUID����
	 * @return				UUID����
	 */
	public static String[] getUUID(int number){
		
		if(number<=0){
			throw new IllegalArgumentException(UUIDUtil.class.getName()+"Exception: ����С��0");
		}

		String[] ids=new String[number];
		
		for(int i=0;i<number;i++){
			ids[i]=UUIDUtil.getUUID();
		}
		
		return ids;
	}
	
	public static String getShortID(String lastId){
		
		return UUIDUtil.getShortID(lastId, "OA");
	}
	
	public static String  getShortID(String id,String prefix){
		String newId=prefix;
		//��ѯ����ID
		String maxId=id;
		//��ȡ��������ڲ����������ڱȽ�
		Date now=new Date();
		DateFormat d1=DateFormat.getInstance();

		String when=maxId.substring(2,8);
		when="20"+when.substring(0,2)+"-"+when.substring(2, 4)+"-"+when.substring(4);
		Date d=DateUtil.format(when,"yyyy-mm-dd");
		now=DateUtil.format(d1.format(now),"yy-mm-dd");
		
		if(ValidateUtil.isEqu(d, now)){
			int number=Integer.valueOf(maxId.substring(maxId.length()-4));
			number++;
			newId+=DateUtil.format(d,"yymmdd")+String.format("%04d", number);
		}else{
			String[] nowDate=d1.format(new Date()).substring(0,7).split("-"); 
			String tmp=nowDate[0]+String.format("%02d", Integer.valueOf(nowDate[1]))+
					String.format("%02d", Integer.valueOf(nowDate[2]));
			//System.out.println(tmp);
			newId+=tmp+String.format("%04d", 1);
		}
		
		//System.out.println(newId);
		return newId;
		
	}

}
