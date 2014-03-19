package com.shuchun.util;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;

public class ExcelPo implements Serializable {

	private static final long serialVersionUID = 1258801742643210953L;

	// Map<name,cell> //没法标识工作薄顺序
	private List<Sheet> sheets = null;


	public ExcelPo(){
		sheets=new ArrayList<Sheet>();
	}

	public void addSheet(String name, int index) {
		if (sheets == null) {
			sheets = new ArrayList<Sheet>();
		}
		if(index>sheets.size()){
			throw new IllegalArgumentException("工作簿索引不连续！");
		}
		sheets.add(index, new Sheet(name));
	}
	
	//TODO set row height
	public void addRowHeight(int index,int row,int height){
		this.getSheet(index).setRowHeight(row, height);
	}
	
	public void addRowHeight(String name,int row,int height){
		this.getSheet(name).setRowHeight(row, height);
	}
	
	public int getRowHeight(int index,int row){
		return this.getSheet(index).getRowFormat()[row];
	}
	
	public int getRowHeight(String name,int row){
		return this.getSheet(name).getRowFormat()[row];
	}
	
	public void addColWidth(int index,int col,int width){
		this.getSheet(index).setColWidth(col, width);
	}
	
	public void addColWidth(String name,int col,int width){
		this.getSheet(name).setColWidth(col, width);
	}
	
	public int getColWidth(int index,int col){
		return this.getSheet(index).getColFormat()[col];
	}
	public int getColWidth(String name,int col){
		return this.getSheet(name).getColFormat()[col];
	}
	
	public int getColSize(int index){
		return this.getSheet(index).getColFormat().length;
	}
	
	public int getRowSize(int index){
		return this.getSheet(index).getRowFormat().length;
	}
	
	public void addCell(int index,int col,int row,String content){
		if(content==null||content.trim().length()<1){
			this.getSheet(index).addCell(col, row);
		}else{
			this.getSheet(index).addCell(col, row, content);
		}
	}
	
	public void addCell(String name,int col,int row,String content){
		this.addCell(indexOfSheet(name), col, row, content);
	}
	
	public void clearSheet(){
		this.sheets.clear();
	}

	
	public int indexOfSheet(String name){
		return this.sheets.indexOf(name);
	}
	
	public Sheet getSheet(int index){
		return this.sheets.get(index);
	}
	
	public Sheet getSheet(String name){
		for(int i=0;i<sheets.size();i++){
			if(sheets.get(i).name.equals(name)){
				return sheets.get(i);
			}
		}
		
		return null;	
	}
	
	
	public Cell[] getCells(int index){
		return this.getSheet(index).getCells();
	}
	
	public String getSheetName(int index){
		return this.getSheet(index).name;
	}
	
	public String getSheetName(String name){
		return this.getSheet(name).name;
	}
	
	public int getSize(){
		return this.sheets.size();
	}
	
	
	

	/**
	 * @see(功能介绍):Excel工作薄，内部类
	 * @version(版本号): 1.0
	 * @date(创建日期): 2014-3-18
	 * @author(创建人): shuchun
	 */
	class Sheet {

		private String name;// 名称
		//private int index;// 索引值
		private List<Cell> cells;// 包含的单元格
		//TODO new work
		private List<Integer> rowHeight;
		private List<Integer> colWidth;

		public Sheet(String name) {
			this.name = name;
			cells=new ArrayList<Cell>();
			rowHeight=new ArrayList<Integer>();
			colWidth=new ArrayList<Integer>();
		}
		
		public void setRowHeight(int row,int height){
			if(this.rowHeight.size()<1){
				for(int i=0;i<row;i++){
					this.rowHeight.add(255);
				}
			}
			this.rowHeight.add(row,height);
		}
		
		public void setColWidth(int col,int width){
			//System.out.println("col:"+col+"|"+width);
			if(this.colWidth.size()<1){
				for(int i=0;i<col;i++){
					this.colWidth.add(256);
				}
			}
			this.colWidth.add(col,width);
		}
		
		public Integer[] getRowFormat(){
			return (Integer[]) this.rowHeight.toArray(new Integer[this.rowHeight.size()]);
		}
		
		public Integer[] getColFormat(){
			return (Integer[]) this.colWidth.toArray(new Integer[this.colWidth.size()]);
		}
		
		public Cell[] getCells(){
			
			int size=this.cells.size();
			//Object[] aa=this.cells.toArray();
			
			return (Cell[]) this.cells.toArray(new Cell[size]);
		}

		/**
		 * 添加单元格
		 * 
		 * @param col
		 *            列
		 * @param row
		 *            行
		 */
		public void addCell(int col, int row) {
			this.addCell(col, row, "None", CELLTYPE.LABLE);
		}
		
		/**
		 * 添加单元格
		 * @param col	列
		 * @param row	行
		 * @param content	内容
		 */
		public void addCell(int col,int row,String content){
			this.addCell(col, row, content, CELLTYPE.LABLE);
		}

		/**
		 * 添加单元格
		 * 
		 * @param col
		 *            列
		 * @param row
		 *            行
		 * @param content
		 *            内容
		 * @param type
		 *            类型
		 */
		public void addCell(int col, int row, String content, CELLTYPE type) {
			this.addOrUpdateCell(col, row, content, type, false);
		}

		/**
		 * 修改单元格
		 * 
		 * @param col
		 *            列
		 * @param row
		 *            行
		 * @param content
		 *            内容
		 */
		public void modifyCell(int col, int row, String content) {
			this.addOrUpdateCell(col, row, content, CELLTYPE.LABLE, true);
		}

		/**
		 * 修改单元格
		 * 
		 * @param col
		 *            列
		 * @param row
		 *            行
		 * @param content
		 *            内容
		 * @param type
		 *            类型
		 */
		public void modifyCell(int col, int row, String content, CELLTYPE type) {
			this.addOrUpdateCell(col, row, content, type, true);
		}

		/**
		 * 添加或修改单元格
		 * 
		 * @param col
		 *            列
		 * @param row
		 *            行
		 * @param content
		 *            内容
		 * @param type
		 *            类型
		 * @param modify
		 *            是否是修改
		 */
		private void addOrUpdateCell(int col, int row, String content,
				CELLTYPE type, boolean modify) {
			// int size=this.getSize();

			boolean exists = false;
			Cell target = this.containsCell(col, row);
			if (target != null) {
				if (modify) {
					target.setContent(content);
					target.setType(type);
				} else {
					throw new IllegalArgumentException("单元格已存在！");
				}
			} else {
				Cell cell = new Cell(col, row, content, type);
				this.cells.add(cell);
			}
		}

		/**
		 * 删除单元格
		 * 
		 * @param col
		 *            列
		 * @param row
		 *            行
		 */
		public void deleteCell(int col, int row) {
			Cell target = this.containsCell(col, row);
			if (target != null) {
				target = null;
			}
		}

		/**
		 * 包含单元格
		 * 
		 * @param col
		 *            列
		 * @param row
		 *            行
		 * @return 包含的单元格
		 */
		public Cell containsCell(int col, int row) {
			Cell tmp = null;

			for (Cell c : this.cells) {
				if (c.getCol() == col && c.getRow() == row) {
					tmp = c;
					break;
				}
			}

			return tmp;
		}

		/**
		 * 包含的单元个数
		 * 
		 * @return 单元格总数
		 */
		public int getSize() {
			if (this.cells == null) {
				return 0;
			}
			return this.cells.size();
		}

		/**
		 * 清除所有单元格
		 */
		public void clear() {
			this.cells.clear();
			this.cells = null;
		}
		
		@Override
		public String toString(){
			return "Excel Sheet:"+this.name;
		}

	}

	/**
	 * @see(功能介绍):Excel单元格，内部类
	 * @version(版本号): 1.0
	 * @date(创建日期): 2014-3-18
	 * @author(创建人): shuchun
	 */
	public class Cell {

		private int col;
		private int row;
		private String content;
		private CELLTYPE type;

		public Cell(int col, int row, String content, CELLTYPE type) {
			this.col = col;
			this.row = row;
			this.content = content;
			this.type = type;
		}

		public int getCol() {
			return col;
		}

		public void setCol(int col) {
			this.col = col;
		}

		public int getRow() {
			return row;
		}

		public void setRow(int row) {
			this.row = row;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public CELLTYPE getType() {
			return type;
		}

		public void setType(CELLTYPE type) {
			this.type = type;
		}
		
		@Override
		public String toString(){
			return "Cell("+this.row+","+this.col+"):"+this.content;
		}
	}

	/**
	 * @see(功能介绍):单元格类型，枚举
	 * @version(版本号): 1.0
	 * @date(创建日期): 2014-3-18
	 * @author(创建人): shuchun
	 */
	public enum CELLTYPE {
		LABLE, LINK, IMAGE
	}

}

