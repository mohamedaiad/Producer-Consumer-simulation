package com.example.ProducerConsumerBack;

import java.util.ArrayList;
import java.util.List;

public class CareTaker {
    private List<memento> mementoList=new ArrayList<>();

    public void add(memento state){
        mementoList.add(state);
    }
    public memento get(int index){
        return mementoList.get(index);
    }
    public int getSize(){
        return mementoList.size();
    }

}