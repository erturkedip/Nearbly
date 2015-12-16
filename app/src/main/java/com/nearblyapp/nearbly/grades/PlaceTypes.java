package com.nearblyapp.nearbly.grades;

import com.nearblyapp.nearbly.R;

import java.util.HashMap;


public class PlaceTypes {

    private HashMap<String,String> placeTypesHash;
    private HashMap<String, Integer> placeIconHash;

    public PlaceTypes(){

        placeTypesHash = new HashMap<String, String>();
        placeTypesHash.put("accounting", "MUHASEBE");
        placeTypesHash.put("airport","HAVA ALANI");
        placeTypesHash.put("atm","BANKAMATİK");
        placeTypesHash.put("bakery","FIRIN");
        placeTypesHash.put("bank","BANKA");
        placeTypesHash.put("beauty_salon","GÜZELLİK SALONU");
        placeTypesHash.put("cafe","KAFE");
        placeTypesHash.put("campground","KAMP YERİ");
        placeTypesHash.put("car_dealer","OTOMOBİL BAYİİ");
        placeTypesHash.put("car_rental","KİRALIK OTOMOBİL");
        placeTypesHash.put("car_repair","OTO TAMİR");
        placeTypesHash.put("car_wash","OTO KUAFÖR");
        placeTypesHash.put("dentist","DİŞ HEKİMİ");
        placeTypesHash.put("gym","SPOR SALONU");
        placeTypesHash.put("train_station","TREN İSTASYONU");
        placeTypesHash.put("stadium","STADYUM");
        placeTypesHash.put("post_office","POSTAHANE");
        placeTypesHash.put("museum","MÜZE");
        placeTypesHash.put("mosque","CAMİİ");
        placeTypesHash.put("library","KÜTÜPHANE");
        placeTypesHash.put("hospital","HASTANE");

        placeIconHash = new HashMap<String, Integer>();

        placeIconHash.put("accounting", R.mipmap.ic_account);
        placeIconHash.put("airport", R.mipmap.ic_airport);
        placeIconHash.put("atm",R.mipmap.ic_bank);
        placeIconHash.put("bakery",R.mipmap.ic_kasap);
        placeIconHash.put("beauty_salon", R.mipmap.ic_beauty_sal);
        placeIconHash.put("cafe", R.mipmap.ic_cafe);
        placeIconHash.put("campground", R.mipmap.ic_campground);
        placeIconHash.put("car_dealer",R.mipmap.ic_car_dealer);
        placeIconHash.put("bank", R.mipmap.ic_bank);
        placeIconHash.put("car_rental", R.mipmap.ic_car_rental);
        placeIconHash.put("car_repair", R.mipmap.ic_car_repair);
        placeIconHash.put("car_wash", R.mipmap.ic_car_wash);
        placeIconHash.put("dentist", R.mipmap.ic_hospital);
        placeIconHash.put("gym", R.mipmap.ic_gym);
        placeIconHash.put("train_station", R.mipmap.ic_train_stat);
        placeIconHash.put("stadium", R.mipmap.ic_stadium);
        placeIconHash.put("post_office", R.mipmap.ic_post_office);
        placeIconHash.put("museum", R.mipmap.ic_museum);
        placeIconHash.put("mosque", R.mipmap.ic_mosque);
        placeIconHash.put("library", R.mipmap.ic_library);
        placeIconHash.put("hospital", R.mipmap.ic_hospital);



    }

    public HashMap<String, String> getPlaceTypesHash(){
        return this.placeTypesHash;
    }

    public HashMap<String, Integer> getPlaceIconHash(){
        return this.placeIconHash;
    }

}
