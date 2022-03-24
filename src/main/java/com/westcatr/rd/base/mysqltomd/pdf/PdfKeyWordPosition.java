package com.westcatr.rd.base.mysqltomd.pdf;

import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nanmu@taomz.com
 * @version V1.0
 * @title : PdfKeyWordPosition
 * @Package : com.westcatr.rd.base.mysqltomd.pdf
 * @Description:
 * @date 2022/3/24 12:36
 **/
@Slf4j
public class PdfKeyWordPosition extends PDFTextStripper {

	// 关键字字符数组
	private char[] key;

	// PDF文件路径
	private PDDocument document;

	// 坐标信息集合
	private List<TextLocal> list = new ArrayList<>();

	// 当前页信息集合
	private List<TextLocal> pageList = new ArrayList<>();

	public PdfKeyWordPosition(String keyWords, PDDocument document) throws IOException {
		super();
		super.setSortByPosition(true);
		this.document = document;
		char[] key = new char[keyWords.length()];
		for (int i = 0; i < keyWords.length(); i++) {
			key[i] = keyWords.charAt(i);
		}
		this.key = key;
	}

	public void setKey(char[] key) {
		this.key = key;
	}

	public char[] getKey() {
		return key;
	}

	public void setDocument(PDDocument document) {
		this.document = document;
	}

	public PDDocument getDocument() {
		return document;
	}

	public List<TextLocal> getCoordinate() {
		try {
			int pages = document.getNumberOfPages();
			for (int i = 1; i < pages; i++) {
				pageList.clear();
				super.setSortByPosition(true);
				super.setStartPage(i);
				super.setEndPage(i);
				Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
				super.writeText(document, dummy);
				for (TextLocal textLocal : pageList) {
					textLocal.setPageNum(i);
				}
				list.addAll(pageList);
			}
			return list;
		} catch (IOException e) {
			log.error("获取坐标信息失败：{}", ExceptionUtil.stacktraceToString(e));
		}
		return list;
	}

	/**
	 * 获取坐标信息
	 * @param text
	 * @param textPositions
	 * @throws IOException
	 */
	@Override
	protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
		for (int i = 0; i < textPositions.size(); i++) {
			// text得到pdf这一行中的汉字，同时下面有判断这一行字的长度，防止关键字在文中多次出现
			String str = textPositions.get(i).getUnicode();
			if (str.equals(key[0] + "")) {
				int count = 0;
				for (int j = 0; j < key.length; j++) {
					String s = "";
					try {
						s = textPositions.get(i + j).getUnicode();
					} catch (Exception e) {
						s = "";
					}
					if (s.equals(key[j] + "")) {
						count++;
					}
				}
				if (count == key.length) {
					TextLocal textLocal = new TextLocal();
					// 需要进行一些调整 使得章盖在字体上
					// X坐标 在这里加上了字体的长度，也可以直接 idx[0] = textPositions.get(i).getX()
					textLocal.setX(textPositions.get(i).getX() + textPositions.get(i).getFontSize());
					textLocal.setY(textPositions.get(i).getPageHeight() - textPositions.get(i).getY());
					pageList.add(textLocal);
				}
			}
		}
	}
}
