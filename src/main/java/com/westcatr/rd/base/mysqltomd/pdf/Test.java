package com.westcatr.rd.base.mysqltomd.pdf;

import com.westcatr.rd.base.mysqltomd.word.WordDataUtil;

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

	public static void main(String[] args) {
		PDFDomainVO pdfDomainVO = new PDFDomainVO();
		pdfDomainVO.setInPutFile("D:\\data\\工厂入驻协议.pdf");
		pdfDomainVO.setOutPutFile("D:\\data\\工厂入驻协议1.pdf");
		pdfDomainVO.setPageNum(3);
		pdfDomainVO.setMessage("测试=========================");
		PDFDataUtil.addPageContent(pdfDomainVO);
	}
}
