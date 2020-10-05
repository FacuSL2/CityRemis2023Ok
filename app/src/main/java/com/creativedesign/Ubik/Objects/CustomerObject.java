package com.creativedesign.Ubik.Objects;

import com.google.firebase.database.DataSnapshot;


/**
 * Customer object, it contains all the relevant info of the customer user
 */
public class CustomerObject{


    private String  id = "";
    private String name = "";
    private String phone = "";
    private String email = "";
    private String profileImage = "default";

    public CustomerObject(String id) {
        this.id = id;
    }

    public CustomerObject() {}


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
        if(dataSnapshot.child("email").getValue()!=null){
            setEmail(dataSnapshot.child("email").getValue().toString());
        }
        if(dataSnapshot.child("profileImageUrl").getValue()!=null){
            profileImage = dataSnapshot.child("profileImageUrl").getValue().toString();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
