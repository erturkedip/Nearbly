package com.nearblyapp.nearbly.foursquare;

import android.location.Location;

public class FoursquareVenue {

	private String id;
	private String name;
	private Location location;

	public FoursquareVenue(){

	}

	public FoursquareVenue(String id, String name, Location location){
		this.id = id;
		this.name = name;
		this.location = location;
	}

	public void setId(String id){
		this.id = id;
	}

	public void setName(String name){
		this.name = name;
	}

	public void setLocation(Location location){

		this.location = location;
	}

	public Location getLocation(){
		return location;
	}

	public String getName(){
		return name;
	}

	public String getId(){
		return id;
	}
}