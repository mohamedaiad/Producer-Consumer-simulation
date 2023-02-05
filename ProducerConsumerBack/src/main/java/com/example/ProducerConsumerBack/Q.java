package com.example.ProducerConsumerBack;

import com.google.gson.Gson;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.awt.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Q implements Runnable{
    Gson gson = new Gson();
    private String name;
    Queue<Product> products;
//    Map<String, String> message;
    LinkedList<Machine> machines;
    ObserverI qObserver;

    public Q(SimpMessagingTemplate messagingTemplate) {
        this.qObserver = new QueueObserver(messagingTemplate);
        products = new LinkedBlockingQueue<>();
        machines = new LinkedList<>();
//        message = new HashMap<>();
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void clear(){
        this.products.clear();
    }

    public synchronized void addProduct(Product product){
        products.add(product);
//        System.out.println(product.getName()+ " " +product.getColor());
//        message.put(product.getName(),product.getColor());
        this.notify();
    }

    public void addMachine(Machine machine){
        machines.add(machine);
    }

    public String getProducts(){
        String json = gson.toJson(products);
        return json;
    }

    public void run(){
        while(true){
            synchronized (this){
                while (products.isEmpty() || machines.isEmpty()){
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                int randMachine = new Random().nextInt(machines.size());
                Machine machine = machines.get(randMachine);
                while(machine.isWorking()){
                    randMachine = new Random().nextInt(machines.size());
                    machine = machines.get(randMachine);
                }
                Product product = products.poll();
                machine.setProduct(product);
                String info = "number," + this.name + "," + products.size();
                qObserver.update(info);
                info = "input," + this.name + "," + product.getName() + "," + product.getColor();
                qObserver.update(info);
                //qObserver.update(Integer.toString(products.size()));
            }

        }
    }
}