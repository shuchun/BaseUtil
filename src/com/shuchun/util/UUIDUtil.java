package com.shuchun.util;

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

}
