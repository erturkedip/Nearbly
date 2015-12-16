package com.nearblyapp.nearbly.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nearblyapp.nearbly.grades.Order;
import com.nearblyapp.nearbly.grades.OrderList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class NearblyDB {


    private static final String DATABASE_NAME = "CategoriesAndOrders.db";

    private static final String CATEGORY_TABLE_NAME = "categories_table";
    private static final String ORDER_TABLE_NAME = "orders_table";
    private static final String LIST_TABLE_NAME = "list_table";

    private static final String CATEGORY_COL_1 = "ID";
    private static final String CATEGORY_COL_2 = "CATEGORY_NAME";
    private static final String CATEGORY_COL_3 = "CATEGORY_ICON_ID";
    private static final String CATEGORY_COL_4 = "CATEGORY_FLAG";
    private static final String CATEGORY_COL_5 = "CATEGORY_TYPE";


    private static final String ORDER_COL_1 = "ID";
    private static final String ORDER_COL_2 = "CATEGORY_ID";
    private static final String ORDER_COL_3 = "ORDER_TEXT";//siparis
    private static final String ORDER_COL_4 = "ORDER_FLAG";//alındı mı
    private static final String ORDER_COL_5 = "ADDED_LIST";//listeye alındı mı
    private static final String ORDER_COL_6 = "LIST_ID";



    private static final String LIST_COL_1 = "ID";
    private static final String LIST_COL_2 = "LIST_NAME";
    private static final String LIST_COL_3 = "LIST_DATE";
    private static final String LIST_COL_4 = "LIST_FLAG";


    private DatabaseHelper helper;
    private final Context context;
    private  SQLiteDatabase db;

    ArrayList<Integer> result;
    HashMap<Integer, String> resultHash;
    ArrayList<Order> textResult;
    ArrayList<OrderList> listResult;

    public static class DatabaseHelper extends SQLiteOpenHelper{



        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + CATEGORY_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, CATEGORY_NAME TEXT, CATEGORY_ICON_ID INTEGER, CATEGORY_FLAG INTEGER DEFAULT 0, CATEGORY_TYPE TEXT)");
            db.execSQL("create table " + ORDER_TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, CATEGORY_ID INTEGER, ORDER_TEXT TEXT, ORDER_FLAG INTEGER DEFAULT 0, ADDED_LIST INTEGER DEFAULT 0, LIST_ID INTEGER)");
            db.execSQL("create table " + LIST_TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, LIST_NAME TEXT, LIST_DATE TEXT, LIST_FLAG INTEGER DEFAULT 0)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ORDER_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + LIST_TABLE_NAME);
            onCreate(db);
        }

    }


    public int getCategoryImgId(String categoryName){

        Cursor cursor = null;
        int imgId = 0;
        try{

            cursor = this.db.rawQuery("SELECT CATEGORY_ICON_ID FROM categories_table WHERE CATEGORY_NAME = ?", new String[] { categoryName + ""});

            if(cursor.getCount() > 0) {

                cursor.moveToFirst();
                imgId = cursor.getInt(cursor.getColumnIndex("CATEGORY_ICON_ID"));
            }

            return imgId;
        }finally {

            cursor.close();
        }

    }

    public int getCategoryId(String categoryName) {

        Cursor cursor = null;
        int id  = -1;
        try{

            cursor = this.db.rawQuery("SELECT ID FROM categories_table WHERE CATEGORY_NAME =?", new String[]{categoryName + ""});

            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                id = cursor.getInt(cursor.getColumnIndex("ID"));
            }

            return id;
        }finally {

            cursor.close();
        }
    }

    public boolean isCategoryValid(String categoryName) {

        Cursor cursor = null;
        int id  = -1;
        try{

            cursor = this.db.rawQuery("SELECT ID FROM categories_table WHERE CATEGORY_NAME =?", new String[]{categoryName + ""});

            if(cursor.getCount() > 0) {
                return true;
            }

            return false;
        }finally {

            cursor.close();
        }
    }

    public String getCategoryName(int categoryId) {

        Cursor cursor = null;
        String empName = "";
        try{

            cursor = this.db.rawQuery("SELECT CATEGORY_NAME FROM categories_table WHERE ID=?", new String[] {categoryId + ""});

            if(cursor.getCount() > 0) {

                cursor.moveToFirst();
                empName = cursor.getString(cursor.getColumnIndex("CATEGORY_NAME"));
            }

            return empName;
        }finally {

            cursor.close();
        }
    }

    public String getCategoryType(int categoryId) {

        Cursor cursor = null;
        String empName = "";
        try{

            cursor = this.db.rawQuery("SELECT CATEGORY_TYPE FROM categories_table WHERE ID=?", new String[] {categoryId + ""});

            if(cursor.getCount() > 0) {

                cursor.moveToFirst();
                empName = cursor.getString(cursor.getColumnIndex("CATEGORY_TYPE"));
            }

            return empName;
        }finally {

            cursor.close();
        }
    }

    public List<Integer> getSavedCategoryName(int flag) {

        Cursor cursor = null;
        try{

            cursor = this.db.rawQuery("SELECT ID FROM categories_table WHERE CATEGORY_FLAG = ?", new String[] { flag + ""});
            result = new ArrayList<Integer>();
            if(cursor.getCount() > 0) {
                int i = 0;
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                    result.add(cursor.getInt(cursor.getColumnIndex("ID")));
                    i++;
                }
            }

        return result;
        }finally {

            cursor.close();
        }
    }

    public int getCategoryCount() {
        String countQuery = "SELECT  * FROM " + CATEGORY_TABLE_NAME;
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public long insertData(String categoryName, int categoryIconId, String categoryType){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CATEGORY_COL_2, categoryName);
        contentValues.put(CATEGORY_COL_3, categoryIconId);
        contentValues.put(CATEGORY_COL_5, categoryType);
        return this.db.insert(CATEGORY_TABLE_NAME, null, contentValues);

    }

    public long updateData(String categoryId, String categoryName, int categoryIconId, int flag, String type){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CATEGORY_COL_1, categoryId);
        contentValues.put(CATEGORY_COL_2, categoryName);
        contentValues.put(CATEGORY_COL_3, categoryIconId);
        contentValues.put(CATEGORY_COL_4, flag);
        contentValues.put(CATEGORY_COL_5, type);
        return this.db.update(CATEGORY_TABLE_NAME, contentValues, "CATEGORY_NAME = ?", new String[]{categoryName});

    }

    public long addOrder(String orderName, int categoryId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ORDER_COL_2, categoryId);
        contentValues.put(ORDER_COL_3, orderName);
        return this.db.insert(ORDER_TABLE_NAME, null, contentValues);
    }

    public long addList(String listName, String dateValue){
        ContentValues contentValues = new ContentValues();
        contentValues.put(LIST_COL_2, listName);
        contentValues.put(LIST_COL_3, dateValue);
        return this.db.insert(LIST_TABLE_NAME, null, contentValues);
    }

    public int getListId(String listName){

        Cursor cursor = null;
        int id  = -1;
        try{

            cursor = this.db.rawQuery("SELECT ID FROM list_table WHERE LIST_NAME = ?", new String[]{listName + ""});

            if(cursor.getCount() > 0) {

                cursor.moveToFirst();
                id = cursor.getInt(cursor.getColumnIndex("ID"));
            }

            return id;
        }finally {

            cursor.close();
        }
    }

    public boolean isThereListInDb(String listName)
    {
        Cursor cursor = null;
        int id  = -1;
        try{

            cursor = this.db.rawQuery("SELECT ID FROM list_table WHERE LIST_NAME = ?", new String[]{listName + ""});

            if(cursor.getCount() > 0) {
                return true;
            }

        }finally {

            cursor.close();
        }
        return false;
    }

    public ArrayList<Integer> getAllOrdersId(int categoryId, int isAdded){

        Cursor cursor = null;
        try{

            cursor = this.db.rawQuery("SELECT ID FROM orders_table WHERE CATEGORY_ID = "+ categoryId +" AND ADDED_LIST = " + isAdded, null);
            result = new ArrayList<Integer>();
            if(cursor.getCount() > 0) {
                int i = 0;
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                    result.add(cursor.getInt(cursor.getColumnIndex("ID")));
                    i++;
                }

            }

            return result;
        }finally {

            cursor.close();
        }

    }

    /*public HashMap<Integer, String> getAllOrdersTextAndId(int categoryId, int isAdded){

        Cursor cursor = null;
        try{

            cursor = this.db.rawQuery("SELECT ID,ORDER_TEXT FROM orders_table WHERE CATEGORY_ID = "+ categoryId +" AND ADDED_LIST = " + isAdded, null);
            resultHash = new HashMap<Integer, String>();
            if(cursor.getCount() > 0) {
                int i = 0;
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                    resultHash.put(cursor.getInt(cursor.getColumnIndex("ID")), cursor.getString(cursor.getColumnIndex("ORDER_TEXT")));
                    i++;
                }

            }

            return resultHash;
        }finally {

            cursor.close();
        }

    }*/

    public ArrayList<OrderList> getAllOrdersList(){

        Cursor cursor = null;
        try{

            cursor = this.db.rawQuery("SELECT * FROM list_table ", null);
            listResult = new ArrayList<OrderList>();
            if(cursor.getCount() > 0) {
                int i = 0;
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                    listResult.add(new OrderList(cursor.getInt(cursor.getColumnIndex("ID")),cursor.getString(cursor.getColumnIndex("LIST_NAME")),cursor.getString(cursor.getColumnIndex("LIST_DATE"))));
                    i++;
                }

            }

            return listResult;
        }finally {

            cursor.close();
        }

    }

    public ArrayList<Order> getAllOrdersText(int categoryId, int isAdded){

        Cursor cursor = null;
        try{

            cursor = this.db.rawQuery("SELECT * FROM orders_table WHERE CATEGORY_ID = "+ categoryId +" AND ADDED_LIST = " + isAdded, null);
            textResult = new ArrayList<Order>();
            if(cursor.getCount() > 0) {
                int i = 0;
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                    textResult.add(new Order(cursor.getString(cursor.getColumnIndex("ORDER_TEXT")),cursor.getInt(cursor.getColumnIndex("ID"))));
                    i++;
                }

            }

            return textResult;
        }finally {

            cursor.close();
        }

    }

    public long deleteOrder(int orderId){
        return this.db.delete(ORDER_TABLE_NAME, "ID = ?", new String[]{orderId + ""});
    }

    public void deleteOrder(int categoryId, int flag){
        db.execSQL(String.format("DELETE FROM " + ORDER_TABLE_NAME + " WHERE CATEGORY_ID = " + categoryId + " AND ORDER_FLAG = " + flag));
    }

    public void deleteList(int categoryId, int isAdded){
        db.execSQL(String.format("DELETE FROM " + ORDER_TABLE_NAME + " WHERE CATEGORY_ID = " + categoryId + " AND ADDED_LIST = " + isAdded));
    }

    public long deleteList(int listId){
        db.execSQL(String.format("DELETE FROM " + ORDER_TABLE_NAME + " WHERE LIST_ID = " + listId));
        return this.db.delete(LIST_TABLE_NAME, "ID = ?", new String[]{listId + ""});
    }

    public long updateCategory(int categoryId, int flag){

        db.execSQL(String.format("DELETE FROM " + ORDER_TABLE_NAME + " WHERE CATEGORY_ID = " + categoryId + " AND ADDED_LIST = 0"));
        ContentValues contentValues = new ContentValues();
        contentValues.put(CATEGORY_COL_4, flag);
        return this.db.update(CATEGORY_TABLE_NAME, contentValues, "ID = ?", new String[]{categoryId + ""});
    }

    public long deleteNewCategory(int categoryId){
        return this.db.delete(CATEGORY_TABLE_NAME, "ID = ?", new String[]{categoryId + ""});
    }

    public int getOrderCount(int categoryId, int isAdded) {
        String countQuery = "SELECT ID FROM " + ORDER_TABLE_NAME + " WHERE CATEGORY_ID = " + categoryId +" AND ADDED_LIST = " + isAdded;
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public int getListedOrderCount(int listId) {
        String countQuery = "SELECT ID FROM " + ORDER_TABLE_NAME + " WHERE LIST_ID = " + listId;
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public ArrayList<Order> getListsOrder(int listId){
        Cursor cursor = null;
        try{

            cursor = this.db.rawQuery("SELECT * FROM orders_table WHERE  LIST_ID = " + listId, null);
            textResult = new ArrayList<Order>();
            if(cursor.getCount() > 0) {
                int i = 0;
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                    textResult.add(new Order(cursor.getString(cursor.getColumnIndex("ORDER_TEXT")),cursor.getInt(cursor.getColumnIndex("ID"))));
                    i++;
                }

            }

            return textResult;
        }finally {

            cursor.close();
        }
    }

    public int getOrderCount() {
        String countQuery = "SELECT * FROM " + ORDER_TABLE_NAME;
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public int getOrderId(){
        String[] columns = new String[]{ORDER_COL_1,ORDER_COL_2,ORDER_COL_3,ORDER_COL_4};
        Cursor cursor = this.db.query(ORDER_TABLE_NAME, columns, null, null, null, null, null);
        cursor.moveToLast();
        return  cursor.getInt(cursor.getColumnIndex("ID"));
    }

    public long updateOrder(int id, int flag){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ORDER_COL_4, flag);
        return this.db.update(ORDER_TABLE_NAME, contentValues, "ID = ?", new String[]{id + ""});
    }

    public long updateListedOrders(int categoryId, int value, String column){
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, value);
        return this.db.update(ORDER_TABLE_NAME, contentValues, "CATEGORY_ID = "+ categoryId +" AND ADDED_LIST = 0", null);

    }

    public long updateListedOrders(int categoryId, int value) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("ADDED_LIST", 1);
        return this.db.update(ORDER_TABLE_NAME, contentValues, "CATEGORY_ID = "+ categoryId +" AND LIST_ID = " + value, null);
    }


    public boolean isChecked(int orderId){


        String[] columns = new String[]{ORDER_COL_4};
        String WHERE = ORDER_COL_4 + "=?";
        Cursor cursor = null;

        try{
            //cursor = db.query(ORDER_TABLE_NAME, columns , WHERE, new String[] { orderId + "" }, null, null, null);
            cursor = db.query(ORDER_TABLE_NAME, columns, ORDER_COL_1 + "=" + orderId , null, null, null, null);
            cursor.moveToLast();
            return  cursor.getInt(cursor.getColumnIndex(ORDER_COL_4)) == 1;


        }finally {

            cursor.close();
        }
    }

    public boolean isThereUnChecked(int categoryId){


        String[] columns = new String[]{ORDER_COL_4};
        Cursor cursor = null;

        try{
            cursor = db.query(ORDER_TABLE_NAME, columns, ORDER_COL_2 + "=" + categoryId , null, null, null, null);
            if(cursor.getCount() > 0) {
                int i = 0;
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                    if(cursor.getInt(cursor.getColumnIndex(ORDER_COL_4)) == 0)
                        return true;
                    i++;
                }

            }
        }finally {

            cursor.close();
        }
        return false;
    }

    public int isAllUncheck(int categoryId){
        String[] columns = new String[]{ORDER_COL_4};
        Cursor cursor = null;

        try{
            cursor = db.query(ORDER_TABLE_NAME, columns, ORDER_COL_2 + "=" + categoryId +" AND ORDER_FLAG = 0" , null, null, null, null);
            return cursor.getCount();
        }finally {
            cursor.close();
        }
    }

    public int getCounts(String tableName) {
        String countQuery = "SELECT ID FROM " + tableName;
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }
    public NearblyDB(Context context) {
        this.context = context;
    }


    public NearblyDB open()throws SQLException{
        this.helper = new DatabaseHelper(this.context);
        this.db = this.helper.getWritableDatabase();
        return this;
    }

    public  void close(){
        this.helper.close();
    }



}
