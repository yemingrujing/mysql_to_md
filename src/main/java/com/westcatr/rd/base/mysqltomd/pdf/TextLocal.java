package com.westcatr.rd.base.mysqltomd.pdf;

import lombok.Getter;
import lombok.Setter;

/**
 * @author nanmu@taomz.com
 * @version V1.0
 * @title : TextLocal
 * @Package : com.westcatr.rd.base.mysqltomd.pdf
 * @Description:
 * @date 2022/3/24 12:22
 **/
@Setter
@Getter
public class TextLocal {

	/**
	 * 关键字在PDF中的X坐标
	 */
	private float x;

	/**
	 * 关键字在PDF中的Y坐标
	 */
	private float y;

	/**
	 * 关键字在PDF中的页码
	 */
	private int pageNum;

	/**
	 * 关键字在PDF中的显示出来的长度
	 */
	private float length;

	/**
	 * 具体内容
	 */
	private String content;
}
