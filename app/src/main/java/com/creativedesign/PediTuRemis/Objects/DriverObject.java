package com.creativedesign.PediTuRemis.Objects;

import com.google.firebase.database.DataSnapshot;

public class DriverObject{


    private String  id = "",
                    name = "",
                    phone = "",
                    car = "--",
                    profileImage = "default",
                    service;

    private float ratingsAvg = 0;

    private LocationObject mLocation;

    private Boolean active = true;

    public DriverObject(String id) {
        this.id = id;
    }
    public DriverObject() {}


    /**
     * Parse datasnapshot into this object
     * @param dataSnapshot - customer info fetched from the database
     */
    public void parseData(DataSnapshot dataSnapshot){

        id = dataSnapshot.getKey();

        if(dataSnapshot.child("name").getValue()!=null){
            name = dataSnapshot.child("name").getValue().toString();
        }
        if(dataSnapshot.child("phone").getValue()!=null){
            phone = dataSnapshot.child("phone").getValue().toString();
        }
        if(dataSnapshot.child("car").getValue()!=null){
            car = dataSnapshot.child("car").getValue().toString();
        }
        if(dataSnapshot.child("profileImageUrl").getValue()!=null){
            profileImage = dataSnapshot.child("profileImageUrl").getValue().toString();
        }
        if (dataSnapshot.child("activated").getValue() != null) {
            active = Boolean.parseBoolean(dataSnapshot.child("activated").getValue().toString());
        }
        if (dataSnapshot.child("service").getValue() != null) {
            service = dataSnapshot.child("service").getValue().toString();
        }
        int ratingSum = 0;
        float ratingsTotal = 0;
        for (DataSnapshot child : dataSnapshot.child("rating").getChildren()){
            ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
            ratingsTotal++;
        }
        if(ratingsTotal!= 0){
            ratingsAvg = ratingSum/ratingsTotal;
        }
    }


    public String getService() {
        return service;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public float getRatingsAvg() {
        return ratingsAvg;
    }

    public void setRatingsAvg(float ratingsAvg) {
        this.ratingsAvg = ratingsAvg;
    }

    public LocationObject getLocation() {
        return mLocation;
    }

    public void setLocation(LocationObject mLocation) {
        this.mLocation = mLocation;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }
}
