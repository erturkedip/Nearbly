package com.nearblyapp.nearbly;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nearblyapp.nearbly.adapters.CategoryAdapter;
import com.nearblyapp.nearbly.adapters.ListAdapter;
import com.nearblyapp.nearbly.adapters.NewCategoryAdapter;
import com.nearblyapp.nearbly.grades.Category;
import com.nearblyapp.nearbly.grades.GetFacebookData;
import com.nearblyapp.nearbly.databases.NearblyDB;
import com.nearblyapp.nearbly.grades.LocationsList;
import com.nearblyapp.nearbly.grades.Order;
import com.nearblyapp.nearbly.grades.OrderList;
import com.nearblyapp.nearbly.grades.PlaceTypes;
import com.nearblyapp.nearbly.place.Place;
import com.nearblyapp.nearbly.place.PlacesService;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener, LocationListener, View.OnClickListener {

    ListView liste;
    ArrayList<Category> categories;
    public static CategoryAdapter arrayAdapter;
    Dialog tDialog;
    EditText tweet_text;
    ImageView addCAtegory, cancel;
    String newCategory;
    int iconPos;
    Spinner categorySpinner;
    ArrayAdapter<String> spinnerAdapter;
    List<Integer> categoryArray, unCategoriedArray;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    NearblyDB categoryDb = new NearblyDB(MainActivity.this);
    double longitude;
    double latitude;
    boolean isEmpty;
    public static List<String> categoryList = new ArrayList<>();
    private SharedPreferences prefs;

    CircleImageView user_img;
    TextView userName;
    TextView userEmail;
    RelativeLayout userCover;
    Bitmap bitmap;

    LocationsList locationsList;

    final String CLIENT_ID = "AIA2DBWXXLZ0MAJBSDINPHXZIWENXLE51Y4T234RV2YJEBM4";
    final String CLIENT_SECRET = "TVTPK2WYT12JPDOTE444DMWSP1Y3PEAYWEHWAMVDXVAIRVRM";

    private ArrayList<Place> mNearbyList;
    private ProgressDialog mProgress;
    public static ListAdapter listAdapter;
    NewCategoryAdapter newCategoryAdapter;
    ArrayList<OrderList> orderLists;
    ArrayList<Category> newCategoryList;
    Boolean isGPSEnabled, isNetworkEnabled;

    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int INITIAL_REQUEST = 1337;
    private static final int LOCATION_REQUEST = INITIAL_REQUEST + 3;
    private static final String PREFS = "prefs";

    ProgressDialog progressDialog;
    Bundle bFacebookData;
    boolean fromCategory;

    LoginButton logout;
    LoginActivity loginActivity;

    FloatingActionButton fab;
    SharedPreferences.Editor prefsEditor;

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LoginActivity.callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        LoginActivity.callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        logout = (LoginButton) findViewById(R.id.logout);
        logout.removeCallbacks(new Runnable() {
            @Override
            public void run() {
                AccessToken.setCurrentAccessToken(null);
            }
        });

        LoginActivity.accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                LoginActivity.facebookAccessToken = currentAccessToken;

            }
        };

        logout.registerCallback(LoginActivity.callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("onSuccess");
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Bilgiler alınıyor...");
                progressDialog.show();
                String accessToken = loginResult.getAccessToken().getToken();

                Log.i("accessToken", accessToken);

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.i("LoginActivity", response.toString());

                                LoginActivity.facebookAccessToken = AccessToken.getCurrentAccessToken();
                                Bundle bFacebookData = GetFacebookData.getFacebookData(object);
                                URL picURL, coverURL;
                                Bitmap bm;
                                String bmStrPro = "" , bmStrCov = "";

                                try {
                                    picURL = new URL(bFacebookData.getString("profile_pic"));
                                    bm = new GetFacebookData.DownloadImagesTask(picURL).execute().get();
                                    bmStrPro = GetFacebookData.BitMapToString(bm);

                                    coverURL = new URL(bFacebookData.getString("cover"));
                                    bm = new GetFacebookData.DownloadImagesTask(coverURL).execute().get();
                                    bmStrCov = GetFacebookData.BitMapToString(bm);

                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }

                                SharedPreferences.Editor prefsEditor = prefs.edit();
                                prefsEditor.putString("first_name", bFacebookData.getString("first_name"));
                                prefsEditor.putString("last_name", bFacebookData.getString("last_name"));
                                prefsEditor.putString("email", bFacebookData.getString("email"));
                                prefsEditor.putString("profile_pic", bmStrPro);
                                prefsEditor.putString("cover", bmStrCov);
                                prefsEditor.commit();

                                userName.setText(bFacebookData.getString("first_name") + " " + bFacebookData.getString("last_name"));
                                userEmail.setText(bFacebookData.getString("email"));
                                new LoadProfile(bFacebookData.getString("profile_pic"), bFacebookData.getString("cover")).execute();

                            }
                        }
                );


                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location, cover"); // Parámetros que pedimos a facebook
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        mNearbyList = new ArrayList<Place>();
        mProgress = new ProgressDialog(this);
        categoryList.clear();

        mProgress.setMessage("Bilgiler Alınıyor...");

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled) {
            showSettingsAlert();
        }

        if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 10, this);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 10, this);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
            }
        }

        prefsEditor = prefs.edit();


        latitude = Double.longBitsToDouble(prefs.getLong("latitude", 0));
        longitude = Double.longBitsToDouble(prefs.getLong("longitude", 0));
        fromCategory = prefs.getBoolean("isFromCategoryActivity", false);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        user_img = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name);
        userEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_email);
        userCover = (RelativeLayout) navigationView.getHeaderView(0).findViewById(R.id.cover_image);

        LoginActivity.accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                LoginActivity.facebookAccessToken = currentAccessToken;

            }
        };

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Bilgiler alınıyor...");
        progressDialog.show();

        LoginActivity.facebookAccessToken = AccessToken.getCurrentAccessToken();

        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(final JSONObject object, GraphResponse response) {
                        Log.i("LoginActivity", response.toString());
                        // Get facebook data from login
                        bFacebookData = GetFacebookData.getFacebookData(object);
                        userName.setText(bFacebookData.getString("first_name") + " " + bFacebookData.getString("last_name"));
                        userEmail.setText(bFacebookData.getString("email"));
                        new LoadProfile(bFacebookData.getString("profile_pic"), bFacebookData.getString("cover")).execute();
                    }
                }
        );


        if (isInternetConnected(this) && !fromCategory && LoginActivity.facebookAccessToken != null && !prefs.getBoolean("isEdited", false)) {

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location, cover");
            request.setParameters(parameters);
            request.executeAsync();
            prefsEditor.putBoolean("notLoggedIn", false);
            prefsEditor.commit();

        } else if(prefs.getBoolean("notLoggedIn", false)){

            user_img.setImageResource(R.mipmap.ic_none_profile);
            userCover.setBackgroundResource(R.drawable.background_color);
            userName.setText("Hoş Geldiniz!");
            userEmail.setText("Facebook ile bilgilerini tamamlayabilirsin.");
            progressDialog.dismiss();

        } else {
            prefsEditor.putBoolean("notLoggedIn", false);
            prefsEditor.commit();
            prefsEditor.putBoolean("isFromCategoryActivity", false);
            prefsEditor.commit();
            user_img.setImageBitmap(GetFacebookData.StringToBitMap(prefs.getString("profile_pic", null)));
            BitmapDrawable background = new BitmapDrawable(GetFacebookData.StringToBitMap(prefs.getString("cover", null)));
            userCover.setBackgroundDrawable(background);
            userName.setText(prefs.getString("first_name", null) + " " + prefs.getString("last_name", null));
            userEmail.setText(prefs.getString("email", null));
            progressDialog.dismiss();
        }

        prefsEditor.putBoolean("isFirstTime", false);
        prefsEditor.commit();

        final ArrayList<Integer> iconList = new ArrayList<Integer>();
        iconList.add(R.mipmap.ic_market);
        iconList.add(R.mipmap.ic_kirtasiye);
        iconList.add(R.mipmap.ic_baskect);
        iconList.add(R.mipmap.ic_terminal);
        iconList.add(R.mipmap.ic_petrol_istasyonu);
        iconList.add(R.mipmap.ic_eczane);
        iconList.add(R.mipmap.ic_hirdavat);
        iconList.add(R.mipmap.ic_kasap);
        iconList.add(R.mipmap.ic_magaza);
        iconList.add(R.mipmap.ic_terzi);

        final String[] category = new String[]{
                "MARKET",
                "KIRTASİYE",
                "PAZAR",
                "TERMİNAL",
                "PETROL İSTASYONU",
                "ECZANE",
                "HIRDAVAT",
                "ALIŞVERİŞ MERKEZİ",
                "ELEKTRONİK",
                "GİYİM - TERZİ"
        };

        String[] type = new String[]{
                "grocery_or_supermarket",
                "book_store",
                "grocery_or_supermarket",
                "bus_station",
                "gas_station",
                "pharmacy",
                "hardware_store",
                "shopping_mall",
                "electronics_store",
                "clothing_store"
        };

        //mFsqApp = new FoursquareApp(this, CLIENT_ID, CLIENT_SECRET);


        try {
            categoryDb.open();
            if (categoryDb.getCategoryCount() == 0) isEmpty = false;
            else isEmpty = true;
            categoryDb.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        if (!isEmpty) {
            for (int i = 0; i < category.length; i++) {
                try {
                    categoryDb.open();
                    categoryDb.insertData(category[i], iconList.get(i), type[i]);
                    categoryDb.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }


        liste = (ListView) findViewById(R.id.categoryList);
        categories = new ArrayList<Category>();

        arrayAdapter = new CategoryAdapter(this, categories);

        try {
            categoryDb.open();
            categoryArray = categoryDb.getSavedCategoryName(1);
            unCategoriedArray = categoryDb.getSavedCategoryName(0);
            categoryDb.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < unCategoriedArray.size(); i++) {
            try {
                categoryDb.open();
                categoryList.add(categoryDb.getCategoryName(unCategoriedArray.get(i)));
                categoryDb.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        String name = "";
        int categoryId = -1;
        for (int i = 0; i < categoryArray.size(); i++) {
            try {
                categoryDb.open();
                name = categoryDb.getCategoryName(categoryArray.get(i));
                categoryId = categoryDb.getCategoryId(name);
                categories.add(new Category(categoryDb.getCategoryImgId(name), name, categoryId, categoryDb.getOrderCount(categoryId, 0), categoryDb.getCategoryType(categoryId)));
                categoryDb.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        arrayAdapter.notifyDataSetChanged();


        liste.setAdapter(arrayAdapter);

        liste.setOnItemClickListener(this);


        Typeface font2 = Typeface.createFromAsset(this.getAssets(), "fonts/Oswald-Stencil.ttf");
        SpannableStringBuilder SS = new SpannableStringBuilder("Nearbly");
        SS.setSpan(new CustomTypefaceSpan("", font2), 0, SS.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        this.getSupportActionBar().setTitle(SS);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                tDialog = new Dialog(MainActivity.this);
                tDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                tDialog.setContentView(R.layout.add_category_activity);


                categorySpinner = (Spinner) tDialog.findViewById(R.id.spinner);
                // Create an ArrayAdapter using the string array and a default spinner layout
                spinnerAdapter = new ArrayAdapter(MainActivity.this, R.layout.spinner_item, categoryList);
                // Specify the layout to use when the list of choices appears
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                categorySpinner.setAdapter(spinnerAdapter);

                categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        newCategory = categorySpinner.getSelectedItem().toString();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                tDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams params = tDialog.getWindow().getAttributes();
                params.dimAmount = 0.8f;
                tDialog.getWindow().setAttributes(params);
                tDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                addCAtegory = (ImageView) tDialog.findViewById(R.id.post_tweet);
                addCAtegory.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        int categoryId = 0;

                        try {
                            categoryDb.open();
                            categoryId = categoryDb.getCategoryId(newCategory);
                            categories.add(new Category(categoryDb.getCategoryImgId(newCategory), newCategory, categoryId, categoryDb.getOrderCount(categoryId, 0), categoryDb.getCategoryType(categoryId)));
                            categoryDb.updateData(String.valueOf(categoryDb.getCategoryId(newCategory)), newCategory, categoryDb.getCategoryImgId(newCategory), 1, categoryDb.getCategoryType(categoryId));
                            categoryDb.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        categoryList.remove(newCategory);
                        iconList.remove(iconPos);
                        spinnerAdapter.notifyDataSetChanged();

                        liste.setAdapter(arrayAdapter);
                        arrayAdapter.notifyDataSetChanged();
                        tDialog.dismiss();
                    }
                });

                cancel = (ImageView) tDialog.findViewById(R.id.cancel_it);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tDialog.dismiss();
                    }
                });


                WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();

                Point size = new Point();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                    display.getSize(size);
                }
                int width = size.x;

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(tDialog.getWindow().getAttributes());
                lp.width = width - 200;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                tDialog.getWindow().setAttributes(lp);
                tDialog.show();
            }
        });


    }

    public static boolean isInternetConnected(Context ctx) {
        ConnectivityManager connectivityMgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi != null) {
            if (wifi.isConnected()) {
                return true;
            }
        }
        if (mobile != null) {
            if (mobile.isConnected()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean hasPermission(String perm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm));
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.user_profile:
                startActivity(new Intent(MainActivity.this, UserActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_categories) {

            fab.setVisibility(View.VISIBLE);
            liste.setAdapter(arrayAdapter);

        } else if (id == R.id.nav_lists) {

            fab.setVisibility(View.GONE);

            orderLists = new ArrayList<OrderList>();

            try {
                categoryDb.open();
                orderLists = categoryDb.getAllOrdersList();
                categoryDb.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            listAdapter = new ListAdapter(this, orderLists);

            liste.setAdapter(listAdapter);

            registerForContextMenu(liste);

        } else if (id == R.id.nav_new_category) {

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Kategoriler alınıyor...");
            progressDialog.show();

            newCategoryList = new ArrayList<Category>();

            fab.setVisibility(View.GONE);
            PlaceTypes placeTypes = new PlaceTypes();
            String catName;
            int imgId = -1;

            for (Map.Entry<String, String> entryName  : placeTypes.getPlaceTypesHash().entrySet())
            {
                catName = entryName.getValue();

                for (Map.Entry<String, Integer> entryImg  : placeTypes.getPlaceIconHash().entrySet())
                {
                    if (entryName.getKey() == entryImg.getKey()) {
                        imgId = entryImg.getValue();
                        newCategoryList.add(new Category(imgId,catName,entryName.getKey()));
                    }

                }

            }

            newCategoryAdapter = new NewCategoryAdapter(this, newCategoryList);

            liste.setAdapter(newCategoryAdapter);
            progressDialog.dismiss();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        if (v.getId()==R.id.categoryList) {

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            OrderList list = ((OrderList) listAdapter.getItem(info.position));
            menu.setHeaderTitle(list.getListName());

            try {
                categoryDb.open();
                ArrayList<Order> orderList = categoryDb.getListsOrder(list.getListId());
                for (int i = 0; i < orderList.size(); i++) {
                    menu.add(Menu.NONE, i, i, orderList.get(i).getOrder());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.logout:
                LoginManager.getInstance().logOut();
        }
    }


    public class LoadProfile extends AsyncTask<String, String, Bitmap> {

        Bitmap bitmapCover;
        String url = null;
        String cover = null;
        GetFacebookData getData;

        public LoadProfile(String url, String cover) {
            this.url = url;
            this.cover = cover;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected Bitmap doInBackground(String... args) {

            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
                bitmapCover = BitmapFactory.decodeStream((InputStream) new URL(cover).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            user_img.setImageBitmap(image);
            BitmapDrawable background = new BitmapDrawable(GetFacebookData.StringToBitMap(prefs.getString("cover", null)));
            userCover.setBackgroundDrawable(background);

            progressDialog.dismiss();
            progressDialog = null;

        }
    }

    private void loadNearbyPlaces(final double latitude, final double longitude, final int position, final String type) {
        mProgress.show();

        new Thread() {
            @Override
            public void run() {
                int what = 0;

                try {

                    findNearLocation(latitude, longitude, type);

                } catch (Exception e) {
                    what = 1;
                    Log.d("YER", e.toString());
                    e.printStackTrace();
                }

                Message msg = new Message();
                Bundle b = new Bundle();
                b.putInt("what", what);
                b.putInt("position", position);
                msg.setData(b);
                mHandler.sendMessage(msg);


            }
        }.start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mProgress.dismiss();
            Bundle getb = msg.getData();
            int what = getb.getInt("what");
            int position = getb.getInt("position");

            if (what == 0) {
                /*if (mNearbyList.size() == 0) {
                    Toast.makeText(MainActivity.this, "No nearby places available", Toast.LENGTH_SHORT).show();
                    return;
                }*/

                Bundle sendb = new Bundle();

                sendb.putParcelable("locations", locationsList);

                Category c = categories.get(position);

                Intent i = new Intent(MainActivity.this, CategoryActivity.class);
                i.putExtra("categoryName", c.getCat_name());
                i.putExtra("logoId", c.getImg_id());
                i.putExtra("latitude", latitude);
                i.putExtra("longitude", longitude);
                i.putExtra("categoryId", c.getCategoryId());
                i.putExtras(sendb);
                startActivity(i);

            } else {
                Bundle sendb = new Bundle();

                sendb.putParcelable("locations", locationsList);

                Category c = categories.get(position);

                Intent i = new Intent(MainActivity.this, CategoryActivity.class);
                i.putExtra("categoryName", c.getCat_name());
                i.putExtra("logoId", c.getImg_id());
                i.putExtra("latitude", latitude);
                i.putExtra("longitude", longitude);
                i.putExtra("categoryId", c.getCategoryId());
                i.putExtras(sendb);
                startActivity(i);

                Toast.makeText(MainActivity.this, "Failed to load nearby places", Toast.LENGTH_SHORT).show();

            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getAdapter() == arrayAdapter) {
            Category c = categories.get(position);
            loadNearbyPlaces(latitude, longitude, position, c.getType());
        }else if(parent.getAdapter() == listAdapter)
        {
            this.openContextMenu(view);
        }

    }

    @Override
    public void onLocationChanged(Location location) {

        Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + location.getLatitude() + "\nLong: " + location.getLongitude(), Toast.LENGTH_LONG).show();

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Editor prefsEditor = prefs.edit();
        prefsEditor.putLong("latitude", Double.doubleToLongBits(location.getLatitude()));
        prefsEditor.putLong("longitude", Double.doubleToLongBits(location.getLongitude()));
        prefsEditor.commit();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean("isBack", true);
        prefsEditor.commit();
        finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    public void findNearLocation(Double latitude, Double longitude, String type) {

        PlacesService service = new PlacesService("AIzaSyDrSX6P4IP96DB_WTk-qwwrfJrxJqw3ijM");

        List<Place> findPlaces = service.findPlaces(latitude, longitude, type);

        locationsList = new LocationsList();

        for (int i = 0; i < findPlaces.size(); i++) {
            locationsList.add(new Place(findPlaces.get(i).getId(), findPlaces.get(i).getName(), findPlaces.get(i).getLatitude(), findPlaces.get(i).getLongitude()));
        }

        mNearbyList.addAll(findPlaces);
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("GPS Ayarları");

        alertDialog.setMessage("GPS ulaşılabilir değil navigasyon hizmeti almak için servis aktif edilsin mi?");

        alertDialog.setPositiveButton("AYARLAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
}
