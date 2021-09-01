package com.westcatr.rd.base.mysqltomd.thread;

/**
 * @author nanmu@taomz.com
 * @version V1.0
 * @title : HandlePromotionFailure
 * @Package : com.westcatr.rd.base.mysqltomd.thread
 * @Description:
 * @date 2021/8/3 16:28
 **/
public class HandlePromotionFailure {

	private static final int _1M = 1024 * 1024;

	public static void testHandlePromotion() {
		byte[] allocation1, allocation2, allocation3, allocation4;

		allocation1 = new byte[2 * _1M];
		allocation2 = new byte[2 * _1M];
		allocation3 = new byte[4 * _1M];
		allocation4 = new byte[4 * _1M];
	}

	public static void main(String[] args) {
		testHandlePromotion();
	}
}
