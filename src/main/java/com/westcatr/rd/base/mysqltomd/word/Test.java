package com.westcatr.rd.base.mysqltomd.word;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nanmu@taomz.com
 * @version V1.0
 * @title : Test
 * @Package : com.westcatr.rd.base.mysqltomd.word
 * @Description:
 * @date 2022/3/11 17:24
 **/
public class Test {

	public static void main(String[] args) throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("合同编号", "{{contractCode}}");
//		params.put("年", "{{currentYear}}");
//		params.put("月", "{{currentMonth}}");
//		params.put("日", "{{nowDay}}");
//		params.put("乙方", "{{companyName}}");
//		params.put("法定代表人", "{{personName}}");
//		params.put("联系邮箱", "{{email}}");
//		params.put("注册地", "{{address}}");
//		params.put("乙方（盖章）", "{{companyName}}");
//		WordDataUtil.replaceDocxText("D:\\data\\线下展示服务确认表-180万冠名20210610.docx", params, "D:\\data\\线下展示服务确认表-180万冠名20210610_11.docx");

//		Map<String, Object> replaceParam = new HashMap<>();
//		Style style = Style.builder().buildColor("000000").buildBold().build();
//		style.setHighlightColor(XWPFHighlightColor.WHITE);
//		replaceParam.put("companyName", Texts.of("上海耽美艺术集团").style(style).create());
//		replaceParam.put("personName", Texts.of("光紫宸").style(style).create());
//		replaceParam.put("email", Texts.of("1021167123@qq.com").style(style).create());
//		replaceParam.put("address", Texts.of("上海九个枣有限公司").style(style).create());
//		replaceParam.put("contractCode", Texts.of("HT343494354345").style(style).create());
//		replaceParam.put("currentYear", Texts.of(String.valueOf(DateUtil.year(DateUtil.date())).substring(2)).style(style).create());
//		replaceParam.put("currentMonth", Texts.of(String.valueOf(DateUtil.month(DateUtil.date()))).style(style).create());
//		replaceParam.put("nowDay", Texts.of(String.valueOf(DateUtil.dayOfMonth(DateUtil.date()))).style(style).create());
//		WordDataUtil.replaceLabel("D:\\data\\线下展示服务确认表-20万赞助20210610_11.docx", replaceParam);

////		WordToText.importWordToTxt("D:\\data\\线下展示服务确认表-180万冠名20210610.docx", "D:\\data\\线下展示服务确认表-180万冠名20210610.txt");
//		String content = WordToText.docxToHtml("D:\\data\\线下展示服务确认表-180万冠名20210610.docx", "D:\\data\\html\\线下展示服务确认表-180万冠名20210610.html");
//		org.jsoup.nodes.Document document = Jsoup.parseBodyFragment(content);
//		// 去掉页边距和固定宽度，去除div的样式
//		Elements elements = document.getElementsByTag("div");
//		for (Element element : elements) {
//			element.attr("style", "");
//		}
//		// 设置所有table的样式
//		Elements tables = document.getElementsByTag("table");
//		for (Element table : tables) {
//			table.attr("style", "border-collapse: collapse;");
//		}
//		String outerHtml = document.outerHtml();
//		WordToText.HtmlToPdf(outerHtml, "D:\\data\\html\\线下展示服务确认表-180万冠名20210610.pdf");


////		String baseURL = "D:\\data\\html";
////		WordToText.HtmlToDocx(baseURL, "D:\\data\\html\\线下展示服务确认表-180万冠名20210610.html", "D:\\data\\html\\线下展示服务确认表-180万冠名20210610.docx");

//		if (StrUtil.isNotBlank(content)) {
//			WordToText.HtmlToDocx(baseURL, content, "D:\\data\\html\\线下展示服务确认表-180万冠名20210610.docx");
//		}


		// 加载示例文档
		Document document1 = new Document();
		document1.loadFromFile("D:\\data\\线下展示服务确认表-180万冠名20210610.docx");
//		document1.saveToFile("D:\\data\\html\\补充协议（续展）20220111.html", FileFormat.Html);
		ByteArrayOutputStream outputStream =  new ByteArrayOutputStream();
		document1.saveToStream(outputStream, FileFormat.Html);

		org.jsoup.nodes.Document nDocument = Jsoup.parseBodyFragment(outputStream.toString());
		// 去除页脚
		Elements elements = nDocument.getElementsByClass("Footer");
		elements.remove();
		// 去除页脚
		Elements elementsHtml = nDocument.getElementsByClass("Footer");
		elementsHtml.remove();
		// 去掉页边距和固定宽度，去除div的样式
//		Elements elements = document.getElementsByTag("div");
//		for (Element element : elements) {
//			element.attr("style", "");
//		}
		// 设置所有table的样式
//		Elements tables = document.getElementsByTag("table");
//		for (Element table : tables) {
//			table.attr("style", "border-collapse: collapse;");
//		}
		String outerHtml = nDocument.outerHtml();
		WordToText.HtmlToPdf(outerHtml, "D:\\data\\html\\线下展示服务确认表-180万冠名20210610.pdf");

//		Document document2 = new Document();
//		document2.loadFromStream(new ByteArrayInputStream(outputStream.toByteArray()), FileFormat.Html);
////		document2.loadFromFile("D:\\data\\html\\补充协议（续展）20220111.html");
//		document2.saveToFile("D:\\data\\html\\补充协议（续展）20220111.pdf", FileFormat.PDF);
	}
}
