package com.nearblyapp.nearbly.grades;


public class Order {

    private String order;
    private int id;

    public Order(String order, int id){
        this.order = order;
        this.id = id;
    }

    public void setOrder(String order){
        this.order = order;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getOrder(){
        return order;
    }

    public int getId(){
        return id;
    }

}
