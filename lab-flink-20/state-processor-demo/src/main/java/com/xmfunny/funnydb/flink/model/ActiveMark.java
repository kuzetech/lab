package com.xmfunny.funnydb.flink.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 活跃标识
 * 相关具体说明可以参考Issues: FunnyDB#777
 * @author wulh
 */
@Data
@NoArgsConstructor
public class ActiveMark implements Serializable {
    /**
     * 当前值
     */
    private int current;
    /**
     * 当前值之前的bit标记(循环标记位)
     * bit容器(暂定short)可容纳16个标记位
     */
    private short beforeBitset;

    public ActiveMark(int current) {
        this(current, (short)0);
    }

    public ActiveMark(int current, short beforeBitset) {
        this.current = current;
        this.beforeBitset = beforeBitset;
    }

    /**
     * 设置新值
     *
     * @param value 值
     */
    public void set(int value) {
        int distinct = value - current;

        // 当距离为0时, 不做操作
        if (distinct == 0) {
            return;
        }

        // 当距离>0时,更新值,且左移bit位
        if (distinct > 0) {
            this.current = value;
            // 当超过16位时, 直接置为0
            if (distinct >= 16) {
                this.beforeBitset = 0;
            } else {
                // 移位
                this.beforeBitset =(short) (this.beforeBitset << distinct);
            }
        }
        // 更新bit位
        this.setBit(Math.abs(distinct) - 1);
    }

    /**
     * 给第N位设置值
     *
     * @param n bit位号
     */
    private void setBit(int n) {
        // 超出bit位则不做设置
        if (n >= 16) {
            return;
        }
        // 设置bit位值
        this.beforeBitset |= (1 << n);
    }

    /**
     * 检查值是否已存在
     *
     * @param value 检查值
     * @return 存在与否
     */
    public boolean exists(int value) {
        int beforeN = current - value;
        // 若检查的值相等时,直接返回,
        if (value == current) {
            return true;
        }

        // 当偏移超过bit范围时, 直接返回不存在.
        if (beforeN <= 0 || beforeN > 16) {
            return false;
        }

        return ((this.beforeBitset >>> (beforeN - 1)) & 1) == 1;
    }

    @Override
    public String toString() {
        return "ActiveMark{" +
                "current=" + current +
                ", beforeBitset=" + Integer.toBinaryString(0xFFFF & beforeBitset) +
                '}';
    }
}
