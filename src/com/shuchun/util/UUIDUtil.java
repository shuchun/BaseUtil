package com.shuchun.util;

import java.util.*;

public class UUIDUtil {
	
	static UUID id=UUID.randomUUID(); 
	
	public static void main(String[] args){
		for(int i=0;i<10;i++){
			System.out.println(UUID.randomUUID());
		}
		
		/*System.out.println("Least:"+id.getLeastSignificantBits());
		System.out.println("Most:"+id.getMostSignificantBits());
		System.out.println(id.toString());
		System.out.println("Variant:"+id.variant()+"|Version:"+id.version());
		String[] seg=id.toString().split("-");
		int bit=0;
		
		for(int i=0;i<seg.length;i++){
			System.out.println("seg["+i+"]"+seg[i]+":"+seg[i].length());
			bit+=(seg[i].length()*4);
		}
		
		System.out.println("size:"+bit);*/
	}

}
