package com.nearblyapp.nearbly.grades;

import android.location.Location;
import android.location.LocationManager;
import android.os.Parcel;
import android.os.Parcelable;

import com.nearblyapp.nearbly.place.Place;

import java.util.ArrayList;

public class LocationsList extends ArrayList<Place> implements Parcelable {
    private static final long serialVersionUID = 663585476779879096L;

    public LocationsList(){

    }

    public LocationsList(Parcel in){

        readFromParcel(in);

    }

    @SuppressWarnings("unchecked")

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public LocationsList createFromParcel(Parcel in) {

            return new LocationsList(in);

        }

        public Object[] newArray(int arg0) {

            return null;

        }

    };

    private void readFromParcel(Parcel in) {

        this.clear();

        int size = in.readInt();

        for (int i = 0; i < size; i++) {

            Place venue = new Place();


            venue.setId(in.readString());
            venue.setName(in.readString());
            venue.setLatitude(in.readDouble());
            venue.setLongitude(in.readDouble());


            this.add(venue);

        }
    }

    public int describeContents() {

        return 0;

    }

    public void writeToParcel(Parcel dest, int flags) {

        int size = this.size();

        dest.writeInt(size);

        for (int i = 0; i < size; i++) {

            Place venue = this.get(i);

            dest.writeString(venue.getId());
            dest.writeString(venue.getName());
            dest.writeDouble(venue.getLatitude());
            dest.writeDouble(venue.getLongitude());

        }

    }

}
