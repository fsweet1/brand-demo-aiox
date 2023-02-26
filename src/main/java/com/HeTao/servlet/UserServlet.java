package com.HeTao.servlet;

import com.HeTao.pojo.User;
import com.HeTao.service.impl.UserServiceImpl;
import com.HeTao.util.CheckCodeUtil;
import com.alibaba.fastjson.JSON;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/User/*")
public class UserServlet extends BaseServlet{
    UserServiceImpl userService = new UserServiceImpl();

    /**
     * 根据用户名和密码登录得到前端的用户名和密码进行封装
     */
    public void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        BufferedReader reader = req.getReader();
        String readLine = reader.readLine();
        User user = JSON.parseObject(readLine, User.class);

        //查询数据
        User select = userService.select(user);
        if (select != null) resp.getWriter().write("success");
    }

    /**
     * 添加用户
     */
    public void register(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        String readLine = reader.readLine();
        User user = JSON.parseObject(readLine, User.class);

        HttpSession session = req.getSession();
        String checkCode = (String) session.getAttribute("checkCode");

        System.out.println(checkCode);
        System.out.println(user.getCheckCode());
        if (!checkCode.equalsIgnoreCase(user.getCheckCode()))
        {
            resp.getWriter().write("checkCode Fail");
            return;
        }

        userService.add(user);
        resp.getWriter().write("success");
    }

    public void checkCode(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(); //需要提前

        //生成验证码
        ServletOutputStream outputStream = resp.getOutputStream();
        String s = CheckCodeUtil.outputVerifyImage(100, 60, outputStream, 4);

        //存入session中
        session.setAttribute("checkCode", s);

    }
}
