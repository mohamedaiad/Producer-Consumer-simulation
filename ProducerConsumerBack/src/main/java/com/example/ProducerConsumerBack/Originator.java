package com.example.ProducerConsumerBack;

public class Originator {
    private String state;

    public void setState(String state){
        this.state=state;
    }

    public String getState() {
        return state;
    }

    public memento saveStateToMemento() {
        return new memento(state);
    }

    public void getStateFromMemento(memento memento){
        state = memento.getState();
    }
}