package com.westcatr.rd.base.mysqltomd.pdf;

import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.util.Matrix;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
			PdfKeyWordPosition pdfKeyWordPosition = new PdfKeyWordPosition("甲方（盖章）", doc);
			List<TextLocal> positions = pdfKeyWordPosition.getCoordinate();

			PDPageTree pages = doc.getDocumentCatalog().getPages();
			PDFont font = PDType0Font.load(doc, new File("D:\\data\\ARIALUNI.TTF"));
			//字体大小
			float fontSize = 12.0f;
			for (TextLocal textLocal : positions)  {
				PDPage page = pages.get(textLocal.getPageNum() - 1);
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
				// 设置透明度
				PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
				graphicsState.setNonStrokingAlphaConstant(0f);
				graphicsState.setAlphaSourceFlag(true);
				contentStream.setGraphicsStateParameters(graphicsState);

				int rotation = page.getRotation();
				boolean rotate = rotation == 90 || rotation == 270;
				if (rotate) {
					// rotate the text according to the page rotation
					contentStream.setTextMatrix(
							Matrix.getRotateInstance(
									Math.PI/2,
									textLocal.getX(),
									textLocal.getY()
							)
					);
				}
				else {
					contentStream.setTextMatrix(Matrix.getTranslateInstance(textLocal.getX(), textLocal.getY()));
				}
				contentStream.showText(vo.getMessage());
				contentStream.endText();
				contentStream.close();
			}
			doc.save( vo.getOutPutFile() );
		} catch (IOException e) {
			log.error("插入字段错误：", ExceptionUtil.stacktraceToString(e));
		}
		return vo;
	}
}
