package com.westcatr.rd.base.mysqltomd.word;

import com.deepoove.poi.data.Texts;
import com.deepoove.poi.data.style.Style;
import com.deepoove.poi.xwpf.XWPFHighlightColor;

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
//		WordDateUtil.searchKeyWord("D:\\data\\补充协议（续展）20220111.docx");
		Map<String, String> params = new HashMap<>();
		params.put("乙方：", "{{companyName}}");
		params.put("代表人：", "{{personName}}");
		params.put("联系邮箱：", "{{email}}");
		WordDataUtil.replaceDocxText("D:\\data\\补充协议（续展）20220111.docx", params, "D:\\data\\补充协议（续展）20220111_11.docx");

		Map<String, Object> replaceParam = new HashMap<>();
		Style style = Style.builder().buildColor("000000").buildBold().build();
		style.setHighlightColor(XWPFHighlightColor.WHITE);
		Style.builder().build().setHighlightColor(XWPFHighlightColor.WHITE);
		replaceParam.put("companyName", Texts.of("上海耽美艺术集团").style(style).create());
		replaceParam.put("personName", Texts.of("光紫宸").style(style).create());
		replaceParam.put("email", Texts.of("1021167123@qq.com").style(style).create());
		WordDataUtil.replaceLabel("D:\\data\\补充协议（续展）20220111_11.docx", replaceParam);
	}
}
