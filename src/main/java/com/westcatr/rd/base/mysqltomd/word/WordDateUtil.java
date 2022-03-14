package com.westcatr.rd.base.mysqltomd.word;

import cn.hutool.core.exceptions.ExceptionUtil;
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
public class WordDateUtil {

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

	public static XWPFDocument replaceDocxText(String path, Map<String, String> params) throws Exception {
		XWPFDocument doc = openDocxDocument(path);
		List<XWPFParagraph> list = doc.getParagraphs();
		replaceInAllParagraphs(list, params);
		saveDocxDocument(doc, path);
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
					replaceInParagraph(paragraph, key, params.get(key));
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
					if (text.trim().equals(oldString)
							|| (text.trim().length() > (oldString.length() + REPLACE_PREFIX.length()) && !text.trim().substring(0, oldString.length() + REPLACE_PREFIX.length()).contains(REPLACE_PREFIX))) {
						text = text.replace(oldString, newString);
						r.setText(text, 0);
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
}
