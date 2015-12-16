package com.nearblyapp.nearbly;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nearblyapp.nearbly.databases.NearblyDB;
import com.nearblyapp.nearbly.grades.GetFacebookData;

import java.io.IOException;
import java.sql.SQLException;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String PREFS = "prefs";
    private SharedPreferences prefs;

    public Menu menuBar;
    CircleImageView user_img;
    ImageView userCover;
    TextView userName, userEmail, orderCount, listCount, categoryCount;

    NearblyDB nearblyDB = new NearblyDB(UserActivity.this);

    Dialog tDialog;
    Button editBtn, edtProfil;
    int count;
    Bitmap bm;
    BitmapDrawable background;
    private static int RESULT_LOAD_IMAGE = 1;
    ImageView saveProfile, cancel;
    EditText name, surname, email;
    FloatingActionButton edit_information;
    SharedPreferences.Editor prefsEditor;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && null != data) {
            /*Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            bm = BitmapFactory.decodeFile(picturePath);*/
            //SharedPreferences.Editor prefsEditor = prefs.edit();

            Uri photoUri = data.getData();
            // Do something with the photo based on Uri
            Bitmap selectedImage = null;
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Load the selected image into a preview
        if (requestCode == 1){
                Bitmap bMapScaled = scaleToFit(selectedImage, userCover.getWidth(),userCover.getHeight());
                userCover.setImageBitmap(bMapScaled);
                prefsEditor.putString("cover", GetFacebookData.BitMapToString(bMapScaled));
            }
            else if (requestCode == 2) {
                user_img.setImageBitmap(selectedImage);
                prefsEditor.putString("profile_pic", GetFacebookData.BitMapToString(selectedImage));
            }
            prefsEditor.putBoolean("isEdited", true);
            prefsEditor.commit();

        }
    }


    public static Bitmap scaleToFit(Bitmap b, int width, int height)

    {

        float factor = width / (float) b.getWidth();
        float factor2 = height / (float) b.getHeight();

        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor2), (int) (b.getHeight() * factor), true);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.user_toolbar);
        setSupportActionBar(toolbar);

        prefsEditor = prefs.edit();
        prefsEditor.putBoolean("isFromCategoryActivity", true);
        prefsEditor.commit();


        ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_back_arrow);
        actionBar.setDisplayShowHomeEnabled(true);

        Typeface font2 = Typeface.createFromAsset(this.getAssets(), "fonts/Oswald-Stencil.ttf");
        SpannableStringBuilder SS = new SpannableStringBuilder("BEN");
        SS.setSpan(new CustomTypefaceSpan("", font2), 0, SS.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        this.getSupportActionBar().setTitle(SS);

        edtProfil = (Button) findViewById(R.id.edit_profile_img);
        editBtn = (Button) findViewById(R.id.edit_profile);
        user_img = (CircleImageView) findViewById(R.id.user_profile_pic);
        userCover = (ImageView) findViewById(R.id.user_cover_pic);
        edit_information = (FloatingActionButton) findViewById(R.id.edit_information);
        user_img.setEnabled(false);

        userName = (TextView) findViewById(R.id.user_pro_name);
        userEmail = (TextView) findViewById(R.id.user_pro_email);

        orderCount = (TextView) findViewById(R.id.allOrderCount);
        listCount = (TextView) findViewById(R.id.allListCount);
        categoryCount = (TextView) findViewById(R.id.allCategoryCount);

        String imgUrl = prefs.getString("profile_pic", null);
        String coverUrl = prefs.getString("cover", null);


        if (imgUrl != null) {
            user_img.setImageBitmap(GetFacebookData.StringToBitMap(imgUrl));
            BitmapDrawable background = new BitmapDrawable(GetFacebookData.StringToBitMap(coverUrl));
            userCover.setBackgroundDrawable(background);
        }else {
            user_img.setImageResource(R.mipmap.ic_none_profile);
            userCover.setBackgroundResource(R.drawable.background_color);
        }

        userName.setText(prefs.getString("first_name", "Hoş") + " " + prefs.getString("last_name", "Geldiniz"));
        userEmail.setText(prefs.getString("email", ""));

        try {
            nearblyDB.open();
            count = nearblyDB.getCounts("orders_table");
            if (count > 0) {
                orderCount.setText(String.valueOf(count));
            }
            else {
                orderCount.setText(String.valueOf(0));
            }
            nearblyDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            nearblyDB.open();
            count = nearblyDB.getCounts("list_table");
            if (count >0)
                listCount.setText(String.valueOf(count));
            else
                listCount.setText(String.valueOf(0));
            nearblyDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            nearblyDB.open();
            count = nearblyDB.getCounts("categories_table");
            if (count >0)
                categoryCount.setText(String.valueOf(count));
            else
                categoryCount.setText(String.valueOf(0));
            nearblyDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        tDialog = new Dialog(UserActivity.this);
        tDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        tDialog.setContentView(R.layout.edit_info);
        tDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = tDialog.getWindow().getAttributes();
        params.dimAmount = 0.8f;
        tDialog.getWindow().setAttributes(params);
        tDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        saveProfile = (ImageView) tDialog.findViewById(R.id.edit_pro_info);
        cancel = (ImageView) tDialog.findViewById(R.id.cancel_edit);

        saveProfile.setOnClickListener(this);
        cancel.setOnClickListener(this);

        name = (EditText) tDialog.findViewById(R.id.edt_name);
        surname = (EditText) tDialog.findViewById(R.id.edt_surname);
        email = (EditText) tDialog.findViewById(R.id.edt_email);



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



        user_img.setOnClickListener(this);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.user_main, menu);
        menu.findItem(R.id.save_profile).setVisible(false);
        menuBar = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.edit_profile:
                menuBar.findItem(R.id.save_profile).setVisible(true);
                menuBar.findItem(R.id.edit_profile).setVisible(false);
                user_img.setEnabled(true);
                userCover.setAlpha(0.5f);
                edtProfil.setOnClickListener(this);
                edtProfil.setVisibility(View.VISIBLE);
                editBtn.setOnClickListener(this);
                editBtn.setVisibility(View.VISIBLE);
                edit_information.setVisibility(View.VISIBLE);
                edit_information.setOnClickListener(this);
                return true;
            case R.id.save_profile:
                edtProfil.setVisibility(View.GONE);
                editBtn.setVisibility(View.GONE);
                edit_information.setVisibility(View.GONE);
                user_img.setEnabled(false);
                userCover.setAlpha(1.0f);
                menuBar.findItem(R.id.save_profile).setVisible(false);
                menuBar.findItem(R.id.edit_profile).setVisible(true);
                prefsEditor.putBoolean("notLoggedIn", false);
                prefsEditor.commit();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.edit_profile:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        onPickPhoto(1);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                            onPickPhoto(1);
                        }
                    }
                }else {
                    onPickPhoto(1);
                }
                Toast.makeText(this,"Cover", Toast.LENGTH_SHORT).show();
                return;
            case R.id.edit_profile_img:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        onPickPhoto(2);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                            onPickPhoto(2);
                        }
                    }
                }else {
                    onPickPhoto(2);
                }
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                return;
            case R.id.edit_pro_info:
                userName.setText(name.getText().toString().trim() + "  " + surname.getText().toString().trim());
                userEmail.setText(email.getText().toString().trim());
                prefsEditor.putString("first_name", name.getText().toString().trim());
                prefsEditor.putString("last_name", surname.getText().toString().trim());
                prefsEditor.putString("email", email.getText().toString().trim());
                prefsEditor.putBoolean("isEdited", true);
                prefsEditor.commit();
                tDialog.dismiss();
                return;
            case R.id.cancel_edit:
                tDialog.dismiss();
                return;
            case R.id.edit_information:
                name.setText(prefs.getString("first_name", ""));
                surname.setText(prefs.getString("last_name", ""));
                email.setText(prefs.getString("email", ""));
                tDialog.show();
                return;

        }
    }

    public void onPickPhoto(int select) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, select);
        }
    }

    private boolean hasPermission(String perm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
        }
        return false;
    }


}
