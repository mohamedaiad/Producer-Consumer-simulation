package com.example.ProducerConsumerBack;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Random;

public class Machine implements Runnable {
    private Product currentProduct;
    private Q nextq;
    private int workingTime;
    private String name;
    ObserverI mObserver;


    public Machine (SimpMessagingTemplate messagingTemplate) {
        this.mObserver = new MachineObserver(messagingTemplate);
        workingTime = new Random().nextInt(6) + 1;
        currentProduct = null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(int workingTime) {
        this.workingTime = workingTime;
    }


    public boolean isWorking() {
        return this.currentProduct != null;
    }

    public synchronized void setProduct(Product product) {
        this.currentProduct = product;
        this.notify();
    }

    public void setNextQ(Q queue){
        this.nextq = queue;
    }

    public void run(){
        while (true){
            synchronized (this){
                while (currentProduct == null){
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String info = "color,#M" + this.name + "," + this.currentProduct.getColor();
                mObserver.update(info);
                try {
                    this.wait(workingTime*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String defaultColor = "#84f5ef";
                for(int k=0; k<3; k++) {
                    //return to default color and update
                    info = "color,#M" + this.name + "," + defaultColor;
                    mObserver.update(info);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    info = "color,#M" + this.name + "," + this.currentProduct.getColor();
                    //useProduct color and update
                    mObserver.update(info);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //return to default color and update
                info = "color,#M" + this.name + "," + defaultColor;
                mObserver.update(info);
                nextq.addProduct(currentProduct);
                info = "input," + nextq.getName() + "," + currentProduct.getName() + "," + currentProduct.getColor();
                mObserver.update(info);
                currentProduct = null;
                info = "color,#M" + this.name + "," + defaultColor;
                mObserver.update(info);
            }
        }
    }
}