package com.westcatr.rd.base.mysqltomd.thread;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nanmu@taomz.com
 * @version V1.0
 * @title : TreadTest
 * @Package : com.westcatr.rd.base.mysqltomd
 * @Description: 测试
 * @date 2021/7/15 19:12
 **/
public class ThreadTest implements Runnable {

	private ThreadID var;
	private Map map;

	public ThreadTest(ThreadID v, Map map) {
		this.var = v;
		this.map = map;
	}

	@Override
	public void run() {
		try {
			map.put(var.getThreadId(), Thread.currentThread());
			print("var currentThreadId = " + Thread.currentThread().getId());
			print("var getThreadId = " + var.getThreadId());
			Thread.sleep(2000);
			print("var getThreadId = " + var.getThreadId());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ThreadID tid = new ThreadID();
		ConcurrentHashMap<Integer, Thread> hashMap = new ConcurrentHashMap();
		ThreadTest shared = new ThreadTest(tid, hashMap);

		try {
			Thread threadA = new Thread(shared, "threadA");
			threadA.start();
			Thread.sleep(500);

			Thread threadB = new Thread(shared, "threadB");
			threadB.start();
			Thread.sleep(500);

			Thread threadC = new Thread(shared, "threadC");
			threadC.start();
			Thread.sleep(500);

			Thread needClosedThread = hashMap.get(10001);
			if (Objects.nonNull(needClosedThread)) {
				needClosedThread.interrupt();
			}
			hashMap.remove(10001);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void print(String msg) {
		String name = Thread.currentThread().getName();
		System.out.println(name + ": " + msg);
	}
}

class ThreadID extends ThreadLocal {
	private int nextId;

	public ThreadID() {
		nextId = 10001;
	}

	private synchronized Integer getNewId() {
		return nextId++;
	}

	protected Object initialValue() {
		return getNewId();
	}

	protected int getThreadId() {
		return (Integer) get();
	}
}
