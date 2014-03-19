package com.shuchun.util;

import java.util.*;

public class UUIDUtil {
	
	/**
	 * 产生一个UUID
	 * @return		UUID
	 */
	public static String getUUID(){
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	/**
	 * 产生指定数量的UUID
	 * @param number		UUID数量
	 * @return				UUID数组
	 */
	public static String[] getUUID(int number){
		
		if(number<=0){
			throw new IllegalArgumentException(UUIDUtil.class.getName()+"Exception: 数量小于0");
		}

		String[] ids=new String[number];
		
		for(int i=0;i<number;i++){
			ids[i]=UUIDUtil.getUUID();
		}
		
		return ids;
	}

}
