package com.westcatr.rd.base.mysqltomd.word;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.util.PropertyPlaceholderHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
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

    /**
     * 读取word2007转化成html
     *
     * @param sourceFileUrl
     * @param targetFileUrl
     * @return
     */
    public static String docxToHtml(String sourceFileUrl, String targetFileUrl) {
        try (ByteArrayInputStream byteArrayInputStream = generateDoc(new FileInputStream(new File(sourceFileUrl)));
             Writer writer = new PrintWriter(new File(targetFileUrl));
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            if (StrUtil.isNotBlank(sourceFileUrl)) {
                document.loadFromStream(byteArrayInputStream, FileFormat.Docx);
                document.saveToStream(outputStream, FileFormat.Html);

                // 去掉页脚
                org.jsoup.nodes.Document nDocument = Jsoup.parseBodyFragment(outputStream.toString());
                Elements elements = nDocument.getElementsByClass("Footer");
                elements.remove();
                Elements elementsDiv = nDocument.getElementsByTag("div");
                if (elements.size() >= 2) {
                    elementsDiv.get(1).removeAttr("style");
                }
                writer.write(nDocument.toString().replaceAll("\\.网格型", ".fabric"));
            }
        } catch (Exception e) {
            log.error("word转化成html失败：{}", ExceptionUtil.stacktraceToString(e));
        }
        return null;
    }

    public static ByteArrayInputStream generateDoc(InputStream inputStream) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XWPFDocument doc = new XWPFDocument(inputStream);
            List<XWPFTable> xwpfTableList = doc.getTables();
            for (XWPFTable xwpfTable : xwpfTableList) {
                List<XWPFTableRow> rows = xwpfTable.getRows();
                for (int n = 0; n < rows.size(); n++) {
                    XWPFTableRow row = rows.get(n);
                    List<XWPFTableCell> xwpfTableCellList = row.getTableCells();
                    for (Integer i = 0; i < xwpfTableCellList.size(); i++) {
                        generateText(xwpfTableCellList.get(i).getParagraphs());
                    }
                }
            }
            doc.write(outputStream);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(outputStream.toByteArray());
            return byteArrayInputStream;
        } catch (Exception e) {
            log.error("word转化成html失败：{}", ExceptionUtil.stacktraceToString(e));
        }
        return null;
    }

    public static void generateText(List<XWPFParagraph> paragraphList) {
        for (XWPFParagraph paragraph : paragraphList) {
            //遍历获取段落中所有的runs
            List<XWPFRun> runs = paragraph.getRuns();
            if (runs.size() <= 1) continue;
            //合并逻辑
            for (Integer i = 0; i < runs.size(); i++) {
                String text0 = runs.get(i).getText(runs.get(i).getTextPosition());
                if (text0 != null && text0.startsWith("甲")) {
                    StringBuilder selectStr = new StringBuilder(text0);
                    //记录分隔符中间跨越的runs数量，用于字符串拼接和替换
                    int num = 0;
                    int j = i + 1;
                    for (; j < runs.size(); j++) {
                        String text1 = runs.get(j).getText(runs.get(j).getTextPosition());
                        if (text1 != null && text1.endsWith("）")) {
                            num = j - i;
                            break;
                        }
                        selectStr.append(text1);
                        if (selectStr.toString().length() >= "甲方（盖章）".length()) {
                            break;
                        }
                    }
                    if (num != 0) {
                        //num!=0说明找到了[]配对，需要替换
                        StringBuilder newText = new StringBuilder();
                        for (int s = i; s <= i + num; s++) {
                            String text2 = runs.get(s).getText(runs.get(s).getTextPosition());
                            newText.append(text2);
                            runs.get(s).setText(null, 0);
                        }
                        runs.get(i).setText(newText.toString(), 0);

                        //重新定义遍历位置，跳过设置为null的位置
                        i = j + 1;
                    }
                }
            }
        }
    }

    public static String docxToHtmlPoi(String sourceFileUrl, String targetFileUrl, String imageUrl) throws Exception {
        OutputStreamWriter outputStreamWriter = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); OutputStreamWriter FileOutputStream = new OutputStreamWriter(new FileOutputStream(targetFileUrl), "utf-8")) {
            XWPFDocument document = new XWPFDocument(new FileInputStream(sourceFileUrl));
            XHTMLOptions options = XHTMLOptions.create();
            options.setIgnoreStylesIfUnused(Boolean.FALSE);
            options.setFragment(Boolean.TRUE);
            if (StrUtil.isNotBlank(imageUrl)) {
                //图片处理，第二个参数为html文件同级目录下，否则图片找不到。
                ImageManager imageManager = new ImageManager(new File(imageUrl), "image");
                options.setImageManager(imageManager);
            }
            outputStreamWriter = new OutputStreamWriter(outputStream, "utf-8");
            XHTMLConverter xhtmlConverter = (XHTMLConverter) XHTMLConverter.getInstance();
            xhtmlConverter.convert(document, outputStreamWriter, options);
            org.jsoup.nodes.Document htmlDoc = Jsoup.parse(new String(outputStream.toByteArray()), StrUtil.EMPTY, Parser.htmlParser());
            htmlDoc.outputSettings().escapeMode(Entities.EscapeMode.base);
            htmlDoc.outputSettings().prettyPrint(Boolean.FALSE);
            Elements divElem = htmlDoc.getElementsByTag("div");
            for (Element element : divElem) {
                element.attr("style", "width: 160mm;margin: 0 auto;line-height: 150%;");
            }
            FileOutputStream.write(htmlDoc.html());
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
            log.warn("word写入数据异常: " + JSON.toJSONString(textMap));
            log.warn(e.getMessage());
        }
        return null;
    }

    /**
     * 替换文本
     *
     * @param document docx解析对象
     * @param valueMap 待写入字符map
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
            changeTableMessage(valueMap, table, false, null);
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
     *
     * @param xmlContent 字符串模板
     * @param map        map
     * @return
     */
    public static String setWordValue(String xmlContent, Map<String, String> map) {
        if (StrUtil.isBlank(xmlContent)) {
            return xmlContent;
        }
        //定义${开头 ，}结尾的占位符
        PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}");
        //调用替换
        return propertyPlaceholderHelper.replacePlaceholders(xmlContent, map::get);
    }
}
