package com.nearblyapp.nearbly.grades;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class GetFacebookData {


    public static Bundle getFacebookData(JSONObject object) {
        Bundle bundle = null;
        try {
            bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
            try {
                bundle.putString("idFacebook", id);
                if (object.has("first_name"))
                    bundle.putString("first_name", object.getString("first_name"));
                if (object.has("last_name"))
                    bundle.putString("last_name", object.getString("last_name"));
                if (object.has("email"))
                    bundle.putString("email", object.getString("email"));
                if (object.has("gender"))
                    bundle.putString("gender", object.getString("gender"));
                if (object.has("birthday"))
                    bundle.putString("birthday", object.getString("birthday"));
                if (object.has("location"))
                    bundle.putString("location", object.getJSONObject("location").getString("name"));
                if (object.has("cover"))
                    bundle.putString("cover", object.getJSONObject("cover").getString("source"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return bundle;

        } catch (Exception e) {

        }
        return bundle;
    }

    public static Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);

            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static Bitmap getCircleImage(Bitmap bitmap){

        Bitmap image_circle = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        BitmapShader shader = new BitmapShader (bitmap,  Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);
        Canvas c = new Canvas(image_circle);
        c.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, bitmap.getWidth()/2 , paint);

        return image_circle;
    }

    public static class DownloadImagesTask extends AsyncTask<ImageView, Void, Bitmap> {
        //Profile pic url
        public URL url;

        public DownloadImagesTask(URL url){
            this.url = url;
        }
        @Override
        protected Bitmap doInBackground(ImageView... imageViews) {
            Bitmap bmp = null;
            try {

                URL urls = url;
                bmp = BitmapFactory.decodeStream(urls.openConnection().getInputStream());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap result) {


        }
    }


}
