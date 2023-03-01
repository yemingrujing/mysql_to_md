package com.westcatr.rd.base.mysqltomd.pdf;

import com.westcatr.rd.base.mysqltomd.pdf.PdfConvertHtmlUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author nanmu@taomz.com
 * @version V1.0
 * @title : Test
 * @Package : com.westcatr.rd.base.mysqltomd.word
 * @Description:
 * @date 2022/3/11 17:24
 **/
public class Test1 {

	public static void main(String[] args) throws Exception {
		File file = new File("D:\\data\\html\\测试小花HT202204123622812038754586820220413145622.pdf");
		String htmlPath = "D:\\data\\html\\测试pdf转html.html";
		InputStream inputStream = null;
		BufferedImage bufferedImage;
		try {
			inputStream = new FileInputStream(file);
			bufferedImage = PdfConvertHtmlUtil.pdfStreamToPng(inputStream);
			String base64_png = PdfConvertHtmlUtil.bufferedImageToBase64(bufferedImage);
			PdfConvertHtmlUtil.createHtmlByBase64(base64_png, htmlPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally {
			try {
				if(inputStream != null){inputStream.close();}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
