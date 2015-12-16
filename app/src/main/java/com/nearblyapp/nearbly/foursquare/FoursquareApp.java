package com.nearblyapp.nearbly.foursquare;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class FoursquareApp {
	private FsqAuthListener mListener;
	private ProgressDialog mProgress;
	private String mTokenUrl;
	private String mAccessToken;

	String hata;

	public static final String CALLBACK_URL = "https://foursquare.com/ediperturk";
	private static final String AUTH_URL = "https://foursquare.com/oauth2/authenticate?response_type=code";
	private static final String TOKEN_URL = "https://foursquare.com/oauth2/access_token?grant_type=authorization_code";	
	private static final String API_URL = "https://api.foursquare.com/v2";


	String CLIENT_ID;
	String CLIENT_SECRET;
	
	private static final String TAG = "FoursquareApi";
	
	public FoursquareApp(Context context, String clientId, String clientSecret) {
		this.CLIENT_ID = clientId;
		this.CLIENT_SECRET = clientSecret;


	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == 1) {
				if (msg.what == 0) {
				} else {
					mProgress.dismiss();

					mListener.onFail("Failed to get access token");
				}
			} else {
				mProgress.dismiss();

				mListener.onSuccess();
			}
		}
	};
	
	public ArrayList<FoursquareVenue> getNearby(double latitude, double longitude) throws Exception {
		ArrayList<FoursquareVenue> venueList = new ArrayList<FoursquareVenue>();
		
		try {
			String v	= timeMilisToString(System.currentTimeMillis()); 
			String ll 	= String.valueOf(latitude) + "," + String.valueOf(longitude);
			//URL url 	= new URL("https://api.foursquare.com/v2/venues/search?ll=" + ll + "&oauth_token=" + mAccessToken + "&v=" + v);

			URL url = new URL("https://api.foursquare.com/v2/venues/search?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&v=" + v +"&ll="+ ll +"&limit=50&radius=1000");

			//https://api.foursquare.com/v2/venues/search?ll=40.7,30&oauth_token=LPPKUAHJYBY1ULRBJT3MKY4CP01EF1RFJSBJBJGF5V2BZ035&v=20151204
			
			Log.d(TAG, "Opening URL " + url.toString());
			
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);
			urlConnection.connect();
			
			String response	= streamToString(urlConnection.getInputStream());
			JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
			
			JSONArray groups = (JSONArray) jsonObj.getJSONObject("response").getJSONArray("venues");
			
			int length = groups.length();
			
			if (length > 0) {
				for (int i = 0; i < length; i++) {

					JSONObject item = groups.getJSONObject(i);

					FoursquareVenue venue 	= new FoursquareVenue();

					venue.setId(item.getString("id"));
					venue.setName(item.getString("name"));

					JSONObject location = (JSONObject) item.getJSONObject("location");

					Location loc 	= new Location(LocationManager.GPS_PROVIDER);

					loc.setLatitude(Double.valueOf(location.getString("lat")));
					loc.setLongitude(Double.valueOf(location.getString("lng")));

					venue.setLocation(loc);
					venueList.add(venue);
				}
			}
		} catch (Exception ex) {
			Log.i(TAG,ex.toString());
			throw ex;
		}
		
		return venueList;
	}
	private String streamToString(InputStream is) throws IOException {
		String str  = "";
		
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;
			
			try {
				BufferedReader reader 	= new BufferedReader(new InputStreamReader(is));
				
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				
				reader.close();
			} finally {
				is.close();
			}
			
			str = sb.toString();
		}
		
		return str;
	}
	
	private String timeMilisToString(long milis) {
		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar   = Calendar.getInstance();
		
		calendar.setTimeInMillis(milis);
		
		return sd.format(calendar.getTime());
	}
	public interface FsqAuthListener {
		public abstract void onSuccess();
		public abstract void onFail(String error);
	}
}