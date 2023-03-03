package com.westcatr.rd.base.mysqltomd.word;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.deepoove.poi.XWPFTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Section;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHighlight;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHighlightColor;
import org.springframework.util.PropertyPlaceholderHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author nanmu@taomz.com
 * @version V1.0
 * @title : WordDateUtil
 * @Package : com.westcatr.rd.base.mysqltomd.word
 * @Description:
 * @date 2022/3/11 17:08
 **/
@Slf4j
public class WordDataUtil {

    private static final String REPLACE_PREFIX = "{{";
    private static final String REPLACE_SUFFIX = "}}";

    public static void replaceLabel(String path, Map<String, Object> params) throws Exception {
        XWPFTemplate template = XWPFTemplate.compile(path).render(params);
        template.writeAndClose(new FileOutputStream(path));
    }

    public static HWPFDocument replaceText(String path, String findText, String replaceText) throws Exception {
        HWPFDocument doc = openDocument(path);
        Range r = doc.getRange();
        for (int i = 0; i < r.numSections(); ++i) {
            Section s = r.getSection(i);
            for (int j = 0; j < s.numParagraphs(); j++) {
                Paragraph p = s.getParagraph(j);
                for (int k = 0; k < p.numCharacterRuns(); k++) {
                    CharacterRun run = p.getCharacterRun(k);
                    String text = run.text();
                    if (text.contains(findText)) {
                        run.replaceText(findText, replaceText);
                    }
                }
            }
        }
        saveDocument(doc, path);
        return doc;
    }

    public static XWPFDocument replaceDocxText(String path, Map<String, String> params, String inputPath) throws Exception {
        XWPFDocument doc = openDocxDocument(path);
        // 替换段落里内容
        List<XWPFParagraph> paragraphList = doc.getParagraphs();
        replaceInAllParagraphs(paragraphList, params);
        // 替换表格里内容
//		List<XWPFTable> tableList = doc.getTables();
//		replaceInTables(tableList, params);
        saveDocxDocument(doc, inputPath);
        return doc;
    }

    /**
     * 替换所有段落中的标记
     *
     * @param xwpfParagraphList
     * @param params
     */
    private static void replaceInAllParagraphs(List<XWPFParagraph> xwpfParagraphList, Map<String, String> params) {
        for (XWPFParagraph paragraph : xwpfParagraphList) {
            if (paragraph.getText() == null || paragraph.getText().equals("")) continue;
            for (String key : params.keySet()) {
                if ("年".equals(key) || "月".equals(key) || "日".equals(key)) {
                    if (paragraph.getText().replaceAll(" ", "").contains("【】" + key)) {
                        replaceDateAtParagraph(paragraph, key, params.get(key));
                    }
                } else {
                    if (paragraph.getText().replaceAll(" ", "").contains(key)) {
                        replaceAtParagraph(paragraph, key, params.get(key));
                    }
                }
            }
        }
    }

    public static void replaceInParagraph(XWPFParagraph xwpfParagraph, String oldString, String newString) {
        List<XWPFRun> runs = xwpfParagraph.getRuns();
        if (runs != null) {
            for (XWPFRun r : runs) {
                String text = r.getText(0);
                if (Objects.nonNull(text) && text.contains(oldString)) {
                    String temp = text.trim();
                    if (temp.equals(oldString)
                            || (temp.length() > (oldString.length() + REPLACE_PREFIX.length()) && !temp.substring(0, oldString.length() + REPLACE_PREFIX.length()).contains(REPLACE_PREFIX))) {
                        text = text.replace(oldString, newString);
                        r.setText(text, 0);
                    }
                }
            }
        }
    }

    /**
     * 替换段落中的字符串
     *
     * @param xwpfParagraph
     * @param oldString
     * @param newString
     */
    public static void replaceAtParagraph(XWPFParagraph xwpfParagraph, String oldString, String newString) {
        JSONObject pos_map = findSubRunPosAtParagraph(xwpfParagraph, oldString);
        if (pos_map != null) {
            List<XWPFRun> runs = xwpfParagraph.getRuns();
            if (pos_map.getString("content").endsWith("：") || pos_map.getString("content").endsWith("：【")) {
                XWPFRun modelRun = runs.get(pos_map.getInteger("end_pos"));
                XWPFRun xwpfRun = xwpfParagraph.insertNewRun(pos_map.getInteger("end_pos") + 1);
                xwpfRun.setText(newString);
                if (modelRun.getFontSizeAsDouble().intValue() != -1) {
                    //默认值是五号字体，但五号字体getFontSize()时，返回-1
                    xwpfRun.setFontSize(modelRun.getFontSizeAsDouble());
                }
                xwpfRun.setColor("D3D3D3");
                // 高亮显示
                highLight(xwpfParagraph, xwpfRun);
                xwpfRun.setFontFamily(modelRun.getFontFamily());
                if (runs.size() >= pos_map.getInteger("end_pos") + 3) {
                    String text = runs.get(pos_map.getInteger("end_pos") + 2).getText(runs.get(pos_map.getInteger("end_pos") + 2).getTextPosition());
                    if (StrUtil.isBlank(text)) {
                        if (text.length() > newString.length()) {
                            xwpfParagraph.removeRun(pos_map.getInteger("end_pos") + 2);
                            XWPFRun run = xwpfParagraph.insertNewRun(pos_map.getInteger("end_pos") + 2);
                            run.setText(text.substring(0, text.length() - newString.length()));
                        }
                    }
                }
            } else if (pos_map.getString("content").contains("【】")) {
                XWPFRun modelRun = runs.get(pos_map.getInteger("end_pos"));
                Double fontSize = modelRun.getFontSizeAsDouble();
                String fontFamily = modelRun.getFontFamily();

                String text = modelRun.getText(runs.get(pos_map.getInteger("end_pos")).getTextPosition()).replace(" ", "");
                String prefix = text.substring(0, text.lastIndexOf("【") + 1);
                String suffix = text.substring(text.lastIndexOf("】"), text.length());
                xwpfParagraph.removeRun(pos_map.getInteger("end_pos"));
                XWPFRun run = xwpfParagraph.insertNewRun(pos_map.getInteger("end_pos"));
                if (fontSize.intValue() != -1) {
                    //默认值是五号字体，但五号字体getFontSize()时，返回-1
                    run.setFontSize(fontSize);
                }
                run.setBold(Boolean.TRUE);
                run.setFontFamily(fontFamily);
                run.setText(prefix);

                XWPFRun runNextOne = xwpfParagraph.insertNewRun(pos_map.getInteger("end_pos") + 1);
                if (fontSize.intValue() != -1) {
                    //默认值是五号字体，但五号字体getFontSize()时，返回-1
                    runNextOne.setFontSize(fontSize);
                }
                runNextOne.setBold(Boolean.TRUE);
                runNextOne.setFontFamily(fontFamily);
                runNextOne.setColor("D3D3D3");
                runNextOne.setText(newString);

                XWPFRun runNextTwo = xwpfParagraph.insertNewRun(pos_map.getInteger("end_pos") + 2);
                if (fontSize.intValue() != -1) {
                    //默认值是五号字体，但五号字体getFontSize()时，返回-1
                    runNextTwo.setFontSize(fontSize);
                }
                runNextTwo.setBold(Boolean.TRUE);
                runNextTwo.setFontFamily(fontFamily);
                runNextTwo.setText(suffix);
            }
        }
    }

    /**
     * 替换段落中的字符串
     *
     * @param xwpfParagraph
     * @param oldString
     * @param newString
     */
    public static void replaceDateAtParagraph(XWPFParagraph xwpfParagraph, String oldString, String newString) {
        JSONObject pos_map = findSubRunPosDateAtParagraph(xwpfParagraph, oldString);
        if (pos_map != null) {
            List<XWPFRun> runs = xwpfParagraph.getRuns();
            if (pos_map.getString("content").endsWith("【】")) {
                XWPFRun modelRun = runs.get(pos_map.getInteger("end_pos"));
                Double fontSize = modelRun.getFontSizeAsDouble();
                String fontFamily = modelRun.getFontFamily();

                String text = modelRun.getText(runs.get(pos_map.getInteger("end_pos")).getTextPosition()).replace(" ", "");
                String prefix = text.substring(0, text.lastIndexOf("【") + 1);
                String suffix = text.substring(text.lastIndexOf("】"));
                xwpfParagraph.removeRun(pos_map.getInteger("end_pos"));
                XWPFRun run = xwpfParagraph.insertNewRun(pos_map.getInteger("end_pos"));
                if (fontSize.intValue() != -1) {
                    //默认值是五号字体，但五号字体getFontSize()时，返回-1
                    run.setFontSize(fontSize);
                }
                run.setBold(Boolean.TRUE);
                run.setFontFamily(fontFamily);
                run.setText(prefix);

                XWPFRun runNextOne = xwpfParagraph.insertNewRun(pos_map.getInteger("end_pos") + 1);
                if (fontSize.intValue() != -1) {
                    //默认值是五号字体，但五号字体getFontSize()时，返回-1
                    runNextOne.setFontSize(fontSize);
                }
                runNextOne.setBold(Boolean.TRUE);
                runNextOne.setFontFamily(fontFamily);
                runNextOne.setColor("D3D3D3");
                runNextOne.setText(newString);

                XWPFRun runNextTwo = xwpfParagraph.insertNewRun(pos_map.getInteger("end_pos") + 2);
                if (fontSize.intValue() != -1) {
                    //默认值是五号字体，但五号字体getFontSize()时，返回-1
                    runNextTwo.setFontSize(fontSize);
                }
                runNextTwo.setBold(Boolean.TRUE);
                runNextTwo.setFontFamily(fontFamily);
                runNextTwo.setText(suffix);
            } else if (pos_map.getString("content").contains("【】" + oldString)) {
                XWPFRun modelRun = runs.get(pos_map.getInteger("end_pos"));
                Double fontSize = modelRun.getFontSizeAsDouble();
                String fontFamily = modelRun.getFontFamily();

                String text = modelRun.getText(runs.get(pos_map.getInteger("end_pos")).getTextPosition());
                String prefix = text.substring(0, text.lastIndexOf("】" + oldString)).trim();
                String suffix = text.substring(text.lastIndexOf("】" + oldString));

                xwpfParagraph.removeRun(pos_map.getInteger("end_pos"));
                XWPFRun run = xwpfParagraph.insertNewRun(pos_map.getInteger("end_pos"));
                if (fontSize.intValue() != -1) {
                    //默认值是五号字体，但五号字体getFontSize()时，返回-1
                    run.setFontSize(fontSize);
                }
                run.setBold(Boolean.TRUE);
                run.setFontFamily(fontFamily);
                run.setText(prefix);

                XWPFRun runNextOne = xwpfParagraph.insertNewRun(pos_map.getInteger("end_pos") + 1);
                if (fontSize.intValue() != -1) {
                    //默认值是五号字体，但五号字体getFontSize()时，返回-1
                    runNextOne.setFontSize(fontSize);
                }
                runNextOne.setBold(Boolean.TRUE);
                runNextOne.setFontFamily(fontFamily);
                runNextOne.setColor("D3D3D3");
                runNextOne.setText(newString);

                XWPFRun runNextTwo = xwpfParagraph.insertNewRun(pos_map.getInteger("end_pos") + 2);
                if (fontSize.intValue() != -1) {
                    //默认值是五号字体，但五号字体getFontSize()时，返回-1
                    runNextTwo.setFontSize(fontSize);
                }
                runNextTwo.setBold(Boolean.TRUE);
                runNextTwo.setFontFamily(fontFamily);
                runNextTwo.setText(suffix);

                if (xwpfParagraph.getText().replaceAll(" ", "").contains("【】" + oldString)) {
                    replaceDateAtParagraph(xwpfParagraph, oldString, newString);
                }
            }
        }
    }

    /**
     * 找到段落中子串的起始XWPFRun下标和终止XWPFRun的下标
     *
     * @param xwpFParagraph
     * @param oldString
     * @return
     */
    public static JSONObject findSubRunPosAtParagraph(XWPFParagraph xwpFParagraph, String oldString) {
        List<XWPFRun> runs = xwpFParagraph.getRuns();
        int start_pos;
        int end_pos;

        String temp = "";
        for (int i = 0, j; i < runs.size(); i++) {
            j = i;
            start_pos = i;
            if (StrUtil.isBlank(runs.get(j).getText(runs.get(j).getTextPosition()))) continue;
            temp += runs.get(j).getText(runs.get(j).getTextPosition()).replace(" ", "");
            if (temp.length() >= oldString.length()) {
                if (temp.endsWith(oldString + "：")) {
                    Boolean flag = Boolean.FALSE;
                    if (runs.size() > j + 1) {
                        if (StrUtil.isNotBlank(runs.get(j + 1).getText(runs.get(j + 1).getTextPosition()))) {
                            String brackets = runs.get(j + 1).getText(runs.get(j + 1).getTextPosition()).replace(" ", "");
                            if (brackets.equals("【")) {
                                continue;
                            }
                            if (brackets.startsWith("【】")) {
                                end_pos = j + 1;
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("start_pos", start_pos + 1);
                                jsonObject.put("end_pos", end_pos);
                                jsonObject.put("content", brackets);
                                return jsonObject;
                            }
                        }
                        for (int n = j + 1; n < runs.size(); n++) {
                            String str = runs.get(n).getText(runs.get(n).getTextPosition());
                            if (StrUtil.isBlank(str)) continue;
                            if (str.startsWith("（")) {
                                break;
                            }
                            if (!str.contains("：")) {
                                flag = Boolean.TRUE;
                                break;
                            }
                        }
                    }
                    if (flag) {
                        temp = "";
                        continue;
                    }
                    end_pos = j;
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("start_pos", start_pos);
                    jsonObject.put("end_pos", end_pos);
                    jsonObject.put("content", temp);
                    return jsonObject;
                } else if (temp.endsWith(oldString + "：【")) {
                    end_pos = j;
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("start_pos", start_pos);
                    jsonObject.put("end_pos", end_pos);
                    jsonObject.put("content", temp);
                    return jsonObject;
                } else if (temp.contains(oldString + "：【】") || temp.contains(oldString + "【】")) {
                    end_pos = j;
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("start_pos", start_pos);
                    jsonObject.put("end_pos", end_pos);
                    jsonObject.put("content", temp);
                    return jsonObject;

                }
            }
        }
        return null;
    }

    /**
     * 找到段落中子串的起始XWPFRun下标和终止XWPFRun的下标
     *
     * @param xwpFParagraph
     * @param oldString
     * @return
     */
    public static JSONObject findSubRunPosDateAtParagraph(XWPFParagraph xwpFParagraph, String oldString) {
        List<XWPFRun> runs = xwpFParagraph.getRuns();
        int start_pos;
        int end_pos;
        String temp = "";
        for (int i = 0, j; i < runs.size(); i++) {
            j = i;
            start_pos = i;
            if (StrUtil.isBlank(runs.get(j).getText(runs.get(j).getTextPosition()))) continue;
            temp += runs.get(j).getText(runs.get(j).getTextPosition()).replace(" ", "");
            if (temp.length() >= oldString.length()) {
                if (runs.size() > j + 1) {
                    if (StrUtil.isNotBlank(runs.get(j + 1).getText(runs.get(j + 1).getTextPosition()))) {
                        String brackets = runs.get(j + 1).getText(runs.get(j + 1).getTextPosition()).replace(" ", "");
                        if (brackets.equals(oldString)) {
                            end_pos = j;
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("start_pos", start_pos);
                            jsonObject.put("end_pos", end_pos);
                            jsonObject.put("content", temp);
                            return jsonObject;
                        }
                    }
                }
                if (temp.contains("【】" + oldString)) {
                    end_pos = j;
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("start_pos", start_pos);
                    jsonObject.put("end_pos", end_pos);
                    jsonObject.put("content", temp);
                    return jsonObject;
                }
            }
        }
        return null;
    }

    /**
     * 替换所有的表格
     *
     * @param xwpfTableList
     * @param params
     */
    public static void replaceInTables(List<XWPFTable> xwpfTableList, Map<String, String> params) {
        for (XWPFTable table : xwpfTableList) {
            replaceInTable(table, params);
        }
    }

    /**
     * 替换一个表格中的所有行
     *
     * @param xwpfTable
     * @param params
     */
    public static void replaceInTable(XWPFTable xwpfTable, Map<String, String> params) {
        List<XWPFTableRow> rows = xwpfTable.getRows();
        replaceInRows(rows, params);
    }


    /**
     * 替换表格中的一行
     *
     * @param rows
     * @param params
     */
    public static void replaceInRows(List<XWPFTableRow> rows, Map<String, String> params) {
        for (int i = 0; i < rows.size(); i++) {
            XWPFTableRow row = rows.get(i);
            replaceInCells(row.getTableCells(), params);
        }
    }

    /**
     * 替换一行中所有的单元格
     *
     * @param xwpfTableCellList
     * @param params
     */
    public static void replaceInCells(List<XWPFTableCell> xwpfTableCellList, Map<String, String> params) {
        if (xwpfTableCellList.size() > 1) {
            for (int i = 1; i < xwpfTableCellList.size(); i += 2) {
                XWPFTableCell xwpfTableCell = xwpfTableCellList.get(i);
                if (StrUtil.isBlank(xwpfTableCell.getText())) {
                    XWPFTableCell beforeCell = xwpfTableCellList.get(i - 1);
                    if (StrUtil.isNotBlank(beforeCell.getText()) && params.containsKey(beforeCell.getText())) {
                        String key = xwpfTableCellList.get(i - 1).getText();
                        List<XWPFParagraph> list = xwpfTableCell.getParagraphs();
                        XWPFParagraph xwpfParagraph = list.get(0);
                        XWPFRun xwpfRun = xwpfParagraph.insertNewRun(0);
                        xwpfRun.setBold(Boolean.TRUE);
                        xwpfRun.setColor("D3D3D3");
                        xwpfRun.setText(params.get(key));
                    }
                }

            }
        }
    }

    private static HWPFDocument openDocument(String path) throws Exception {
        HWPFDocument document = new HWPFDocument(new POIFSFileSystem(new File(path)));
        return document;
    }

    private static XWPFDocument openDocxDocument(String path) throws Exception {
        XWPFDocument document = new XWPFDocument(new FileInputStream(new File(path)));
        return document;
    }

    private static void saveDocument(HWPFDocument doc, String path) {
        try (FileOutputStream out = new FileOutputStream(path)) {
            doc.write(out);
        } catch (IOException e) {
            log.error("保存文档失败：{}", ExceptionUtil.stacktraceToString(e));
        }
    }

    private static void saveDocxDocument(XWPFDocument doc, String path) {
        try (FileOutputStream out = new FileOutputStream(path)) {
            doc.write(out);
        } catch (IOException e) {
            log.error("保存文档失败：{}", ExceptionUtil.stacktraceToString(e));
        }
    }

    private static void highLight(XWPFParagraph p, XWPFRun run) {
        CTRPr pRpr = getRunCTRPr(p, run);
        CTHighlight highlight = pRpr.addNewHighlight();
        highlight.setVal(STHighlightColor.LIGHT_GRAY);
    }

    public static CTRPr getRunCTRPr(XWPFParagraph p, XWPFRun pRun) {
        CTRPr pRpr;
        if (pRun.getCTR() != null) {
            pRpr = pRun.getCTR().getRPr();
            if (pRpr == null) {
                pRpr = pRun.getCTR().addNewRPr();
            }
        } else {
            pRpr = p.getCTP().addNewR().addNewRPr();
        }
        return pRpr;
    }

    /**
     * 替换 xml字符串中 {{aaa}}
     *
     * @param xmlContent 字符串模板
     * @param map        map
     * @return
     */
    public static String replaceXmlElementValue(String xmlContent, Map<String, String> map) {
        if (StrUtil.isBlank(xmlContent)) {
            return xmlContent;
        }
        //定义{{开头 ，}}结尾的占位符
        PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("{{", "}}");
        //调用替换
        return propertyPlaceholderHelper.replacePlaceholders(xmlContent, map::get);
    }
}
