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
}
