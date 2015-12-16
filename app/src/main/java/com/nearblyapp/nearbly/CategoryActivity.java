package com.nearblyapp.nearbly;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.MapView;
import com.nearblyapp.nearbly.adapters.OrderAdapter;
import com.nearblyapp.nearbly.databases.NearblyDB;
import com.nearblyapp.nearbly.grades.LocationsList;
import com.nearblyapp.nearbly.grades.Order;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class CategoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, OnMapReadyCallback, View.OnClickListener {


    public ListView orderList, doneOrderList;
    public static OrderAdapter orderAdapter;
    public  ArrayList<Order> orders;
    public static View orderView;
    ArrayList<Integer> ordersId;
    MapView mapView;
    private GoogleMap mMap;

    private String listeTamamla = "listeTamamla";
    private String listeSil = "listeSil";

    double latitude;
    double longitude;
    int categoryId;
    int orderCount;

    EditText input;
    ImageView accept;
    String listName;
    NearblyDB orderDB = new NearblyDB(CategoryActivity.this);

    ImageButton addOrder;
    EditText edtOrder;

    private static final String PREFS = "prefs";

    private LocationsList mNearbyList;

    SharedPreferences prefs;
    public static Menu menuBar;
    String categoryName;
    AlertDialog.Builder alertDialog;
    AlertDialog dialog;
    Button completeBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.cat_toolbar);
        setSupportActionBar(toolbar);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);


        final SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean("isFromCategoryActivity", true);
        prefsEditor.commit();


        latitude = getIntent().getExtras().getDouble("latitude");
        longitude = getIntent().getExtras().getDouble("longitude");
        categoryId = getIntent().getExtras().getInt("categoryId");
        Bundle b = getIntent().getExtras(); //Get the intent's extras

        mNearbyList = b.getParcelable("locations"); //get our list


        ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_back_arrow);
        actionBar.setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(getIntent().getExtras().getInt("logoId"));
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        addOrder = (ImageButton) findViewById(R.id.add_order);
        addOrder.setOnClickListener(this);

        edtOrder = (EditText) findViewById(R.id.order_txt);



        categoryName = getIntent().getExtras().getString("categoryName");
        //Toast.makeText(this,categoryName, Toast.LENGTH_SHORT).show();

       // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude , Toast.LENGTH_LONG).show();


        Typeface font2 = Typeface.createFromAsset(this.getAssets(), "fonts/Oswald-Stencil.ttf");
        SpannableStringBuilder SS = new SpannableStringBuilder(categoryName);
        SS.setSpan(new CustomTypefaceSpan("", font2), 0, SS.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        this.getSupportActionBar().setTitle(SS);


        orderList = (ListView) findViewById(R.id.orderList);

        ordersId = new ArrayList<Integer>();

        try {
            orderDB.open();
            orderCount = orderDB.getOrderCount(categoryId, 0);
            orderDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            orderDB.open();
            if (orderCount > 0) {
                ordersId = orderDB.getAllOrdersId(categoryId, 0);
            }
            orderDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        orders = new ArrayList<Order>();

        try {
            orderDB.open();
            if (orderCount> 0) {
                orders = orderDB.getAllOrdersText(categoryId, 0);
            }
            orderDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        orderAdapter = new OrderAdapter(this, orders, categoryId);

        orderList.setAdapter(orderAdapter);
        orderAdapter.notifyDataSetChanged();

        orderList.setOnItemClickListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.order_main, menu);
        if (orderCount > 0)
        {
            menu.findItem(R.id.delete_list).setEnabled(true);
            menu.findItem(R.id.delete_list).setIcon(R.mipmap.ic_delete_list);
            menu.findItem(R.id.complete_list).setEnabled(false);
            try {
                orderDB.open();
                if (orderDB.isAllUncheck(categoryId) != orderCount) {
                    menu.findItem(R.id.complete_list).setEnabled(true);
                    menu.findItem(R.id.complete_list).setIcon(R.mipmap.ic_complete);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else {

            menu.findItem(R.id.delete_list).setEnabled(false);
            menu.findItem(R.id.complete_list).setEnabled(false);
        }
        menuBar = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.delete_list:
                alertDialog = new AlertDialog.Builder(this);
                showSettingsAlert(listeSil);
                return true;
            case R.id.complete_list:
                alertDialog = new AlertDialog.Builder(this);
                showSettingsAlert(listeTamamla);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void showSettingsAlert(String islem){


        if (islem == listeSil) {

            alertDialog.setTitle("Listeyi Sil");

            try {
                orderDB.open();
                if (orderDB.isThereUnChecked(categoryId))
                    alertDialog.setMessage("Alışverişi tamamlamadınız siparişler silinecek, listeyi silmek istiyor musunuz?");
                else
                    alertDialog.setMessage("Alışverişi tamamladınız listeyi silmek istiyor musunuz?");
            } catch (SQLException e) {
                e.printStackTrace();
            }


            alertDialog.setPositiveButton("SİL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        orderDB.open();
                        orderDB.deleteList(categoryId, 0);
                        orderDB.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);


                }
            });

            alertDialog.setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }

        else if (islem == listeTamamla) {


            accept = new ImageView(this);
            accept.setImageResource(R.mipmap.ic_warning);

            input = new EditText(this);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            LinearLayout info = new LinearLayout(this);

            info.setLayoutParams(layoutParams);
            info.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            info.setOrientation(LinearLayout.HORIZONTAL);
            input.setWidth(500);

            info.addView(input);
            info.addView(accept);

            alertDialog.setView(info);


            alertDialog.setTitle("Alışverişi Tamamla");

            try {
                orderDB.open();
                if (orderDB.isThereUnChecked(categoryId))
                    alertDialog.setMessage("Alışverişi tamamlamadınız işaretlemediğiniz siparişler silinecek, listeyi kaydetmek ister misiniz?");
                else
                    alertDialog.setMessage("Alışverişi tamamladınız listeyi kaydetmek ister misiniz?");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            alertDialog.setPositiveButton("KAYDET", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    listName = input.getText().toString().trim() + "-" + categoryName;
                    if (!listName.equals("")) {
                        try {
                            orderDB.open();
                            orderDB.addList(listName, getDateTime());
                            orderDB.deleteOrder(categoryId, 0);
                            orderDB.updateListedOrders(categoryId, orderDB.getListId(listName), "LIST_ID");
                            orderDB.updateListedOrders(categoryId, orderDB.getListId(listName));
                            orderDB.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(CategoryActivity.this, "Lütfen bir liste adı giriniz!", Toast.LENGTH_SHORT).show();
                    }
                }
            });



            input.addTextChangedListener(new TextWatcher() {

                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {

                }

                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    //Toast.makeText(CategoryActivity.this, "before", Toast.LENGTH_SHORT).show();

                }

                public void afterTextChanged(Editable s) {
                    //Toast.makeText(CategoryActivity.this, "after", Toast.LENGTH_SHORT).show();


                    if (input.getText().toString().trim().equals("")){
                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE)
                                .setEnabled(false);
                        accept.setImageResource(R.mipmap.ic_warning);
                    }
                    else {
                        accept.setImageResource(R.mipmap.ic_accept);
                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE)
                                .setEnabled(true);
                    }
                    try {
                        orderDB.open();
                        if (orderDB.isThereListInDb(input.getText().toString().trim() + "-" + categoryName)) {
                            ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE)
                                    .setEnabled(false);
                            accept.setImageResource(R.mipmap.ic_warning);
                            Toast.makeText(CategoryActivity.this, "Bu isimde bir liste bulunmaktadır.", Toast.LENGTH_SHORT).show();
                        }
                        orderDB.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            });


            alertDialog.setNeutralButton("İPTAL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                }
            });
            // on pressing cancel button
            alertDialog.setNegativeButton("SİL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        orderDB.open();
                        orderDB.deleteList(categoryId, 0);
                        orderDB.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
        }
        // Showing Alert Message
        dialog = alertDialog.create();

        dialog.show();

        if (islem == listeTamamla) {
            ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE)
                    .setEnabled(false);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(this, "Alım iptal", Toast.LENGTH_SHORT).show();
    }

    public static void deleteOrder(){
        orderAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LatLng currentLoc = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(currentLoc).title("Buradasınız!"));

        for (int i = 0; i<mNearbyList.size();i++)
        {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mNearbyList.get(i).getLatitude(), mNearbyList.get(i).getLongitude()))
                    .title(mNearbyList.get(i).getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 15));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_order:

                String newOrder = edtOrder.getText().toString();
                if(!newOrder.equals("")) {
                    try {
                        orderDB.open();
                        orderDB.addOrder(newOrder, categoryId);
                        orders.add(new Order(newOrder, orderDB.getOrderId()));
                        orderDB.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    orderAdapter.notifyDataSetChanged();
                    orderList.setAdapter(orderAdapter);
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    menuBar.findItem(R.id.delete_list).setEnabled(true);
                    menuBar.findItem(R.id.delete_list).setIcon(R.mipmap.ic_delete_list);
                }

                edtOrder.setText("");
                break;
        }
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
