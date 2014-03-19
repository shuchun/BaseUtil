package com.shuchun.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;

import com.shuchun.util.ExcelPo;
import com.shuchun.util.ExcelUtil;

public class ExcelUtilTest {

	private ExcelUtil excel=null;
	private String basePath;

	@Before
	public void setUp() throws Exception {
		excel=new ExcelUtil();
		basePath="D:"+File.separator;
	}
	
	@Test(expected=FileNotFoundException.class)
	public void readTest() throws FileNotFoundException{
		//10MB excel 48m
		long start=System.currentTimeMillis();
		
		/*ExcelPo po=excel.read(basePath+"¹ýÂË.xls");
		assertNotNull(po);
		excel.export(basePath+"export.xls", po);

		long end=System.currentTimeMillis();
		String content=(end-start)/1000+"Ãë";
		System.out.println(content);*/
		
		excel.read("D:"+File.separator+"test!.xls");
	}
	
	@Test(expected=FileNotFoundException.class) 
	public void queryTest() throws FileNotFoundException{
		
		ExcelPo po=excel.queryExcel(basePath+"test.xls", 1, 3);
		assertNotNull(po);
		assertEquals(1*3,po.getRowSize(0));
		
		excel.read("D:"+File.separator+"test!.xls");
	}
	
	@Test
	public void queryTest2() throws FileNotFoundException{
		ExcelPo po=excel.queryExcel(basePath+"test.xls", 0, 2, 4);
		assertNotNull(po);

		assertEquals(2*4, po.getRowSize(0));
		
		excel.export(basePath+"tmp.xls", po);
	}

}
