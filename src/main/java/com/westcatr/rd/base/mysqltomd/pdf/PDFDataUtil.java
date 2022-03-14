package com.westcatr.rd.base.mysqltomd.pdf;

import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.util.Matrix;

import java.io.File;
import java.io.IOException;

/**
 * @author nanmu@taomz.com
 * @version V1.0
 * @title : PDFDataUtil
 * @Package : com.westcatr.rd.base.mysqltomd.word
 * @Description:
 * @date 2022/3/14 15:09
 **/
@Slf4j
public class PDFDataUtil {

	/**
	 * 指定页插入一段文字
	 * @param vo
	 * @return
	 * @throws IOException
	 */
	public static PDFDomainVO addPageContent (PDFDomainVO vo) {
		// the document
		try (PDDocument doc = PDDocument.load(new File(vo.getInPutFile()))) {
			PDPageTree pages = doc.getDocumentCatalog().getPages();
			PDFont font = PDType0Font.load(doc, new File("D:\\data\\ARIALUNI.TTF"));
			//字体大小
			float fontSize = 36.0f;
			PDPage page = pages.get(vo.getPageNum());
			PDRectangle pageSize = page.getBBox();
			float stringWidth = font.getStringWidth(vo.getMessage()) * fontSize/1000f;
			// calculate to center of the page
			int rotation = page.getRotation();
			boolean rotate = rotation == 90 || rotation == 270;
			float pageWidth = rotate ? pageSize.getHeight() : pageSize.getWidth();
			float pageHeight = rotate ? pageSize.getWidth() : pageSize.getHeight();
			float centeredXPosition = rotate ? pageHeight/2f : (pageWidth - stringWidth)/2f;
			float centeredYPosition = rotate ? (pageWidth - stringWidth)/2f : pageHeight/2f;
			// append the content to the existing stream
			PDPageContentStream contentStream = new PDPageContentStream(
					doc,
					page,
					PDPageContentStream.AppendMode.APPEND,
					true,
					true);
			contentStream.beginText();
			// set font and font size
			contentStream.setFont(font, fontSize);
			// set text color to red
			contentStream.setNonStrokingColor(1L, 0L, 0L);
			if (rotate) {
				// rotate the text according to the page rotation
				contentStream.setTextMatrix(
						Matrix.getRotateInstance(
								Math.PI/2,
								centeredXPosition,
								centeredYPosition
						)
				);
			}
			else {
				contentStream.setTextMatrix(Matrix.getTranslateInstance(centeredXPosition, centeredYPosition));
			}
			contentStream.showText(vo.getMessage());
			contentStream.endText();
			contentStream.close();
			vo.setAfterPages(doc.getNumberOfPages());
			doc.save( vo.getOutPutFile() );
		} catch (IOException e) {
			log.error("插入字段错误：", ExceptionUtil.stacktraceToString(e));
		}
		return vo;
	}
}
