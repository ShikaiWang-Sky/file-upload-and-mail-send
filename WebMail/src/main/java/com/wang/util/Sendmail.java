package com.wang.util;


import com.sun.mail.util.MailSSLSocketFactory;
import com.wang.pojo.User;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

//网站3秒原则:用户体验
//多线程实现用户体验!    (异步处理)
public class Sendmail extends Thread {

    //用于给用户发送邮件的邮箱
    private String from = "715180879@qq.com";
    //邮箱的用户名
    private String username = "715180879@qq.com";
    //邮箱的密码
    private String password = "授权码";
    //发送邮件的服务器地址
    private String host = "smtp.qq.com";

    private User user;
    public Sendmail(User user) {
        this.user = user;
    }

    //丛谢run方法的实现,在run方法中发送邮件给指定的用户
    @Override
    public void run() {
        try {
            Properties prop = new Properties();

            prop.setProperty("mail.host", host);
            //邮件发送协议
            prop.setProperty("mail.transport.protocol","smtp");
            //需要验证用户名和密码
            prop.setProperty("mail.smtp.auth","true");

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
                    return new PasswordAuthentication(from, password);
                }
            });

            //开启Session的Debug模式,这样就可以查看到程序发送Email的运行状态
            session.setDebug(true);

            //2.通过session得到transport对象
            Transport ts = session.getTransport();

            //3.使用邮箱的用户名和授权码连上服务器
            ts.connect(host, username, password);

            //4.创建邮件:写邮件
            //注意需要传递session
            MimeMessage message = new MimeMessage(session);
            //邮件的发件人
            message.setFrom(new InternetAddress(from));

            //指明邮件的 收件人,现在收件人是从前端接收来的
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));

            //邮件的标题
            message.setSubject("用户注册邮件");

            String info = "恭喜注册成功,您的用户名: " + user.getName() + "您的密码: " + user.getPassword() + "请妥善保管,如有问题请联系网站客服!";

            message.setContent(info, "text/html;charset=UTF-8");
            message.saveChanges();

            //5.发送邮件
            ts.sendMessage(message, message.getAllRecipients());

            //6.关闭连接
            ts.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
