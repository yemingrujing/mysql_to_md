package com.westcatr.rd.base.mysqltomd.coupon_demo;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author nanmu@taomz.com
 * @version V1.0
 * @title : Combinations
 * @Package : com.westcatr.rd.base.mysqltomd.coupon_demo
 * @Description: 满减算法
 * @date 2021/6/22 14:48
 **/
public class Combinations {

	/**
	 * 所有的组合
	 *
	 * @param arr
	 * @return
	 */
	public static <R extends Comparable<R>> List<List<R>> allCombination(List<R> arr) {
		int length = arr.size();
		// 组合由多少个元素组成的
		List<List<R>> result = new ArrayList<>();
		int i = 1;
		while (i <= length) {
			// 生成i个元素的组合
			result.addAll(combinationSelect(arr, i));
			i++;
		}
		return result;
	}

	/**
	 * 由n个元素组成的组合
	 *
	 * @param arr 数组
	 * @param i 组合的元素个数
	 * @return 组合集合
	 */
	private static <R extends Comparable<R>> List<List<R>> combinationSelect(List<R> arr, int i) {
		return new DFSCombination<>(arr, i).select();
	}

	public static <R extends Comparable<R>> HashMap<String, BigDecimal> getCombinationMap(List<Coupon> couponList, BigDecimal amount) {
		List<List<Coupon>> allCombination = allCombination(couponList);
		HashMap<String, BigDecimal> map = new HashMap<>();
		for (List<Coupon> coupons : allCombination) {
			List<String> collect = coupons.stream().map(coupon -> String.valueOf(coupon.getId()))
					.collect(Collectors.toList());
			BigDecimal total = coupons.stream().map(Coupon::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
			if (amount.compareTo(total) >= 0) {
				map.put(String.join("-", collect), total);
			}
		}
		return map;
	}

	// 筛选最接近支付金额的结果
	public static String binarysearchKey(HashMap<String, BigDecimal> map, BigDecimal targetNum) {
		// 将map.entrySet()转换成list
		List<Map.Entry<String, BigDecimal>> list = new ArrayList<>(map.entrySet());
		// 通过比较器来实现排序
		Collections.sort(list, Comparator.comparing(Map.Entry::getValue));
		int left = 0, right = 0;
		for (right = list.size() - 1; left != right;) {
			int midIndex = (right + left) / 2;
			int mid = (right - left);
			BigDecimal midValue = list.get(midIndex).getValue();
			if (targetNum.compareTo(midValue) == 0) {
				return list.get(midIndex).getKey();
			}

			if (targetNum.compareTo(midValue) == 1) {
				left = midIndex;
			} else {
				right = midIndex;
			}
			if (mid <= 1) {
				break;
			}
		}
		BigDecimal rightnum = list.get(right).getValue();
		BigDecimal leftnum = list.get(left).getValue();
		// 如果一个大于支付金额，一个小于支付金额，优先返回大于支付金额的结果
		if (rightnum.compareTo(targetNum) == 1 &&leftnum.compareTo(targetNum) == -1) {
			return list.get(right).getKey();
		}
		BigDecimal rightiffVal = rightnum.subtract(targetNum);
		BigDecimal leftDiffVal = leftnum.subtract(targetNum);
		return Math.abs(leftDiffVal.intValue()) > Math.abs(rightiffVal.intValue()) ? list.get(right).getKey() : list.get(left).getKey();
	}

	// 筛选最接近支付金额的结果
	public static BigDecimal binarysearchKey(Object[] array, BigDecimal targetNum) {
		Arrays.sort(array);
		int left = 0, right = 0;
		for (right = array.length - 1; left != right;) {
			int midIndex = (right + left) / 2;
			int mid = (right - left);
			BigDecimal midValue = ((BigDecimal) array[midIndex]);
			if (targetNum.compareTo(midValue) == 0) {
				return midValue;
			}

			if (targetNum.compareTo(midValue) == 1) {
				left = midIndex;
			} else {
				right = midIndex;
			}
			if (mid <= 1) {
				break;
			}
		}
		BigDecimal rightnum = (BigDecimal) array[right];
		BigDecimal leftnum = (BigDecimal) array[left];
		// 如果一个大于支付金额，一个小于支付金额，优先返回大于支付金额的结果
		if (rightnum.compareTo(targetNum) == 1 &&leftnum.compareTo(targetNum) == -1) {
			return rightnum;
		}
		BigDecimal rightiffVal = rightnum.subtract(targetNum);
		BigDecimal leftDiffVal = leftnum.subtract(targetNum);
		return Math.abs(leftDiffVal.intValue()) > Math.abs(rightiffVal.intValue()) ? rightnum : leftnum;
	}

	public static List<Coupon> getOptimumList(List<Coupon> couponList, BigDecimal amount) {
		// endResultMap 同批次现金券最优解，折扣券最优解，代金券最优解
		// endResultMap key 优惠券id字符串 value 最优解优惠金额
		HashMap<String, BigDecimal> endResultMap = new HashMap<>();
		// 现金券集合
		Map<Integer, List<Coupon>> cashMap = new HashMap<>();

		for (Coupon coupon : couponList) {
			// 如果是现金券，对不同批次进行筛选
			if (cashMap.get(coupon.getRelateId()) == null) {
				List<Coupon> cacheList = new ArrayList<>();
				cacheList.add(coupon);
				cashMap.put(coupon.getRelateId(), cacheList);
			} else {
				List<Coupon> cacheList = cashMap.get(coupon.getRelateId());
				cacheList.add(coupon);
			}
		}

		// 判断现金券map长度 现金券处理开始
		if (!cashMap.isEmpty()) {
			// 筛选现金券不同批次最优解
			for (Map.Entry<Integer, List<Coupon>> m : cashMap.entrySet()) {
				// 获取当前批次所有组合
				HashMap<String, BigDecimal> map = Combinations.getCombinationMap(m.getValue(), amount);
				// 获取当前批次最优解
				String result = Combinations.binarysearchKey(map, amount);
				// 根据value找最优解优惠券id,放入最优解map
				getOptimumMap4SameBatch(map, result, endResultMap);
			}
		}

		// 获取最终结果
		String couponDetailStr = Combinations.binarysearchKey(endResultMap, amount);
		// 判断是否为空
		List<String> couponDetailIdList = new ArrayList<>();
		if (StringUtils.isNotBlank(couponDetailStr)) {
			if (couponDetailStr.contains("-")) {
				String[] couponDetailArr = couponDetailStr.split("-");
				for (String str : couponDetailArr) {
					couponDetailIdList.add(str);
				}
			} else {
				couponDetailIdList.add(couponDetailStr);
			}
		}
		List<Coupon> optimumList = new ArrayList<>();
		// 循环用户优惠券列表，进行比较
		for (Coupon coupon : couponList) {
			for (String str : couponDetailIdList) {
				if (coupon.getId() == Integer.parseInt(str)) {
					optimumList.add(coupon);
				}
			}
		}
		return optimumList;
	}

	private static void getOptimumMap4SameBatch(HashMap<String, BigDecimal> map, String result,
												HashMap<String, BigDecimal> endResultMap) {
		for (Map.Entry<String, BigDecimal> m : map.entrySet()) {
			if (m.getKey().equals(result)) {
				endResultMap.put(m.getKey(), m.getValue());
				return;
			}
		}
	}

	/**
	 * DFS实现组合
	 */
	private static class DFSCombination<R extends Comparable<R>> {

		// 标记元素是否已被组合
		private Set<R> bookSet = new HashSet<>();
		private List<R> arr;
		private int n;
		private Map<Integer, R> bucks;
		private List<List<R>> result = new ArrayList<>();

		public DFSCombination(List<R> arr, int n) {
			this.arr = arr;
			this.n = n;
			bucks = new LinkedHashMap<>();
		}

		private void dfs(int index) {
			if (index == n) {
				// 说明组合数满了
				result.add(composite());
				return;
			}

			for (int i = 0; i < arr.size(); i++) {
				R element = arr.get(i);
				if (!bookSet.contains(element)) {
					if (index > 0) {
						// 保证一个组合的顺序,从小到大的顺序
						R lastElement = bucks.get(index - 1);
						if (lastElement.compareTo(element) > 0) {
							continue;
						}
					}
					// 第几个位置放置什么元素
					bucks.put(index, element);
					bookSet.add(element);
					dfs(index + 1);
					bookSet.remove(element);
				}
			}
		}

		public List<List<R>> select() {
			dfs(0);
			return result;
		}

		private List<R> composite() {
			return bucks.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
		}
	}
}
