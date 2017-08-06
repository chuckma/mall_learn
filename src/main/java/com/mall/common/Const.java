package com.mall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by Andy on 2017/4/23.
 */
public class Const {

    public static final String CURRENT_USER = "current_User";

    public static final String EMAIL = "email";

    public static final String USERNAME = "username";

    public interface ProductListOrderBy {
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }

    public interface Cart{
        int CHECKED = 1;   // 购物车选中
        int UNCHECKED = 0; // 购物车未选中

        String LIMTT_NUM_FAIL = "LIMTT_NUM_FAIL";
        String LIMTT_NUM_SUCCESS = "LIMTT_NUM_SUCCESS";
    }

    public interface Role{
        int ROLE_CUSTOMER = 0; // 普通用户
        int ROLE_ADMIN = 1; // 管理员
    }


    public enum ProductStatusEnum{
        ON_SALE(1,"在线");
        private String value;
        private int code;
        ProductStatusEnum(int code,String value) {
            this.value = value;
            this.code = code;
        }
        public String getValue() {
            return value;
        }
        public int getCode() {
            return code;
        }

    }

   /* public enum ProductStatusEnum{
        ON_SALE(1,"在线");
        private String value;
        private int code;
        ProductStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }*/


    public enum OrderStatusEnum{
        CANCELED(0, "已取消"),
        NO_PAY(10, "未支付"),
        PAID(20, "已支付"),
        SHIPPED(40, "已发货"),
        ORDER_SUCCESS(50, "订单完成"),
        ORDER_CLOSE(60, "订单关闭");

        OrderStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }

        private String value;
        private int code;

        public String getValue(){
            return value;
        }
        public int getCode(){
            return code;
        }


        public static OrderStatusEnum codeOf(int code) {
            for (OrderStatusEnum orderStatusEnum : values()) { //values 是枚举类的一个数组
                if (orderStatusEnum.getCode() == code) {
                    return orderStatusEnum;
                }

            }
            throw new RuntimeException("没找到对应的枚举");

        }
    }


    public interface AliPayCallBack{
        String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRAD_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }


    public enum PayPlatFormEnum{
        ALIPAY(1, "支付宝");
        PayPlatFormEnum(int code,String value){
            this.code = code;
            this.value = value;
        }

        private String value;
        private int code;

        public String getValue(){
            return value;
        }
        public int getCode(){
            return code;
        }
    }


    public enum PaymentTypeEnum {
        ONLINE_PAY(1, "在线支付");
        PaymentTypeEnum(int code,String value){
            this.code = code;
            this.value = value;
        }

        private String value;
        private int code;

        public String getValue(){
            return value;
        }
        public int getCode(){
            return code;
        }

        public static PaymentTypeEnum codeOf(int code) {
            for (PaymentTypeEnum paymentTypeEnum : values()) { //values 是枚举类的一个数组
                if (paymentTypeEnum.getCode() == code) {
                    return paymentTypeEnum;
                }

            }
            throw new RuntimeException("没找到对应的枚举");

        }
    }
}
