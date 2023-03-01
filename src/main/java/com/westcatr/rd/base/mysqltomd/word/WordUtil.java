package com.westcatr.rd.base.mysqltomd.word;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import fr.opensagres.poi.xwpf.converter.core.ImageManager;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.util.PropertyPlaceholderHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

/**
 * @author nanmu@taomz.com
 * @version V1.0
 * @title : WordUtil
 * @Package : com.westcatr.rd.base.mysqltomd.word
 * @Description:
 * @date 2022/8/5 10:33
 **/
@Slf4j
public class WordUtil {

	public static String docxToHtml(String sourceFileUrl, String targetFileUrl, String imageUrl) throws Exception {
		OutputStreamWriter outputStreamWriter = null;
		try {
			XWPFDocument document = new XWPFDocument(new FileInputStream(sourceFileUrl));
			XHTMLOptions options = XHTMLOptions.create();
			if (StrUtil.isNotBlank(imageUrl)) {
				//图片处理，第二个参数为html文件同级目录下，否则图片找不到。
				ImageManager imageManager = new ImageManager(new File(imageUrl), "image");
				options.setImageManager(imageManager);
			}
			outputStreamWriter = new OutputStreamWriter(new FileOutputStream(targetFileUrl), "utf-8");
			XHTMLConverter xhtmlConverter = (XHTMLConverter) XHTMLConverter.getInstance();
			xhtmlConverter.convert(document, outputStreamWriter, options);
		} finally {
			if (outputStreamWriter != null) {
				outputStreamWriter.close();
			}
		}
		return targetFileUrl;
	}

	/**
	 * @param inputStream 模板文件流
	 * @param textMap     待写入字符map
	 * @return 新文件流
	 */
	public static InputStream changWord(InputStream inputStream, Map<String, String> textMap) {
		try {
			//读取模板信息,替换指定位置字符
			XWPFDocument document = new XWPFDocument(inputStream);
			if (!changeText(document, textMap)) {
				return null;
			}
			//新文件(XWPFDocument)转成文件流
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			document.write(byteArrayOutputStream);
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			return byteArrayInputStream;
		} catch (Exception e) {
			log.warn("word写入数据异常: "+ JSON.toJSONString(textMap));
			log.warn(e.getMessage());
		}
		return null;
	}

	/**
	 * 替换文本
	 * @param document docx解析对象
	 * @param valueMap  待写入字符map
	 */
	public static Boolean changeText(XWPFDocument document, Map<String, String> valueMap) {
		List<XWPFParagraph> paragraphs = document.getParagraphs();
		if (CollUtil.isEmpty(paragraphs)) {
			return false;
		}
		for (XWPFParagraph paragraph : paragraphs) {
			String text = paragraph.getText();
			if (StrUtil.isBlank(text)) {
				continue;
			}
			if (text.indexOf("$") == -1) {
				continue;
			}
			List<XWPFRun> runs = paragraph.getRuns();
			for (XWPFRun run : runs) {
				String value = setWordValue(run.toString(), valueMap);
				run.setText(value, 0);
			}
		}
		List<XWPFTable> tables = document.getTables();
		if (CollUtil.isEmpty(tables)) {
			return false;
		}
		for (XWPFTable table : tables) {
			String text = table.getText();
			if (StrUtil.isBlank(text)) {
				continue;
			}
			if (text.indexOf("$") == -1) {
				continue;
			}
			changeTableMessage(valueMap,table,false,null);
		}
		return true;
	}

	/**
	 * word中的表格文字替换
	 */
	public static void changeTableMessage(Map<String, String> params, XWPFTable table, boolean isBold, Integer fontSize) {
		int count = table.getNumberOfRows();//获取table的行数
		for (int i = 0; i < count; i++) {
			XWPFTableRow row = table.getRow(i);
			List<XWPFTableCell> cells = row.getTableCells();
			for (XWPFTableCell cell : cells) {//遍历每行的值并进行替换
				System.out.println(cell.getText());
				for (Map.Entry<String, String> e : params.entrySet()) {
					if (cell.getText().equals("${" + e.getKey() + "}")) {
						XWPFParagraph newPara = new XWPFParagraph(cell.getCTTc().addNewP(), cell);
						XWPFRun r1 = newPara.createRun();
						r1.setBold(isBold);
						if (fontSize != null) {
							r1.setFontSize(fontSize);
						}
						r1.setText(e.getValue());
						cell.removeParagraph(0);
						cell.setParagraph(newPara);
					}
				}
			}
		}
	}

	/**
	 * 替换 字符串中 ${aaa}
	 * @param xmlContent  字符串模板
	 * @param map map
	 * @return
	 */
	public static String setWordValue(String xmlContent, Map<String, String> map){
		if(StrUtil.isBlank(xmlContent)){
			return xmlContent;
		}
		//定义${开头 ，}结尾的占位符
		PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}");
		//调用替换
		return propertyPlaceholderHelper.replacePlaceholders(xmlContent, map::get);
	}
}
