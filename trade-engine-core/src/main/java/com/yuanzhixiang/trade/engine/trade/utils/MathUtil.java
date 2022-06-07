package com.yuanzhixiang.trade.engine.trade.utils;

import java.util.Random;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/25 19:12
 */
public class MathUtil {

    private static final Random random = new Random();

    /**
     * 随机数量，随机出来的数字是 100 的整数倍
     *
     * @param bound 随机出来的数字不会大于该数字
     * @return 随机出来的数字
     */
    public static int randomNumberMultiplesOfHundred(int bound) {
        int number = random.nextInt(bound);
        // 在随机出来的数字上加 100
        return (number / 100 + 1) * 100;
    }

    /**
     * 随机一个数字，但是数字不会是 0
     *
     * @param bound 随机数字的上线
     * @param max   最大不超过这个数字
     * @return 随机出来的数字
     */
    public static int randomInt(int bound, int max) {
        if (bound < max) {
            return bound;
        }

        int randomNumber = random.nextInt(bound);
        if (randomNumber > max) {
            return max;
        }
        return randomNumber == 0 ? randomNumber + 1 : randomNumber;
    }

    /**
     * 随机一个 boolean，50% true，50% false
     *
     * @return true / false
     */
    public static boolean randomBoolean() {
        return random.nextInt(10) < 5;
    }

}
