package com.westcatr.rd.base.mysqltomd.pdf;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author nanmu@taomz.com
 * @version V1.0
 * @title : PDFDomainVO
 * @Package : com.westcatr.rd.base.mysqltomd.pdf
 * @Description:
 * @date 2022/3/14 15:11
 **/
@Setter
@Getter
public class PDFDomainVO {

	private Integer id;

	/**
	 * 操作时间
	 */
	private Date time;

	/**
	 * 文件名
	 */
	private String fileName;

	/**
	 * 文件大小
	 */
	private String fileSize;

	/**
	 * 文件类型
	 */
	private String fileType;

	/**
	 * 操作详情
	 */
	private String details;

	/**
	 * pdf中内容
	 */
	private String content;

	/**
	 * 输出路径(保存路径)
	 */
	private String outPutFile;

	/**
	 * 要操作的pdf路径
	 */
	private String inPutFile;

	/**
	 * 需要替换的文本
	 */
	private String strToFind;

	/**
	 * 替换的文本
	 */
	private String message;

	/**
	 * 图片路径
	 */
	private String imageFile;

	/**
	 * 图片集合
	 */
	private String imageList;

	/**
	 * 指定页码
	 */
	private Integer pageNum;

	/**
	 * 总页数
	 */
	private Integer pages;

	private Integer rid;

	/**
	 * 操作页数
	 */
	private Integer pageOperation;

	/**
	 * 开始页
	 */
	private Integer pageStart;

	/**
	 * 结束页
	 */
	private Integer pageEnd;

	/**
	 * 位置:X,Y
	 */
	private String position;

	/**
	 * 操作后文件大小
	 */
	private String fileSizeAfter;

	/**
	 * 状态
	 */
	private Integer status;

	/**
	 * 操作后页码
	 */
	private Integer afterPages;

	/**
	 * 图片大小
	 */
	private Integer imgSize;
}
