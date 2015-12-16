package com.nearblyapp.nearbly;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.nearblyapp.nearbly.grades.GetFacebookData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static LoginButton loginButton;
    private Button continue_btn;
    public static CallbackManager callbackManager;
    private Button share_button;
    ProgressDialog progressDialog;
    public static AccessToken facebookAccessToken;
    SharedPreferences getPrefs;
    public static AccessTokenTracker accessTokenTracker;
    private static final String PREFS = "prefs";
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;
    Boolean isBack = false;


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        if (prefs.getBoolean("isBack", false))
        {
            finish();
        }

        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        continue_btn = (Button) findViewById(R.id.continue_btn);
        continue_btn.setOnClickListener(this);

        loginButton = (LoginButton) findViewById(R.id.login_button);

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                facebookAccessToken = currentAccessToken;

            }
        };
        facebookAccessToken = AccessToken.getCurrentAccessToken();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("onSuccess");
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Bilgiler alınıyor...");
                progressDialog.show();
                String accessToken = loginResult.getAccessToken().getToken();

                Log.i("accessToken", accessToken);

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.i("LoginActivity", response.toString());

                                facebookAccessToken = AccessToken.getCurrentAccessToken();

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
                                prefsEditor.putBoolean("notLoggedIn", false);
                                prefsEditor.commit();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class).putExtras(bFacebookData));
                            }
                        }
                );


                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location, cover");
                request.setParameters(parameters);
                request.executeAsync();


            }


            @Override
            public void onCancel() {
                System.out.println("onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                System.out.println("onError");
                Log.v("LoginActivity", exception.getCause().toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
        finish();
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        prefsEditor = prefs.edit();
        prefsEditor.putBoolean("notLoggedIn", true);
        prefsEditor.commit();
    }
}