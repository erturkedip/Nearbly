package com.nearblyapp.nearbly.grades;

public class Category {

    private int img_id;
    private String cat_name;
    private int orderCount;
    private int categoryId;
    private String type;

    Category(){

    }

    public Category(int img, String cate, int categoryId, int orderCount, String type){

        this.img_id = img;
        this.cat_name = cate;
        this.categoryId = categoryId;
        this.orderCount = orderCount;
        this.type = type;
    }

    public Category(int img, String cate, String type)
    {
        this.img_id = img;
        this.cat_name = cate;
        this.type = type;
    }

    public void setImg_idId(int img){img_id  = img;}

    public void setCat_name(String cate){
        cat_name = cate;
    }

    public void setCategoryId(int id){
        categoryId = id;
    }

    public void setOrderCount(int oCount){ orderCount = oCount; }

    public void setType(String type){ this.type = type; }

    public int getImg_id(){ return img_id; }

    public String getCat_name(){ return cat_name; }

    public int getCategoryId(){ return categoryId; }

    public int getOrderCount(){ return orderCount; }

    public String getType(){return type;}
}
