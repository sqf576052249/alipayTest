/**
 * BEYONDSOFT.COM INC
 */
package com.alipay.pay.alipay.web;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.assertj.core.util.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.pay.alipay.biz.AliCatPayManager;
import com.alipay.pay.alipay.result.RestObjectResult;



/**
 * @Description 支付宝接口实现类
 * @author shenqifeng
 * @version $Id: AlipayServiceImpl.java, v 0.1 2018/6/20 18:01 by Exp $$
 */
@RestController
public class AliCatPayRestController  {

    @Autowired
    private AliCatPayManager aliCatPayManager;

    @RequestMapping("/alipay/pcPrePay")
    public ModelAndView pcPrePay(@RequestParam("tradeNo") String tradeNo,
                                             @RequestParam("payAmount") String payAmount,
                                             @RequestParam("goodsName") String goodsName,
                                             @RequestParam("goodsDesc") String goodsDesc) {
        ModelAndView view = new ModelAndView("pagePay");
        view.addObject("data", aliCatPayManager.pcPay(tradeNo, payAmount, goodsName, goodsDesc));
    	return view;
    }

    @RequestMapping("/alipay/appPrePay")
    public RestObjectResult<String> appPrePay(@RequestParam("tradeNo") String tradeNo,
                                              @RequestParam("payAmount") String payAmount,
                                              @RequestParam("goodsName") String goodsName,
                                              @RequestParam("goodsDesc") String goodsDesc) {
        return new RestObjectResult<>(
            aliCatPayManager.appPay(tradeNo, payAmount, goodsName, goodsDesc));
    }

    @RequestMapping("/alipay/refund")
    public RestObjectResult<Boolean> refund(@RequestParam("tradeNo") String tradeNo,
                                            @RequestParam("refundAmount") String refundAmount,
                                            @RequestParam("refundReason") String refundReason,
                                            @RequestParam("operator") String operator) {
        return new RestObjectResult<>(
            aliCatPayManager.refund(tradeNo, refundAmount, refundReason, operator));
    }

    @RequestMapping("/alipay/refundQuery")
    public RestObjectResult<Boolean> refundQuery(@RequestParam("tradeNo") String tradeNo) {
        return new RestObjectResult<>(aliCatPayManager.refundQuery(tradeNo));
    }

    @RequestMapping("/alipay/transfer")
    public RestObjectResult<Boolean> transfer(@RequestParam("tradeNo") String tradeNo,
                                              @RequestParam("payeeAccount") String payeeAccount,
                                              @RequestParam("amount") String amount,
                                              @RequestParam("remark") String remark) {
        return new RestObjectResult<>(
            aliCatPayManager.transfer(tradeNo, payeeAccount, amount, remark));
    }

    @RequestMapping("/alipay/transQuery")
    public RestObjectResult<Boolean> transQuery(@RequestParam("tradeNo") String tradeNo) {
        return new RestObjectResult<>(aliCatPayManager.transQuery(tradeNo));
    }
    
    /**
     * app支付异步回调接口
     * @param request
     * @return
     */
    @RequestMapping(value = "/rest/alipay/pay")
    public AlipayTradePayResponse appPayNotify(HttpServletRequest request) {
    	AlipayTradePayResponse response = new AlipayTradePayResponse();
    	 response.setCode("success");
         response.setMsg("success");
    	return response;
    }

    /**
     * 跳转回商户页面回调
     * @param request
     * @return
     */
    @RequestMapping(value = "/rest/alipay/payNotify")
    public AlipayTradePayResponse pcPayNotify(HttpServletRequest request) {
    	AlipayTradePayResponse response = new AlipayTradePayResponse();
   	 response.setCode("success");
        response.setMsg("success");
   	return response;
    }
    
    
    /**
     * 获取回调参数
     * @param requestParams
     */
    private Map<String, String> getParamMap(Map<String, String[]> requestParams) {
    	Map<String,String> params = new HashMap<String,String>();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                    : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        return params;
    }

}
