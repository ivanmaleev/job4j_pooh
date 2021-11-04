package ru.job4j.pooh;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TopicService implements Service {

    Map<String, Map<String, Queue<String>>> queues = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        String text = "";
        String status = "204";
        if ("POST".equalsIgnoreCase(req.httpRequestType())) {
            Map<String, Queue<String>> userQueues = queues.get(req.getSourceName());
            if (userQueues != null) {
                userQueues.forEach((key, value) -> value.offer(req.getParam()));
                status = "200";
            }
        } else if ("GET".equalsIgnoreCase(req.httpRequestType())) {
            queues.putIfAbsent(req.getSourceName(), new ConcurrentHashMap<>());
            queues.get(req.getSourceName()).putIfAbsent(req.getParam(), new ConcurrentLinkedQueue<>());
            text = queues.get(req.getSourceName()).get(req.getParam()).poll();
            if (text != null) {
                status = "200";
            } else {
                text = "";
            }
        }
        return new Resp(text, status);
    }
}