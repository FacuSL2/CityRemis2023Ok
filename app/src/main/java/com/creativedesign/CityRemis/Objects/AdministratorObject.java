package com.creativedesign.CityRemis.Objects;

import com.google.firebase.database.DataSnapshot;

public class AdministratorObject {

    private String  id = "",
            name = "",
            phone = "",
            profileImage = "default";

    private LocationObject mLocation;

    private Boolean active = true;

    public AdministratorObject(String id) {
        this.id = id;
    }
    public AdministratorObject() {}


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
        if(dataSnapshot.child("profileImageUrl").getValue()!=null){
            profileImage = dataSnapshot.child("profileImageUrl").getValue().toString();
        }
        if (dataSnapshot.child("activated").getValue() != null) {
            active = Boolean.parseBoolean(dataSnapshot.child("activated").getValue().toString());
        }
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

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
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
