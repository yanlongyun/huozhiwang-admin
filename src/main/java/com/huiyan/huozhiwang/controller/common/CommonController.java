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
import com.huiyan.huozhiwang.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

/**
 * @author 慧燕
 * @link https://github.com/huozhiwang
 */
@Controller
public class CommonController {

    @Autowired
    private DefaultKaptcha captchaProducer;

    @GetMapping("/common/kaptcha")
    public void defaultKaptcha(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        byte[] captchaOutputStream = null;
        ByteArrayOutputStream imgOutputStream = new ByteArrayOutputStream();
        try {
            //生产验证码字符串并保存到session中
            String verifyCode = captchaProducer.createText();
            httpServletRequest.getSession().setAttribute("verifyCode", verifyCode);
            BufferedImage challenge = captchaProducer.createImage(verifyCode);
            ImageIO.write(challenge, "jpg", imgOutputStream);
        } catch (IllegalArgumentException e) {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        captchaOutputStream = imgOutputStream.toByteArray();
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        httpServletResponse.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = httpServletResponse.getOutputStream();
        responseOutputStream.write(captchaOutputStream);
        responseOutputStream.flush();
        responseOutputStream.close();
    }

    @GetMapping("/common/mall/kaptcha")
    public void mallKaptcha(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        /*首先创建默认的验证码*/
        DefaultKaptcha mallLoginKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        //验证码边框，无
        properties.put("kaptcha.border", "no");
        //验证码字体颜色，三个数字，RGB
        properties.put("kaptcha.textproducer.font.color", "27,174,171");
        //验证码干扰线颜色
        properties.put("kaptcha.noise.color", "20,33,42");
        //验证码字体大小
        properties.put("kaptcha.textproducer.font.size", "30");
        //验证码图片宽度
        properties.put("kaptcha.image.width", "110");
        //验证码图片高度
        properties.put("kaptcha.image.height", "40");
        //验证码sessionkey
        properties.put("kaptcha.session.key", Constants.MALL_VERIFY_CODE_KEY);
        //验证码字符间距
        properties.put("kaptcha.textproducer.char.space", "2");
        //验证码长度
        properties.put("kaptcha.textproducer.char.length", "6");
        Config config = new Config(properties);
        mallLoginKaptcha.setConfig(config);

        /*创建字节数组用于存储图片*/
        byte[] captchaOutputStream = null;
        /*创建二进制输出流*/
        ByteArrayOutputStream imgOutputStream = new ByteArrayOutputStream();
        try {
            //生产验证码字符串
            String verifyCode = mallLoginKaptcha.createText();
            // 将验证码字符串保存在session中。
            httpServletRequest.getSession().setAttribute(Constants.MALL_VERIFY_CODE_KEY, verifyCode);
            // 使用生成的验证码字符串，完成图片的生成
            BufferedImage challenge = mallLoginKaptcha.createImage(verifyCode);
            // 将图片写入到流中。
            ImageIO.write(challenge, "jpg", imgOutputStream);
        } catch (IllegalArgumentException e) {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        //使用HttpServletResponse将图片写入到浏览器中
        captchaOutputStream = imgOutputStream.toByteArray();
        // 通过response设定响应请求类型
        // no-store用于防止重要的信息被无意的发布。在请求消息中发送将使得请求和响应消息都不使用缓存。
        httpServletResponse.setHeader("Cache-Control", "no-store");
        // no-cache指示请求或响应消息不能缓存
        httpServletResponse.setHeader("Pragma", "no-cache");
        /* expires是response的一个属性,它可以设置页面在浏览器的缓存里保存的时间 ,超过设定的时间后就过期 。过期后再次
         * 浏览该页面就需要重新请求服务器发送页面数据，如果在规定的时间内再次访问次页面 就不需从服务器传送 直接从缓存中读取。
         * */
        httpServletResponse.setDateHeader("Expires", 0);
        // servlet接受request请求后接受图片形式的响应
        httpServletResponse.setContentType("image/jpeg");
        // 通过response获得输出流。
        ServletOutputStream responseOutputStream = httpServletResponse.getOutputStream();
        responseOutputStream.write(captchaOutputStream);
        // 刷新此输出流并强制将所有缓冲的输出字节被写出
        responseOutputStream.flush();
        //关闭输出流
        responseOutputStream.close();
    }
}
