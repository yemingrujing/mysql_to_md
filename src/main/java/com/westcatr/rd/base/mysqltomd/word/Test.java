package com.westcatr.rd.base.mysqltomd.word;

import cn.hutool.core.date.DateUtil;
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
		Map<String, String> params = new HashMap<>();
		params.put("合同编号", "{{contractCode}}");
		params.put("年", "{{currentYear}}");
		params.put("月", "{{currentMonth}}");
		params.put("日", "{{nowDay}}");
		params.put("乙方", "{{companyName}}");
		params.put("法定代表人", "{{personName}}");
		params.put("联系邮箱", "{{email}}");
		params.put("注册地", "{{address}}");
		WordDataUtil.replaceDocxText("D:\\data\\补充协议（续展）20220111.docx", params, "D:\\data\\补充协议（续展）20220111_11.docx");

		Map<String, Object> replaceParam = new HashMap<>();
		Style style = Style.builder().buildColor("000000").buildBold().build();
		style.setHighlightColor(XWPFHighlightColor.WHITE);
		Style.builder().build().setHighlightColor(XWPFHighlightColor.WHITE);
		replaceParam.put("companyName", Texts.of("上海耽美艺术集团").style(style).create());
		replaceParam.put("personName", Texts.of("光紫宸").style(style).create());
		replaceParam.put("email", Texts.of("1021167123@qq.com").style(style).create());
		replaceParam.put("address", Texts.of("上海九个枣有限公司").style(style).create());
		replaceParam.put("contractCode", Texts.of("HT343494354345").style(style).create());
		replaceParam.put("currentYear", Texts.of(String.valueOf(DateUtil.year(DateUtil.date())).substring(2)).style(style).create());
		replaceParam.put("currentMonth", Texts.of(String.valueOf(DateUtil.month(DateUtil.date()))).style(style).create());
		replaceParam.put("nowDay", Texts.of(String.valueOf(DateUtil.dayOfMonth(DateUtil.date()))).style(style).create());
		WordDataUtil.replaceLabel("D:\\data\\补充协议（续展）20220111_11.docx", replaceParam);
	}
}
