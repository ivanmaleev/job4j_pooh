package ru.job4j.pooh;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

public class QueueService implements Service {

    Map<String, Queue<String>> queues = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        String text = "";
        String status = "204";
        if ("POST".equalsIgnoreCase(req.httpRequestType())) {
            queues.putIfAbsent(req.getSourceName(), new ConcurrentLinkedQueue<>());
            queues.get(req.getSourceName()).offer(req.getParam());
        } else if ("GET".equalsIgnoreCase(req.httpRequestType())) {
            Queue<String> queue = queues.get(req.getSourceName());
            if (queue != null) {
                status = "200";
                text = queue.poll();
                if (text != null) {
                    status = "200";
                } else {
                    text = "";
                }
            }
        }
        return new Resp(text, status);
    }
}