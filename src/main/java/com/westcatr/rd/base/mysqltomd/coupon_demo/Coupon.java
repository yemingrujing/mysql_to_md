package com.westcatr.rd.base.mysqltomd.coupon_demo;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author nanmu@taomz.com
 * @version V1.0
 * @title : Coupon
 * @Package : com.westcatr.rd.base.mysqltomd.coupon_demo
 * @Description:
 * @date 2021/6/22 14:45
 **/
@Setter
@Getter
public class Coupon implements Comparable<Coupon> {

	private int id;

	private int relateId;

	private String name;

	private BigDecimal amount;

	private String couponType;

	public Coupon(int id, int relateId, BigDecimal amount, String couponType) {
		this.id = id;
		this.relateId = relateId;
		this.amount = amount;
		this.couponType = couponType;
	}

	@Override
	public int compareTo(Coupon o) {
		return amount.compareTo(o.amount);
	}

	@Override
	public String toString() {
		return "[" + "id=" + id + ", amount=" + amount + ']';
	}

}
