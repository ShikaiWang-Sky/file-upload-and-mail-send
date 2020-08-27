package com.wang.servlet;

import com.wang.pojo.User;
import com.wang.util.Sendmail;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RegisterServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //接受用户请求,封装成对象
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String email = req.getParameter("email");

//        System.out.println(username);
//        System.out.println(password);
//        System.out.println(email);

        User user = new User(username, password, email);

        //用户注册成功之后,给用户发送一封邮件
        //我们使用线程来专门发送邮件,防止出现耗时,和网站注册人数过多的情况
        Sendmail sendmail = new Sendmail(user);
        //启动线程:线程启动之后就会执行run方法来发送邮件
        sendmail.start();

        //注册用户
        req.setAttribute("message", "注册成功,我们已经发送了一封带了注册信息的电子邮件,请查收!如果网络不稳定,可能会过会儿才能收到!!");
        req.getRequestDispatcher("info.jsp").forward(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
