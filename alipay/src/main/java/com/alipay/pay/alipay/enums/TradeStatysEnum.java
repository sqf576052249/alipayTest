/**
 * BEYONDSOFT.COM INC
 */
package com.alipay.pay.alipay.enums;

/**
 * 
 * @author yulijun
 * @version $Id: TradeStatysEnum.java, v 0.1 2018年6月16日 上午10:02:26 xuwentao Exp $
 */
public enum TradeStatysEnum {

     WAIT_BUYER_PAY("WAIT_BUYER_PAY", "交易创建，等待买家付款"),

     TRADE_CLOSED("TRADE_CLOSED", "未付款交易超时关闭，或支付完成后全额退款"),

     TRADE_SUCCESS("TRADE_SUCCESS", "交易支付成功"),

     TRADE_FINISHED("TRADE_FINISHED", "交易结束，不可退款"),;

    private String code;

    private String message;

    /**
     * @param code
     * @param message
     */
    TradeStatysEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
