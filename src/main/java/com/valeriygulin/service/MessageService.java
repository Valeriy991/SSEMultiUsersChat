package com.valeriygulin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.valeriygulin.DAO.DAO;
import com.valeriygulin.model.Message;
import com.valeriygulin.model.User;
import com.valeriygulin.repository.SSEEmittersRepository;

import javax.servlet.AsyncContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.*;

public class MessageService {
    private SSEEmittersRepository repository;

    private BlockingQueue<Message> messageBlockingQueue = new LinkedBlockingQueue<>();

    private ExecutorService singleThreadExecutorTasker;

    private void sendMessage(PrintWriter writer, Message message) {
        try {
            writer.println("data: " + new ObjectMapper().writeValueAsString(message));
            writer.println();
            writer.flush();
        } catch (Exception ignored) {
        }
    }

    public MessageService(SSEEmittersRepository repository) {
        this.repository = repository;
        this.startMessageReceive();
    }

    private void startMessageReceive() {
        singleThreadExecutorTasker = Executors.newSingleThreadExecutor();
        singleThreadExecutorTasker.execute(() -> {
            try {
                while (true) {
                    Message message = messageBlockingQueue.take();
                    System.out.println("Start sending\n" + repository.getMap());
                    ConcurrentHashMap<Long, CopyOnWriteArrayList<AsyncContext>> map = repository.getMap();
                    for (Map.Entry<Long, CopyOnWriteArrayList<AsyncContext>> longCopyOnWriteArrayListEntry : map.entrySet()) {
                        for (AsyncContext asyncContext : longCopyOnWriteArrayListEntry.getValue()) {
                            try {
                                sendMessage(asyncContext.getResponse().getWriter(), message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Thread is interrupting");
            }
            System.out.println("Thread is interrupted");
        });
    }

    public Message add(User user, String message) {
        Message message1 = new Message(message);
        message1.setUser(user);
        DAO.addObject(message1);
        this.messageBlockingQueue.add(message1);
        return message1;
    }

    public void stop() {
        this.singleThreadExecutorTasker.shutdownNow();
        this.repository.clear();
        this.messageBlockingQueue.clear();
    }
}
