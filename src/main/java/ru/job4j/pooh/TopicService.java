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
        String status = "";
        if ("POST".equalsIgnoreCase(req.httpRequestType())) {
            Map<String, Queue<String>> userQueues = queues.get(req.getSourceName());
            if (userQueues == null) {
                status = "204";
            } else {
                userQueues.forEach((key, value) -> value.offer(req.getParam()));
                status = "200";
            }
        } else if ("GET".equalsIgnoreCase(req.httpRequestType())) {
            Map<String, Queue<String>> userQueues = queues.putIfAbsent(req.getSourceName(), new ConcurrentHashMap<>());
            if (userQueues == null) {
                userQueues = queues.get(req.getSourceName());
            }
            Queue<String> userQueue = userQueues.putIfAbsent(req.getParam(), new ConcurrentLinkedQueue<>());
            if (userQueue == null) {
                userQueue = userQueues.get(req.getParam());
            }
            text = userQueue.poll();
            if (text == null) {
                text = "";
                status = "204";
            } else {
                status = "200";
            }
        }
        return new Resp(text, status);
    }
}