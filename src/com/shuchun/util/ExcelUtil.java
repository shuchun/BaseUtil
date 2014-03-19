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
	 * ����Excle
	 * 
	 * @param file
	 *            �ļ�����·��
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
	 * ����Excle
	 * 
	 * @param file
	 *            �ļ�����·��
	 * @throws FileNotFoundException
	 */
	public ExcelPo read(String file) throws FileNotFoundException {
		return this.queryExcel(file,-1,-1,-1);
	}

	//TODO ��ҳ��ѯ
	
	/**
	 * ��ҳ��ѯ 
	 * @param file		excel�ļ�·��
	 * @param page		ҳ��
	 * @param rows		ÿҳ����
	 * @return			excel����
	 * @throws FileNotFoundException
	 */
	public ExcelPo queryExcel(String file,int page,int rows) throws FileNotFoundException{
		return this.queryExcel(file,0, page, rows);
	}
	
	/**
	 * ��ҳ��ѯ
	 * @param file		excel�ļ�·��
	 * @param index		����������
	 * @param page		ҳ��
	 * @param rows		ÿҳ����
	 * @return			excel����
	 * @throws FileNotFoundException
	 */
	public ExcelPo queryExcel(String file,int index,int page,int rows) throws FileNotFoundException{
		int begin=(page-1)*rows;
		int end=begin+rows;
		
		File excel = new File(file);

		if (!excel.exists()) {
			throw new FileNotFoundException("�ļ�������");

		}

		Workbook wb = null;

		try {
			wb = Workbook.getWorkbook(excel);
			// ��ȡ����������
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
	 * ��ȡexcel�ļ�ӳ��ΪexcelPo����
	 * 
	 * @param sheets
	 *            ������
	 * @return excelPo����
	 */
	private ExcelPo excelToPo(Sheet[] sheets,int first,int last) {
//		int first=3;
//		int last=6;

		ExcelPo excelPo = null;

		if (sheets != null && sheets.length > 0) {
			excelPo = new ExcelPo();

			// ����������
			for (int i = 0; i < sheets.length; i++) {

				excelPo.addSheet(sheets[i].getName(), i);
				// ��ȡ�ù���������
				
				int rows = sheets[i].getRows();
				int columns = sheets[i].getColumns();
				int end=0;
				int eof=0;
				if(last>0){
					rows=Math.min(last, rows);
				}
				first=first<0?0:first;
				
				// �����ù�������ÿһ��
				for (int r = first; r < rows; r++) {

					excelPo.addRowHeight(i, r, sheets[i].getRowView(r).getSize());

					for (int c = 0; c < columns; c++) {
						// ӳ�䵽������
						
						if(r==first){
							excelPo.addColWidth(i, c, sheets[i].getColumnView(c).getSize()/256);
						}
						
						Cell cell = sheets[i].getCell(c, r);
						String content = cell.getContents();// ��Ԫ������
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
			// ����sheet
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
	 * ����ͼƬ Ŀǰֻ֧��png
	 * 
	 * @param row
	 *            ��
	 * @param column
	 *            ��
	 * @param imgPath
	 *            ͼƬ·��
	 * @return ͼƬ��Ԫ��
	 * @throws FileNotFoundException
	 */
	private WritableImage insertImg(int row, int column, String imgPath)
			throws FileNotFoundException {

		File img = new File(imgPath);
		if (!img.exists()) {
			throw new FileNotFoundException("ͼƬ������");
		}
		if (!imgPath.toLowerCase().endsWith(".png")) {
			throw new IllegalArgumentException("Ŀǰֻ֧��pngͼƬ");
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

			// �ϲ���Ԫ��
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
	 * ��������
	 * 
	 * @param row
	 *            ��
	 * @param column
	 *            ��
	 * @param uri
	 *            ����,url���ļ����ӻ���������
	 * @param desc
	 *            ��������
	 * @return ���ӵ�Ԫ��
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

		// TODO ����ļ������ж�

		if (link != null) {
			link.setDescription(desc);
		}

		return null;

	}

	/**
	 * ������ʽ���Ӵ֡�����
	 * 
	 * @return ��ʽ
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
