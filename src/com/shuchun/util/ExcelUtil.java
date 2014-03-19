package com.shuchun.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.biff.FontRecord;
import jxl.format.Alignment;
import jxl.format.CellFormat;
import jxl.format.Font;
import jxl.format.VerticalAlignment;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableHyperlink;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelUtil {

	// Excel cell default width and height
	private final int CELL_WIDTH = 65;
	private final int CELL_HEIGHT = 18;

	/**
	 * 导出Excle
	 * 
	 * @param file
	 *            文件绝对路径
	 */
	public void export(String file, ExcelPo po) {
		File excel = new File(file);
		WritableWorkbook wwb = null;

		try {
			wwb = Workbook.createWorkbook(excel);

		} catch (IOException e) {
			e.printStackTrace();
		}

		if (wwb != null) {

			this.poToExcel(wwb, po);

			try {
				wwb.write();
				wwb.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			} finally {

			}
		}

	}

	/**
	 * 导入Excle
	 * 
	 * @param file
	 *            文件绝对路径
	 * @throws FileNotFoundException
	 */
	public ExcelPo read(String file) throws FileNotFoundException {
		return this.queryExcel(file,-1,-1,-1);
	}

	//TODO 分页查询
	
	/**
	 * 分页查询 
	 * @param file		excel文件路径
	 * @param page		页码
	 * @param rows		每页行数
	 * @return			excel对象
	 * @throws FileNotFoundException
	 */
	public ExcelPo queryExcel(String file,int page,int rows) throws FileNotFoundException{
		return this.queryExcel(file,0, page, rows);
	}
	
	/**
	 * 分页查询
	 * @param file		excel文件路径
	 * @param index		工作薄索引
	 * @param page		页码
	 * @param rows		每页行数
	 * @return			excel对象
	 * @throws FileNotFoundException
	 */
	public ExcelPo queryExcel(String file,int index,int page,int rows) throws FileNotFoundException{
		int begin=(page-1)*rows;
		int end=begin+rows;
		
		File excel = new File(file);

		if (!excel.exists()) {
			throw new FileNotFoundException("文件不存在");

		}

		Workbook wb = null;

		try {
			wb = Workbook.getWorkbook(excel);
			// 获取工作薄集合
			Sheet[] sheets = wb.getSheets();

			if(index<0){
				return this.excelToPo(sheets, page, rows);
			}else{
				return this.excelToPo(new Sheet[]{sheets[index]},begin,end);
			}

		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wb.close();
		}

		return null;
	}
	
	
	
	/**
	 * 读取excel文件映射为excelPo对象
	 * 
	 * @param sheets
	 *            工作薄
	 * @return excelPo对象
	 */
	private ExcelPo excelToPo(Sheet[] sheets,int first,int last) {
//		int first=3;
//		int last=6;

		ExcelPo excelPo = null;

		if (sheets != null && sheets.length > 0) {
			excelPo = new ExcelPo();

			// 迭代工作薄
			for (int i = 0; i < sheets.length; i++) {

				excelPo.addSheet(sheets[i].getName(), i);
				// 获取该工作薄行数
				
				int rows = sheets[i].getRows();
				int columns = sheets[i].getColumns();
				int end=0;
				int eof=0;
				if(last>0){
					rows=Math.min(last, rows);
				}
				first=first<0?0:first;
				
				// 迭代该工作簿的每一行
				for (int r = first; r < rows; r++) {

					excelPo.addRowHeight(i, r, sheets[i].getRowView(r).getSize());

					for (int c = 0; c < columns; c++) {
						// 映射到对象中
						
						if(r==first){
							excelPo.addColWidth(i, c, sheets[i].getColumnView(c).getSize()/256);
						}
						
						Cell cell = sheets[i].getCell(c, r);
						String content = cell.getContents();// 单元格内容
						CellFormat format=cell.getCellFormat();
						if (content != null && content.trim().length() > 0) {
							excelPo.addCell(i, c, r, content);
						}else{
							end++;
							if(end>=5){
								columns=c;
								end=0;
								//System.out.println("column modify:"+c);
							}
						}


						//System.out.println("c:"+c);
						// System.out.println(content);
					}
					
					if(columns==0){
						eof++;
						if(eof==5){
							//System.out.println("row:"+r);
							break;
						}
					}
				}
			}
		}

		return excelPo;
	}
	

	private void poToExcel(WritableWorkbook canvas, ExcelPo po) {
		if (canvas != null) {
			// 创建sheet
			WritableSheet[] sheets = new WritableSheet[po.getSize()];

			for (int i = 0; i < po.getSize(); i++) {
				sheets[i] = canvas.createSheet(po.getSheetName(i), i);
				
				for(int c=0;c<po.getColSize(i);c++){
					sheets[i].setColumnView(c, po.getColWidth(i, c));
				}
				for(int r=0;r<po.getRowSize(i);r++){
					try {
						sheets[i].setRowView(r, po.getRowHeight(i, r));
					} catch (RowsExceededException e) {
						e.printStackTrace();
					}
				}
				
				
				ExcelPo.Cell[] cells = po.getCells(i);

				for (int c = 0; c < cells.length; c++) {

					int col = cells[c].getCol();
					int row = cells[c].getRow();
					String content = cells[c].getContent();

					try {
						switch (cells[c].getType()) {
						case LABLE:
							sheets[i].addCell(new Label(col, row, content));
							break;
						case LINK:
							sheets[i].addHyperlink(this.insertLink(row, col,
									content, content));
							break;
						case IMAGE:
							sheets[i].addImage(this
									.insertImg(row, col, content));
							break;
						}

					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}

				}
			}

		}
	}

	/**
	 * 插入图片 目前只支持png
	 * 
	 * @param row
	 *            行
	 * @param column
	 *            列
	 * @param imgPath
	 *            图片路径
	 * @return 图片单元格
	 * @throws FileNotFoundException
	 */
	private WritableImage insertImg(int row, int column, String imgPath)
			throws FileNotFoundException {

		File img = new File(imgPath);
		if (!img.exists()) {
			throw new FileNotFoundException("图片不存在");
		}
		if (!imgPath.toLowerCase().endsWith(".png")) {
			throw new IllegalArgumentException("目前只支持png图片");
		}

		FileInputStream fs = new FileInputStream(img);
		try {
			BufferedImage bufferImg = javax.imageio.ImageIO.read(fs);
			int imgRow = bufferImg.getHeight() / CELL_HEIGHT;
			int imgCol = bufferImg.getWidth() / CELL_WIDTH;

			if (bufferImg.getHeight() % CELL_HEIGHT != 0) {
				imgRow++;
			}
			if (bufferImg.getWidth() % CELL_WIDTH != 0) {
				imgCol++;
			}

			// 合并单元格
			// sheet.mergeCells(column, row, column+imgCol, row+imgRow);

			WritableImage imgCell = new WritableImage(column, row, imgCol,
					imgRow, img);
			return imgCell;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 插入连接
	 * 
	 * @param row
	 *            行
	 * @param column
	 *            列
	 * @param uri
	 *            连接,url或文件连接或工作薄连接
	 * @param desc
	 *            连接描述
	 * @return 连接单元格
	 */
	private WritableHyperlink insertLink(int row, int column, String uri,
			String desc) {
		WritableHyperlink link = null;

		// if (ValidateUtil.isUrl(uri)) {
		if (true) {
			URL url = null;
			try {
				url = new URL(uri);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			link = new WritableHyperlink(column, row, url);
		}

		// TODO 添加文件连接判断

		if (link != null) {
			link.setDescription(desc);
		}

		return null;

	}

	/**
	 * 标题样式，加粗、居中
	 * 
	 * @return 样式
	 */
	private WritableCellFormat title() {
		WritableFont boldFont = new WritableFont(WritableFont.ARIAL, 10,
				WritableFont.BOLD);
		WritableCellFormat format = new WritableCellFormat(boldFont);
		try {
			format.setAlignment(Alignment.CENTRE);
			format.setVerticalAlignment(VerticalAlignment.CENTRE);
			format.setWrap(false);

		} catch (WriteException e) {
			e.printStackTrace();
		}

		return format;

	}
}
