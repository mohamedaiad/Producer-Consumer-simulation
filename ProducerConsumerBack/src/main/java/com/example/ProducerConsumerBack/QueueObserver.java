package com.example.ProducerConsumerBack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class QueueObserver implements ObserverI{

    @Autowired
    private WSService service;

    public QueueObserver(SimpMessagingTemplate messagingTemplate) {
        this.service= new WSService(messagingTemplate);
    }

    @Override
    public void update(String info) {
        service.notifyFrontend(info);
    }
}
