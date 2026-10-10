package com.atguigu.springbootdemo.userinfo.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.*;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author weiyunhui
 * @since 2024-08-27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("order_info")
public class OrderInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 收货人
     */
    private String consignee;

    /**
     * 收件人电话
     */
    private String consigneeTel;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 订单状态
     */
    private String orderStatus;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 付款方式
     */
    private String paymentWay;

    /**
     * 送货地址
     */
    private String deliveryAddress;

    /**
     * 订单备注
     */
    private String orderComment;

    /**
     * 订单交易编号（第三方支付用)
     */
    private String outTradeNo;

    /**
     * 订单描述(第三方支付用)
     */
    private String tradeBody;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 失效时间
     */
    private Date expireTime;

    /**
     * 进度状态
     */
    private String processStatus;

    /**
     * 物流单编号
     */
    private String trackingNo;

    /**
     * 父订单编号
     */
    private Long parentOrderId;

    /**
     * 图片链接
     */
    private String imgUrl;

    /**
     * 省份id
     */
    private Integer provinceId;

    /**
     * 活动减免金额
     */
    private BigDecimal activityReduceAmount;

    /**
     * 优惠券减免金额
     */
    private BigDecimal couponReduceAmount;

    /**
     * 原始总金额
     */
    private BigDecimal originalTotalAmount;

    /**
     * 运费金额
     */
    private BigDecimal feightFee;

    /**
     * 运费减免金额
     */
    private BigDecimal feightFeeReduce;

    /**
     * 可退款时间（签收后30天）
     */
    private Date refundableTime;
}
