package com.westcatr.rd.base.mysqltomd.word;

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
		params.put("乙方：", "乙方：{{companyName}}");
		WordDateUtil.replaceDocxText("D:\\data\\补充协议（续展）20220111.docx", params);
	}
}
