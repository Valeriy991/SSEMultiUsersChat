package com.valeriygulin.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.valeriygulin.DAO.DAO;
import com.valeriygulin.dto.ResponseResult;
import com.valeriygulin.model.Message;
import com.valeriygulin.model.User;
import com.valeriygulin.repository.SSEEmittersRepository;
import com.valeriygulin.service.MessageService;

import javax.servlet.AsyncContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@WebServlet(value = "/sse/getChat", asyncSupported = true)
public class MessageServlet extends HttpServlet {
    private SSEEmittersRepository emitters = new SSEEmittersRepository();
    private MessageService service;

    private ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void init() {
        this.service = new MessageService(this.emitters);
    }

    @Override
    public void destroy() {
        this.service.stop();
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/event-stream");
        String id = req.getParameter("id");
        String message = req.getParameter("message");
        ResponseResult<Message> result;
        User user = (User) DAO.getObjectById(Long.parseLong(id), User.class);
        if (user != null) {
            if (message != null) {
                Message addedMessage = this.service.add(user, message);
                result = new ResponseResult<>(null, addedMessage);
            } else {
                resp.setStatus(400);
                result = new ResponseResult<>("Message is null!", null);
            }
        } else {
            resp.setStatus(400);
            result = new ResponseResult<>("User is null!", null);
        }
        this.objectMapper.writeValue(resp.getWriter(), result);
        DAO.closeOpenedSession();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getHeader("Accept") != null && req.getHeader("Accept").equals("text/event-stream")) {
            long id = Long.parseLong(req.getParameter("id"));
            resp.setContentType("text/event-stream");
            resp.setHeader("Connection", "keep-alive");
            resp.setCharacterEncoding("UTF-8");
            AsyncContext asyncContext = req.startAsync();
            asyncContext.setTimeout(60000L);
            this.emitters.add(id, asyncContext);
        } else {
            resp.setCharacterEncoding("utf-8");
            resp.setContentType("application/json;charset=utf-8");
            ResponseResult<List<Long>> result;
            List<Long> res = new ArrayList<>();
            for (Map.Entry<Long, CopyOnWriteArrayList<AsyncContext>> entry : this.emitters.getMap().entrySet()) {
                CopyOnWriteArrayList<AsyncContext> value = entry.getValue();
                if (!value.isEmpty()) {
                    res.add(entry.getKey());
                }
            }
            ObjectMapper objectMapper = new ObjectMapper();
            result = new ResponseResult<>(null, res);
            objectMapper.writeValue(resp.getWriter(), result);
        }
    }
}
