/**
 * BEYONDSOFT.COM INC
 */
package com.alipay.pay.alipay.biz;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.AlipayFundTransOrderQueryResponse;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.alipay.pay.alipay.enums.TradeStatysEnum;

/**
 * @Description 支付宝支付
 * @author yulijun
 * @version $Id: AlipayManager.java, v 0.1 2018/6/20 11:02 by Exp $$
 */
@Component
public class AliCatPayManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AliCatPayManager.class);

    @Autowired
    private AlipayClient        alipayClient;


    /**
     * 网页支付
     * @param tradeNo   交易编号
     * @param payAmount 实付金额
     * @param goodsName 订单信息 例：交易猫租号平台租号交易
     * @param goodsDesc 商品名称、套餐信息
     * @return 返回html，直接回显使用
     */
    public String pcPay(String tradeNo, String payAmount, String goodsName, String goodsDesc) {
        //设置请求参数
    	 AlipayConfig alipayConfig = AlipayConfig.getInstance();
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(alipayConfig.getReturn_url());
        alipayRequest.setNotifyUrl(alipayConfig.getNotify_url());
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(tradeNo);
        model.setTotalAmount(payAmount);
        model.setSubject(goodsName);
        model.setBody(goodsDesc);
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        model.setGoodsType("0");
        model.setTimeoutExpress("15m");
        alipayRequest.setBizModel(model);
        try {
            Date now = Calendar.getInstance().getTime();
            //请求
            AlipayTradePagePayResponse response = alipayClient.pageExecute(alipayRequest);
//            // 保存记录
//            PayRecordVO payRecordVO = new PayRecordVO(tradeNo, now, response.getTradeNo(),
//                payAmount, response.getSellerId(), response.getCode(), response.getMsg(),
//                JSON.toJSONString(response));
////            payRecordManager.save(payRecordVO);
            String result = response.getBody();
//            JSONObject object = JSONObject.parseObject(result);
//            String form = object.getString("data");
//            LOGGER.info("支付返回结果：{}", form);
            return result;
        } catch (AlipayApiException e) {
            LOGGER.error("网页预支付失败，tradeNo:{},payAmount:{},orderName:{},goodsName:{},msg:{}",
                tradeNo, payAmount, goodsName, goodsDesc, e.getErrMsg(), e);
        }
        return "支付失败";
    }

    /**
     * app支付
     * @param tradeNo   交易编号
     * @param payAmount 实付金额
     * @param goodsName 订单信息 例：交易猫租号平台租号交易
     * @param goodsDesc 商品名称、套餐信息
     * @return 返回唤起参数，不需要二次处理，直接使用
     */
    public String appPay(String tradeNo, String payAmount, String goodsName, String goodsDesc) {
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody(goodsDesc);
        model.setSubject(goodsName);
        model.setOutTradeNo(tradeNo);
        model.setTimeoutExpress("15m");
        model.setTotalAmount(payAmount);
        model.setProductCode("QUICK_MSECURITY_PAY");
        model.setGoodsType("0");
        request.setBizModel(model);
        request.setNotifyUrl(AlipayConfig.getInstance().getNotify_url());
        try {
            Date now = Calendar.getInstance().getTime();
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
//            // 保存记录
//            PayRecordVO payRecordVO = new PayRecordVO(tradeNo, now, response.getTradeNo(),
//                payAmount, response.getSellerId(), response.getCode(), response.getMsg(),
//                JSON.toJSONString(response));
//            payRecordManager.save(payRecordVO);
            //就是orderString 可以直接给客户端请求，无需再做处理。
            LOGGER.info("支付返回结果：{}", response.getBody());
            return response.getBody();
        } catch (AlipayApiException e) {
            LOGGER.error("app预支付失败，tradeNo:{},payAmount:{},orderName:{},goodsName:{},msg:{}",
                tradeNo, payAmount, goodsName, goodsDesc, e.getErrMsg(), e);
        }
        return "支付失败";
    }

    /**
     * wap支付（手机网页支付）
     * @param tradeNo   交易编号
     * @param payAmount 实付金额
     * @param goodsName 订单信息 例：交易猫租号平台租号交易
     * @param goodsDesc 商品名称、套餐信息
     * @return 返回html，直接回显使用
     */
    public String wapPay(String tradeNo, String payAmount, String goodsName, String goodsDesc) {
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
        alipayRequest.setReturnUrl(AlipayConfig.getInstance().getReturn_url());
        alipayRequest.setNotifyUrl(AlipayConfig.getInstance().getNotify_url());//在公共参数中设置回跳和通知地址
        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        model.setBody(goodsDesc);
        model.setSubject(goodsName);
        model.setOutTradeNo(tradeNo);
        model.setTimeoutExpress("15m");
        model.setTotalAmount(payAmount);
        model.setProductCode("QUICK_WAP_WAY");
        model.setGoodsType("0");
        alipayRequest.setBizModel(model);//填充业务参数
        String form = "";
        try {
            Date now = Calendar.getInstance().getTime();
            AlipayTradeWapPayResponse response = alipayClient.pageExecute(alipayRequest);
//            // 保存记录
//            PayRecordVO payRecordVO = new PayRecordVO(tradeNo, now, response.getTradeNo(),
//                payAmount, response.getSellerId(), response.getCode(), response.getMsg(),
//                JSON.toJSONString(response));
//            payRecordManager.save(payRecordVO);
            form = response.getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            LOGGER.error("wap预支付失败，tradeNo:{},payAmount:{},orderName:{},goodsName:{},msg:{}",
                tradeNo, payAmount, goodsName, goodsDesc, e.getErrMsg(), e);
        }
        return form;

    }

    /**
     * 支付结果回调
     * @param params
     * @return
     */
    public AlipayTradePayResponse payNotify(Map<String, String> params) {
        LOGGER.info("支付宝回调参数,params:{}", params);
        AlipayConfig alipayConfig = AlipayConfig.getInstance();
        AlipayTradePayResponse response = new AlipayTradePayResponse();
        Map<String, String> paramsMap = params; //将异步通知中收到的所有参数都存放到map中
        try {
            // 更新记录回调信息
//            PayRecordVO payRecordVO = payRecordManager.getByTradeNo(paramsMap.get("out_trade_no"))
//                .getData();
//            payRecordVO.setPrNotifyCode(response.getCode());
//            payRecordVO.setPrNotifyMsg(response.getMsg());
//            payRecordVO.setPrNotifyTime(Calendar.getInstance().getTime());
//            payRecordVO.setPrNotifyContent(JSON.toJSONString(response));
//            payRecordManager.update(payRecordVO);

            response.setCode("success");
            response.setMsg("success");

            boolean signVerified = AlipaySignature.rsaCheckV1(paramsMap,
                alipayConfig.getAlipay_public_key(), alipayConfig.getCharset(),
                alipayConfig.getSign_type()); //调用SDK验证签名
            if (signVerified) {
                // 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验
                if (TradeStatysEnum.TRADE_SUCCESS.getCode().equals(paramsMap.get("trade_status"))) {
                    // 校验成功后在response中返回success并继续商户自身业务处理
                    //orderManager.payOperation(paramsMap.get("out_trade_no"), "SUCCESS");
                } else {
                    // 校验失败返回failure

                }

            } else {
                // 验签失败则记录异常日志，并在response中返回failure.
                response.setCode("failure");
                response.setMsg("failure");
                return response;
            }
            return response;
        } catch (AlipayApiException e) {
            LOGGER.error("异步通知失败，msg:{}", e.getErrMsg(), e);
            response.setCode("failure");
            response.setMsg("failure");
            return response;
        }
    }

    /**
     * 退款
     * @param tradeNo      交易号
     * @param refundAmount 退款金额
     * @param refundReason 退款原因
     * @param operator     操作人
     * @return 返回成功或失败
     */
    public Boolean refund(String tradeNo, String refundAmount, String refundReason,
                          String operator) {
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        model.setOutTradeNo(tradeNo);
        model.setRefundAmount(refundAmount);
        model.setRefundReason(refundReason);
        model.setOperatorId(operator);
        request.setBizModel(model);
        try {
            Date now = Calendar.getInstance().getTime();
            AlipayTradeRefundResponse response = alipayClient.execute(request);
//            // 保存记录
//            RefundRecordVO refundRecordVO = new RefundRecordVO(tradeNo, response.getTradeNo(), now,
//                response.getBuyerLogonId(), response.getFundChange(), response.getRefundFee(),
//                response.getGmtRefundPay(), response.getBuyerUserId(), response.getCode(),
//                response.getMsg(), JSON.toJSONString(response));
//            refundRecordManager.save(refundRecordVO);
            if (response.isSuccess()) {
                return true;
            } else {
                LOGGER.error("退款接口失败，tradeNo:{},refundAmount:{},refundReason:{},operator:{},msg:{}",
                    tradeNo, refundAmount, refundReason, operator, response.getMsg());
                return false;
            }
        } catch (AlipayApiException e) {
            LOGGER.error("退款接口失败，tradeNo:{},refundAmount:{},refundReason:{},operator:{},msg:{}",
                tradeNo, refundAmount, refundReason, operator, e.getErrMsg(), e);
            return false;
        }
    }

    /**
     * 交易退款状态查询
     * @param tradeNo  交易号
     * @return 返回成功或失败
     */
    public Boolean refundQuery(String tradeNo) {
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();
        model.setOutTradeNo(tradeNo);
        model.setOutRequestNo(tradeNo);
        request.setBizModel(model);
        try {
            AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
            // 更新记录查询信息
//            RefundRecordVO refundRecordVO = refundRecordManager.getByTradeNo(tradeNo).getData();
//            refundRecordVO.setrResultCode(response.getCode());
//            refundRecordVO.setrResultMsg(response.getMsg());
//            refundRecordVO.setrResultTtime(Calendar.getInstance().getTime());
//            refundRecordVO.setrResultContent(JSON.toJSONString(response));
//            refundRecordManager.update(refundRecordVO);
            if (response.isSuccess()) {
                if ("SUCCESS".equals(response.getRefundStatus())) {
                    return true;
                }
                return false;
            } else {
                LOGGER.error("交易退款状态查询失败，tradeNo:{},msg:{}", tradeNo, response.getMsg());
                return false;
            }
        } catch (AlipayApiException e) {
            LOGGER.error("交易退款状态查询失败，tradeNo:{},msg:{}", tradeNo, e.getErrMsg(), e);
            return false;
        }
    }

    /**
     * 交易转账
     * @param tradeNo      交易号  保证每次转账的唯一性
     * @param payeeAccount 收款账号
     * @param amount       金额
     * @param remark       备注
     * @return 返回成功或失败
     */
    public boolean transfer(String tradeNo, String payeeAccount, String amount, String remark) {
        AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
        AlipayFundTransToaccountTransferModel model = new AlipayFundTransToaccountTransferModel();
        model.setOutBizNo(tradeNo);
        model.setPayeeType("ALIPAY_LOGONID");
        model.setPayeeAccount(payeeAccount);
        model.setAmount(amount);
        model.setPayerShowName(AlipayConfig.getInstance().getPayer_show_name());
        model.setRemark(remark);
        request.setBizModel(model);
        try {
            Date now = Calendar.getInstance().getTime();
            AlipayFundTransToaccountTransferResponse response = alipayClient.execute(request);
            // 保存记录
//            TransRecordVO transRecordVO = new TransRecordVO(tradeNo, response.getOrderId(), now,
//                response.getPayDate(), response.getCode(), response.getMsg(),
//                JSON.toJSONString(response));
//            transRecordManager.save(transRecordVO);
            if (response.isSuccess()) {
                return true;
            } else {
                LOGGER.error("交易成功转账失败，tradeNo:{},msg:{}", tradeNo, response.getMsg());
                return false;
            }
        } catch (AlipayApiException e) {
            LOGGER.error("交易成功转账失败，tradeNo:{},msg:{}", tradeNo, e.getErrMsg(), e);
            return false;
        }
    }

    /**
     * 查询转账状态
     * @param tradeNo  交易号
     * @return
     */
    public boolean transQuery(String tradeNo) {
        AlipayFundTransOrderQueryRequest request = new AlipayFundTransOrderQueryRequest();
        AlipayFundTransOrderQueryModel model = new AlipayFundTransOrderQueryModel();
        model.setOutBizNo(tradeNo);
        request.setBizModel(model);
        try {
            AlipayFundTransOrderQueryResponse response = alipayClient.execute(request);
            // 更新记录查询信息
//            TransRecordVO transRecordVO = transRecordManager.getByTradeNo(tradeNo).getData();
//            transRecordVO.setTrResultCode(response.getCode());
//            transRecordVO.setTrResultMsg(response.getMsg());
//            transRecordVO.setTrResultTime(Calendar.getInstance().getTime());
//            transRecordVO.setTrResultContent(JSON.toJSONString(response));
//            transRecordManager.update(transRecordVO);
            if (response.isSuccess()) {
                if ("SUCCESS".equals(response.getStatus())) {
                    return true;
                }
                return false;
            } else {
                LOGGER.error("查询转账状态失败，tradeNo:{},msg:{}", tradeNo, response.getMsg());
                return false;
            }
        } catch (AlipayApiException e) {
            LOGGER.error("查询转账状态失败，tradeNo:{},msg:{}", tradeNo, e.getErrMsg(), e);
            return false;
        }
    }

//    /**
//     * 获得初始化的AlipayClient
//     * @return AlipayClient
//     */
//    private AlipayClient getClient() {
//        AlipayConfig alipayConfig = AlipayConfig.getInstance();
//        // 获得初始化的AlipayClient
//        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig.getGatewayUrl(),
//            alipayConfig.getApp_id(), alipayConfig.getMerchant_private_key(), "json",
//            alipayConfig.getCharset(), alipayConfig.getAlipay_public_key(),
//            alipayConfig.getSign_type());
//        return alipayClient;
//    }
}
