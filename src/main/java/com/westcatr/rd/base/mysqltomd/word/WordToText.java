package com.westcatr.rd.base.mysqltomd.word;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.BlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.font.FontProvider;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.docx4j.convert.in.xhtml.FormattingOption;
import org.docx4j.convert.in.xhtml.XHTMLImporter;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.wml.RFonts;

import javax.xml.bind.JAXBException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 * @author nanmu@taomz.com
 * @version V1.0
 * @title : WordToText
 * @Package : com.westcatr.rd.base.mysqltomd.word
 * @Description:
 * @date 2022/3/19 13:52
 **/
public class WordToText {

	/**
	 * 读取word2007转化成text
	 * @param path
	 * @return
	 */
	static void importWordToTxt(String path, String dest){
		try (OPCPackage openPackage  = POIXMLDocument.openPackage(path)) {
			XWPFWordExtractor word = new XWPFWordExtractor(openPackage);
			String content = word.getText();

			BufferedWriter writer = new BufferedWriter(new FileWriter(dest));
			writer.write(content);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取word2007转化成html
	 * @param path
	 * @param dest
	 * @return
	 */
	public static String docxToHtml(String path, String dest) {
		try (FileOutputStream outputStream = new FileOutputStream(new File(dest))) {
			StringWriter stringWriter = new StringWriter();
			// 读取docx文档
			XWPFDocument document = new XWPFDocument(new FileInputStream(new File(path)));
			// 输出docx文档
			XHTMLOptions options = XHTMLOptions.create();
			// 是否分片
			options.setFragment(Boolean.TRUE);
			options.setIgnoreStylesIfUnused(Boolean.FALSE);

			XHTMLConverter xhtmlConverter = (XHTMLConverter) XHTMLConverter.getInstance();
			xhtmlConverter.convert(document, stringWriter, options);
			return stringWriter.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void HtmlToDocx(String baseURL, String content, String dest) throws Docx4JException, JAXBException, IOException {
		// 设置字体映射
		RFonts rfonts = Context.getWmlObjectFactory().createRFonts();
		rfonts.setAscii("Century Gothic");
		XHTMLImporterImpl.addFontMapping("Century Gothic", rfonts);

		// 创建一个空的docx对象
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
		XHTMLImporter importer = new XHTMLImporterImpl(wordMLPackage);
		importer.setTableFormatting(FormattingOption.IGNORE_CLASS);
		importer.setParagraphFormatting(FormattingOption.IGNORE_CLASS);

		NumberingDefinitionsPart ndp = new NumberingDefinitionsPart();
		wordMLPackage.getMainDocumentPart().addTargetPart(ndp);
		ndp.unmarshalDefaultNumbering();

		// 转换XHTML，并将其添加到我们制作的空docx中
		XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(wordMLPackage);

		XHTMLImporter.setHyperlinkStyle("Hyperlink");
		wordMLPackage.getMainDocumentPart().getContent().addAll(importer.convert(new File(content), baseURL));
		wordMLPackage.save(new File(dest));
	}

	public static void HtmlToPdf(String htmlFile, String pdfFile) throws Exception {
		float topMargin = 114f;
		float bottomMargin = 156f;
		float leftMargin = 90f;
		float rightMargin = 90f;
		try (FileOutputStream outputStream = new FileOutputStream(new File(pdfFile));) {
			PdfWriter writer = new PdfWriter(outputStream);
			PdfDocument pdfDocument = new PdfDocument(writer);
			ConverterProperties props = new ConverterProperties();
			FontProvider fp = new FontProvider();
			fp.addStandardPdfFonts();
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			fp.addDirectory(classLoader.getResource("fonts").getPath());
			props.setFontProvider(fp);
			List<IElement> iElements = HtmlConverter.convertToElements(htmlFile, props);
			Document document = new Document(pdfDocument, PageSize.A4, true); // immediateFlush设置true和false都可以，false 可以使用 relayout
			document.setMargins(topMargin, rightMargin, bottomMargin, leftMargin);
			for (IElement iElement : iElements) {
				BlockElement blockElement = (BlockElement) iElement;
				blockElement.setMargins(1, 0, 1, 0);
				document.add(blockElement);
			}
			document.close();
		} catch (Exception e) {
			throw e;
		}
	}
}
