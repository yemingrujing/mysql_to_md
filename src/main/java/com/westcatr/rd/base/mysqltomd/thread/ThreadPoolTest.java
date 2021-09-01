package com.westcatr.rd.base.mysqltomd.thread;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author nanmu@taomz.com
 * @version V1.0
 * @title : ThreadPoolTest
 * @Package : com.westcatr.rd.base.mysqltomd.thread
 * @Description: 线程池测试
 * @date 2021/7/16 9:48
 **/
public class ThreadPoolTest {

	/** 信号量 */
	private Semaphore semaphore = new Semaphore(0); // 1

	/** 线程池 */
	private ThreadPoolExecutor pool = new ThreadPoolExecutor(3, 5, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(3));

	/** Future */
	private Future<String> future;

	public void test() {
		future = pool.submit(() -> {
			String result;
			try {
				semaphore.acquire();
				result = "ok";
			} catch (InterruptedException e) {
				result = "interrupted";
			}
			return result;
		});

		String result = "timeout";
		try {
			// 等待3s
			result = future.get(3, TimeUnit.SECONDS);
		}catch (Exception e) {
			System.out.println("超时异常");
		}
		future.cancel(true);

		// 删除线程池中任务
		boolean cancelResult = future.cancel(true);

		System.out.println("result is " + result);
		System.out.println("删除结果："  +cancelResult);
		System.out.println("当前active线程数：" +pool.getActiveCount());
	}

	public static void main(String[] args) {
		ThreadPoolTest o = new ThreadPoolTest();
		o.test();
	}
}
