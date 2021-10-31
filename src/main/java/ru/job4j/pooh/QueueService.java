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
        String status = "";
        if ("POST".equalsIgnoreCase(req.httpRequestType())) {
            Queue<String> queue = queues.putIfAbsent(req.getSourceName(), new ConcurrentLinkedQueue<>());
            if (queue == null) {
                queue = queues.get(req.getSourceName());
            }
            queue.offer(req.getParam());
            status = "200";
        } else if ("GET".equalsIgnoreCase(req.httpRequestType())) {
            Queue<String> queue = queues.get(req.getSourceName());
            if (queue == null) {
                status = "204";
            }
            text = queue.poll();
            if (text == null) {
                text = "";
            }
            status = "200";
        }
        return new Resp(text, status);
    }
}