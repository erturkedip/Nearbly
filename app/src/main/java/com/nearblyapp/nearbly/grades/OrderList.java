package com.nearblyapp.nearbly.grades;


public class OrderList {

    private int listId;
    private String listName;
    private String listSaveDate;

    public OrderList(int listId, String listName, String listSaveDate){
        this.listId = listId;
        this.listName = listName;
        this.listSaveDate = listSaveDate;
    }

    public void setListId(int listId){
        this.listId = listId;
    }

    public void setListName(String listName){
        this.listName = listName;
    }

    public void setListSaveDate(String listSaveDate){
        this.listSaveDate = listSaveDate;
    }

    public int getListId(){
        return listId;
    }

    public String getListName(){
        return listName;
    }

    public String getListSaveDate(){
        return listSaveDate;
    }



}
