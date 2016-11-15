package com.foo.customview;

import java.util.Arrays;

/**
 * 游戏：点兵点将
 * <p>
 * 有 n 个人站成一排，从左到右依次编号为 1, 2, 3 ... n。现数到 m ，若 m > n，则数到 n 之后从右往左第二个为 n + 1,
 * 从右往左第三个为 n + 2,以此类推，则第 m 个数指向的人编号为？
 * <p>
 * 扩展：
 * 用于解决需要从固定的范围中往返获取其数据时的计算
 *
 * @author: Major
 * @since: 2016/11/15 1:03
 */
public class Main {

    public static void main(String[] args) {

        getNo(3, 10);
        getNo(5, 12);
    }

    /**
     * @param n 总人数
     * @param m 数到 m
     * @return 返回数到第 m 的人的序号
     */
    public static int getNo(int n, int m) {
        int srcLen = n * 2 - 2;
        int[] src = new int[srcLen];
        src[0] = 1; // 元素以 1 开始，但其角标为 0
        for (int i = 1; i < src.length; i++) {
            if (i < n) {
                src[i] = src[i - 1] + 1;
            } else {
                src[i] = src[i - 1] - 1;
            }
        }

        System.out.println(Arrays.toString(src));

        int mod = m % (srcLen);
        int result;
        if (mod == 0) {
            result = n;
        } else {
            result = src[mod - 1];
        }

        System.out.println("数到第 " + m + " 的编号为 " + result);
        return result;
    }
}
