package com.yuanzhixiang.trade.engine.trade.domain.model.util;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import com.google.common.util.concurrent.AtomicDouble;

import cn.hutool.core.util.RandomUtil;
import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.exception.OptimisticLockerException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/01 09:55
 */
@Slf4j
public class ThreadUtil {

    /**
     * 判断是否使用的是 h2 的 db
     *
     * @return true 是/ false 不是
     */
    private static boolean isH2DB() {
        try {
            Class<?> aClass = ThreadUtil.class.getClassLoader().loadClass("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            return false;
        }

        return true;
    }

    /**
     * 并发执行任务
     *
     * @param runnable 需要被执行的任务
     */
    public static void asyncTask(Runnable runnable) throws InterruptedException {
        // 由于 h2 db 在高并发场景会出现数据不正确，所以这里我们降低并发数
        if (isH2DB()) {
            asyncTask(5, runnable);
        } else {
            asyncTask(100, runnable);
        }

    }

    /**
     * 并发执行任务
     *
     * @param threadCount 并发线程数
     * @param runnable    需要被执行的任务
     */
    public static void asyncTask(int threadCount, Runnable runnable) throws InterruptedException {
        // 创建一个倒计时器
        CountDownLatch countDownLatch = new CountDownLatch(threadCount + 1);

        ArrayList<Thread> threads = new ArrayList<>();
        // 创建多线程
        for (int i = 0; i < threadCount; i++) {
            threads.add(new Thread(() -> {
                // 倒计时器减一
                countDownLatch.countDown();
                // 等待倒计时到 0
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    log.error("", e);
                }
                runnable.run();
            }));
        }

        // 开启线程
        for (Thread thread : threads) {
            thread.start();
        }

        // 启动任务
        countDownLatch.countDown();

        // 等待线程执行完任务
        for (Thread thread : threads) {
            thread.join();
        }
    }

    /**
     * 同步任务，提供 long 值并返回 long 值的加和
     *
     * @param predicate 消费 long 值的任务
     * @return long 值的加和
     */
    public static long syncTaskProvideLong(Predicate<Long> predicate) {

        long sumQuantity = 0;
        for (int i = 0; i < 100; i++) {
            // 随机一个数量
            long quantity = RandomUtil.randomLong(0, 100);
            // 执行任务
            if (predicate.test(quantity)) {
                // 加总数量
                sumQuantity += quantity;
            }
        }

        return sumQuantity;
    }


    /**
     * 并发任务，提供 long 值并返回 long 值的加和
     *
     * @param predicate 需要并发执行的任务
     * @return long 值的加和
     * @throws InterruptedException 线程被打断异常
     */
    public static long asyncTaskProvideLong(Predicate<Long> predicate) throws InterruptedException {
        AtomicLong atomicLong = new AtomicLong(0);
        ThreadUtil.asyncTask(() -> {
            // 随机一个数量
            long quantity = RandomUtil.randomLong(0, 100);

            while (true) {
                boolean result = false;
                // 获取执行结果
                try {
                    result = predicate.test(quantity);
                } catch (OptimisticLockerException e) {
                    // 忽略乐观锁异常
                } catch (Exception e) {
                    log.error("", e);
                }

                // 执行成功则跳出循环
                if (result) {
                    // 加总数量
                    atomicLong.addAndGet(quantity);
                    break;
                }
            }
        });
        return atomicLong.get();
    }

    /**
     * 同步任务，提供 double 值并返回 double 值的加和
     *
     * @param predicate 消费 double 值的任务
     * @return double 值的加和
     */
    public static double syncTaskProvideDouble(Predicate<Double> predicate) {
        double sumDouble = 0;
        for (int i = 0; i < 100; i++) {
            // 随机一个 double 数
            double doubleValue = RandomUtil.randomDouble(4, RoundingMode.HALF_UP) * 100;
            // 执行任务
            if (predicate.test(doubleValue)) {
                // 加总数量
                sumDouble += doubleValue;
            }
        }

        return sumDouble;
    }

    /**
     * 并发任务，提供 double 值并返回 double 值的加和
     *
     * @param predicate 需要并发执行的任务
     * @return double 值的加和
     * @throws InterruptedException 线程被打断异常
     */
    public static double asyncTaskProvideDouble(Predicate<Double> predicate) throws InterruptedException {
        AtomicDouble atomicDouble = new AtomicDouble(0);
        ThreadUtil.asyncTask(() -> {
            double doubleValue = RandomUtil.randomDouble(4, RoundingMode.HALF_UP) * 100;

            while (true) {
                boolean result = false;
                // 获取执行结果
                try {
                    result = predicate.test(doubleValue);
                } catch (OptimisticLockerException e) {
                    // 忽略乐观锁异常
                } catch (Exception e) {
                    log.error("", e);
                }

                // 执行成功则跳出循环
                if (result) {
                    // 加总数量
                    atomicDouble.addAndGet(doubleValue);
                    break;
                }
            }
        });
        return atomicDouble.get();
    }

}
