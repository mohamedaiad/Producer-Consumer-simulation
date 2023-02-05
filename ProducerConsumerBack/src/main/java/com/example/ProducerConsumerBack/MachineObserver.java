package com.example.ProducerConsumerBack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class MachineObserver implements ObserverI{

    @Autowired
    private WSService service;

    public MachineObserver (SimpMessagingTemplate messagingTemplate)
    {
        this.service= new WSService(messagingTemplate);
    }

    @Override
    public void update(String info) {
        service.notifyFrontend(info);
    }
}