package com.westcatr.rd.base.mysqltomd.pdf;

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
		pdfDomainVO.setInPutFile("D:\\data\\html\\394172a540c34a62baf9214a4caf6eb3.pdf");
		pdfDomainVO.setOutPutFile("D:\\data\\html\\394172a540c34a62baf9214a4caf6eb3_1.pdf");
		pdfDomainVO.setPageNum(3);
		pdfDomainVO.setMessage("测试=========================");
		PDFDataUtil.addPageContent(pdfDomainVO);
	}
}
