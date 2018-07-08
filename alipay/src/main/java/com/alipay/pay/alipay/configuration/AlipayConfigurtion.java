/**
 * BEYONDSOFT.COM INC
 */
package com.alipay.pay.alipay.configuration;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.pay.alipay.biz.AlipayConfig;


/**
 * @Description 初始化支付宝接入参数
 * @author shenqifeng
 * @version $Id: AlipayConfiguration.java, v 0.1 2018/6/20 14:39 by Exp $$
 */
@Configuration
public class AlipayConfigurtion {
    @Autowired
    private Environment environment;

    @Bean
    public AlipayClient alipayClient() {
        AlipayConfig alipayConfig = AlipayConfig.getInstance();
        alipayConfig.setAlipay_public_key(environment.getProperty("alipay.alipay_public_key"));
        alipayConfig.setApp_id(environment.getProperty("alipay.app_id"));
        alipayConfig.setCharset(environment.getProperty("alipay.charset"));
        alipayConfig.setGatewayUrl(environment.getProperty("alipay.gatewayUrl"));
        alipayConfig
            .setMerchant_private_key(environment.getProperty("alipay.merchant_private_key"));
        alipayConfig.setNotify_url(environment.getProperty("alipay.notify_url"));
        alipayConfig.setReturn_url(environment.getProperty("alipay.return_url"));
        alipayConfig.setSign_type(environment.getProperty("alipay.sign_type"));
        alipayConfig.setPayer_show_name(environment.getProperty("alipay.payer_show_name"));
        
        // 获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig.getGatewayUrl(),
            alipayConfig.getApp_id(), alipayConfig.getMerchant_private_key(), "json",
            alipayConfig.getCharset(), alipayConfig.getAlipay_public_key(),
            alipayConfig.getSign_type());
        return alipayClient;
    }
}

