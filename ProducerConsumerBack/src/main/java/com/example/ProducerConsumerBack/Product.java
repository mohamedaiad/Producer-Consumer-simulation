package com.example.ProducerConsumerBack;

import java.awt.*;

public class Product {
     private String name;
     private String color;

    public Product(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
    public String getColor() {
        return color;
    }
//
//    public void setColor(Color color) {
//        this.color = color;
//    }
}
