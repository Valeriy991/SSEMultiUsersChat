package com.valeriygulin.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.valeriygulin.DAO.DAO;
import com.valeriygulin.dto.ResponseResult;
import com.valeriygulin.model.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet(value = "/sse/User", asyncSupported = true)

public class UserServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        ObjectMapper mapper = new ObjectMapper();
        ResponseResult<User> result;
        try (BufferedReader reader = req.getReader()) {
            User user = mapper.readValue(reader, User.class);
            DAO.addObject(user);
            result = new ResponseResult<>(null, user);
        } catch (Exception e) {
            resp.setStatus(400);
            result = new ResponseResult<>("Error " + e.getMessage(), null);
        }
        this.objectMapper.writeValue(resp.getWriter(), result);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        ResponseResult<User> result;
        if (login != null && password != null) {
            try {
                User user = (User) DAO.getObjectByParams(new String[]{"login", "password"},
                        new Object[]{login, password}, User.class);
                if (user == null) {
                    resp.setStatus(400);
                    result = new ResponseResult<>("There is not such User", null);
                } else {
                    result = new ResponseResult<>(null, user);
                }
            } catch (NumberFormatException e) {
                resp.setStatus(400);
                result = new ResponseResult<>("Invalid number format", null);
            }
            this.objectMapper.writeValue(resp.getWriter(), result);
        } else {
            resp.setStatus(400);
            result = new ResponseResult<>("Invalid number format", null);
        }
        this.objectMapper.writeValue(resp.getWriter(), result);
        DAO.closeOpenedSession();

    }

}
