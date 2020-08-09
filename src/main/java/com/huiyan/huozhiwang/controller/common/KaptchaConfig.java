/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package com.huiyan.huozhiwang.controller.common;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class KaptchaConfig {
    /*验证码配置*/
    @Bean
    public DefaultKaptcha getDefaultKaptcha(){
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
//        配置验证码的边界，默认yes
        properties.put("kaptcha.border", "no");
//        验证码文字颜色，默认black
        properties.put("kaptcha.textproducer.font.color", "black");
//        图片宽度，默认200
        properties.put("kaptcha.image.width", "150");
//        图片高度，默认50
        properties.put("kaptcha.image.height", "40");
//        字体大小，默认40
        properties.put("kaptcha.textproducer.font.size", "30");
//        默认其他
        properties.put("kaptcha.session.key", "verifyCode");
//        文字间隔
        properties.put("kaptcha.textproducer.char.space", "5");

        Config config = new Config(properties);

        defaultKaptcha.setConfig(config);

        return defaultKaptcha;
    }
}