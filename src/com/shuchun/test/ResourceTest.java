package com.shuchun.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.shuchun.util.ResourceConfig;

public class ResourceTest {

	private ResourceConfig con;
	
	@Before
	public void setUp() throws Exception {
		con=new ResourceConfig();
	}
	
	@Test
	public void loadTest()throws IOException{
		//ResourceConfig con=new ResourceConfig();
		//con.load("resource/test.csv");
		con.load("resource/test.json");
	}
	
	@Test
	public void getTest() throws IOException{
		con.load("resource/test.csv");
		
		assertEquals("value1",con.get("key1"));
		assertEquals("Öµ",con.get("¼ü"));
		assertNull(con.get("sdf"));
	}
	
	@Test
	public void separatorTest() throws IOException{
		con.load("resource/test.txt","@");
		assertEquals("value1",con.get("key1"));
		con.load("resource/test.txt", "?");
		assertEquals("value1",con.get("key1"));
	}

}
