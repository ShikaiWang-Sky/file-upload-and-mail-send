package com.wang;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

//发送一封简单的邮件
public class MailDemo01 {
    public static void main(String[] args) throws Exception {

        Properties prop = new Properties();
        //设置QQ邮件服务器
        prop.setProperty("mail.host", "smtp.qq.com");
        //邮件发送协议
        prop.setProperty("mail.transport.protocol", "smtp");
        //需要验证用户名和密码
        prop.setProperty("mail.smtp.auth", "true");

        //关于QQ邮箱,还要设置SSL加密,加上以下代码即可,其他邮箱不需要
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.socketFactory", sf);

        //使用JavaMail发送邮件的五个步骤

        //1.创建定义整个应用所需的环境信息的Session对象
        //QQ才有,其他邮箱不用!
        Session session = Session.getDefaultInstance(prop, new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                //发送人邮件的用户名,授权码
                return new PasswordAuthentication("715180879@qq.com", "授权码");
            }
        });

        //开启Session的Debug模式,这样就可以查看到程序发送Email的运行状态
        session.setDebug(true);

        //2.通过session得到transport对象
        Transport ts = session.getTransport();

        //3.使用邮箱的用户名和授权码连上服务器
        ts.connect("smtp.qq.com", "715180879@qq.com", "授权码");

        //4.创建邮件:写邮件
        //注意需要传递session
        MimeMessage message = new MimeMessage(session);
        //邮件的发件人
        message.setFrom(new InternetAddress("715180879@qq.com"));

        //指明邮件的 收件人,现在发件人和收件人是一样的,那就是自己给自己发
        message.setRecipient(Message.RecipientType.TO, new InternetAddress("715180879@qq.com"));

        //邮件的标题
        message.setSubject("简单邮件");

        //邮件的文本内容
        message.setContent("Hello!", "text/html;charset=UTF-8");

        //5.发送邮件
        ts.sendMessage(message, message.getAllRecipients());

        //6.关闭连接
        ts.close();

    }
}
