package com.shuchun.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.shuchun.util.UUIDUtil;

public class UUIDTest {

	@Test(expected=IllegalArgumentException.class)
	public void getUUIDTest() {
		//assertNotEquals(UUIDUtil.getUUID(),UUIDUtil.getUUID());
		String[] ids=UUIDUtil.getUUID(2);
		//assertNotEquals(ids[0],ids[1]);
		System.out.println(ids[0]);
		UUIDUtil.getUUID(-1);
	}
	
	@Test
	public void getShortIDTest(){
		assertEquals("TE1403240011",UUIDUtil.getShortID("OA1403240010","TE"));
		assertEquals("OA1403240001",UUIDUtil.getShortID("OA1402010111"));
	}
}
