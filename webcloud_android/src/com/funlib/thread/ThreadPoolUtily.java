package com.funlib.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtily {

	private static int RUNNING_THREAD_MAX_NUM = 6; // 同时运行的线程最大个数
	private static int THREAD_IN_POOL_MAX_NUM = 10; // 线程池中最大线程个数
	private static int THREAD_IDLE_TIME = 1; // 线程空闲时间，到期，被干掉，应该尽量小
	private static TimeUnit IDLE_TIME_UNIT = TimeUnit.MILLISECONDS;

	private static BlockingQueue<Runnable> sQueue;
	private static ThreadPoolExecutor sExecutor;

	private ThreadPoolUtily() {

	}

	public static void init() {

		sQueue = new LinkedBlockingQueue<Runnable>();// 使用动态增加队列，防止线程超过最大数后，线程池抛出异常　
		sExecutor = new ThreadPoolExecutor(RUNNING_THREAD_MAX_NUM,
				THREAD_IN_POOL_MAX_NUM, THREAD_IDLE_TIME, IDLE_TIME_UNIT,
				sQueue);
	}

	/**
	 * 执行新任务，任务可能不会立即执行
	 * 
	 * @param task
	 */
	public static void executorTask(Runnable task) {
		sExecutor.execute(task);
	}

	/**
	 * 退出线程池
	 */
	public static void quit() {
		sExecutor.shutdown();
	}

	/**
	 * 如果任务尚未开始，remove
	 * 
	 * @param task
	 */
	public static void removeTask(Runnable task) {
		sExecutor.remove(task);
		sExecutor.getQueue().clear();
	}
}
