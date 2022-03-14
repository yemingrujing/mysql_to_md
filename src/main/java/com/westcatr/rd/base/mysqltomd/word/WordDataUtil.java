package com.westcatr.rd.base.mysqltomd.word;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHighlight;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHighlightColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
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
		List<XWPFParagraph> list = doc.getParagraphs();
		replaceInAllParagraphs(list, params);
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
				if (paragraph.getText().contains(key)) {
					replaceAtParagraph(paragraph, key, params.get(key));
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
		Map<String, Integer> pos_map = findSubRunPosAtParagraph(xwpfParagraph, oldString);
		if (pos_map != null) {
			List<XWPFRun> runs = xwpfParagraph.getRuns();
			XWPFRun modelRun = runs.get(pos_map.get("end_pos"));
			XWPFRun xwpfRun = xwpfParagraph.insertNewRun(pos_map.get("end_pos") + 1);
			xwpfRun.setText(newString);
			if (modelRun.getFontSizeAsDouble().intValue() != -1) {
				//默认值是五号字体，但五号字体getFontSize()时，返回-1
				xwpfRun.setFontSize(modelRun.getFontSizeAsDouble());
			}
			xwpfRun.setColor("D3D3D3");
			// 高亮显示
			highLight(xwpfParagraph, xwpfRun);
			xwpfRun.setFontFamily(modelRun.getFontFamily());
		}
	}

	/**
	 * 找到段落中子串的起始XWPFRun下标和终止XWPFRun的下标
	 *
	 * @param xwpFParagraph
	 * @param oldString
	 * @return
	 */
	public static Map<String, Integer> findSubRunPosAtParagraph(XWPFParagraph xwpFParagraph, String oldString) {
		List<XWPFRun> runs = xwpFParagraph.getRuns();
		int start_pos;
		int end_pos;
		for (int i = 0; i < runs.size(); i++) {
			start_pos = i;
			for (int j = i; j < runs.size(); j++) {
				if (runs.get(j).getText(runs.get(j).getTextPosition()) == null) continue;
				if (j < runs.size() - 1 && runs.get(j + 1).getText(runs.get(j + 1).getTextPosition()) != null) {
					String nextText = runs.get(j + 1).getText(runs.get(j + 1).getTextPosition());
					if (!nextText.contains("：") && StrUtil.isNotBlank(nextText.trim())) continue;
					if (nextText.length() >= REPLACE_PREFIX.length() && nextText.substring(0, REPLACE_PREFIX.length()).contains(REPLACE_PREFIX)) continue;
				}
				String temp = runs.get(j).getText(runs.get(j).getTextPosition()).trim();
				if (temp.equals(oldString)) {
					end_pos = j;
					Map<String, Integer> map = new HashMap<>();
					map.put("start_pos", start_pos);
					map.put("end_pos", end_pos);
					return map;
				}
			}
		}
		return null;
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
}
