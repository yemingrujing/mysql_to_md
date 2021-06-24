package com.westcatr.rd.base.mysqltomd.coupon_demo;

import com.alibaba.fastjson.JSON;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nanmu@taomz.com
 * @version V1.0
 * @title : TestOptimumCoupon
 * @Package : com.westcatr.rd.base.mysqltomd.coupon_demo
 * @Description: 测试
 * @date 2021/6/22 14:50
 **/
public class TestOptimumCoupon {

	public static void main(String[] args) {
		Coupon coupon1 = new Coupon(1, 2, new BigDecimal(50), "DISCOUNT");
		Coupon coupon2 = new Coupon(2, 2, new BigDecimal(70), "DISCOUNT");
		Coupon coupon3 = new Coupon(3, 2, new BigDecimal(10), "DISCOUNT");
		Coupon coupon4 = new Coupon(4, 2, new BigDecimal(5), "DISCOUNT");
		Coupon coupon5 = new Coupon(5, 2, new BigDecimal(20), "DISCOUNT");

		Coupon coupon6 = new Coupon(6, 3, new BigDecimal(100), "DISCOUNT");
		Coupon coupon7 = new Coupon(7, 3, new BigDecimal(80), "DISCOUNT");
		Coupon coupon8 = new Coupon(8, 3, new BigDecimal(66), "DISCOUNT");
		Coupon coupon9 = new Coupon(9, 3, new BigDecimal(25), "DISCOUNT");
		Coupon coupon10 = new Coupon(10, 3, new BigDecimal(1), "DISCOUNT");
		// 用户优惠券列表
		List<Coupon> couponList = new ArrayList<>();
		couponList.add(coupon1);
		couponList.add(coupon2);
		couponList.add(coupon3);
		couponList.add(coupon4);
		couponList.add(coupon5);

		couponList.add(coupon6);
		couponList.add(coupon7);
		couponList.add(coupon8);
		couponList.add(coupon9);
		couponList.add(coupon10);
		// 支付金额
		BigDecimal money = new BigDecimal(125);
		List<Coupon> optimumList = Combinations.getOptimumList(couponList, money);
		System.out.println(JSON.toJSONString(optimumList));
	}
}
