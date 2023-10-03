package com.valeriygulin.repository;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SSEEmittersRepository {
    private ConcurrentHashMap<Long, CopyOnWriteArrayList<AsyncContext>> map = new ConcurrentHashMap<>();

    public void add(long id, AsyncContext asyncContext) {
        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent asyncEvent) {
                CopyOnWriteArrayList<AsyncContext> asyncContexts =
                        map.getOrDefault(id, new CopyOnWriteArrayList<>());
                asyncContexts.remove(asyncContext);
                map.put(id, asyncContexts);
                System.out.println("Finish");
            }

            @Override
            public void onTimeout(AsyncEvent asyncEvent) {
                CopyOnWriteArrayList<AsyncContext> asyncContexts =
                        map.getOrDefault(id, new CopyOnWriteArrayList<>());
                asyncContexts.remove(asyncContext);
                map.put(id, asyncContexts);
                System.out.println("Timeout");
            }

            @Override
            public void onError(AsyncEvent asyncEvent) {
                //TODO тут по id получить список и удалить из него asyncEvent
                CopyOnWriteArrayList<AsyncContext> asyncContexts =
                        map.getOrDefault(id, new CopyOnWriteArrayList<>());
                asyncContexts.remove(asyncContext);
                map.put(id, asyncContexts);
                System.out.println("Error");
            }

            @Override
            public void onStartAsync(AsyncEvent asyncEvent) {
                System.out.println("Start async");
            }
        });

        CopyOnWriteArrayList<AsyncContext> asyncContexts = map.getOrDefault(id, new CopyOnWriteArrayList<>());
        asyncContexts.add(asyncContext);
        map.put(id, asyncContexts);


        System.out.println("After adding emitter " + map);
    }

    public ConcurrentHashMap<Long, CopyOnWriteArrayList<AsyncContext>> getMap() {
        return map;
    }

    public void clear() {
        this.map.clear();
    }
}
