package com.creativedesign.PediTuRemis.Objects;

import android.graphics.drawable.Drawable;


/**
 * Type of car object, contains the info of each type of car
 * to be used by the type adapter.
 */
public class TypeObject {

    String name, id;
    Drawable image;
    int people;

    public TypeObject(String id, String name, Drawable image, int people){
        this.id = id;
        this.name = name;
        this.image = image;
        this.people = people;
    }

    public String getId() {
        return id;
    }

    public Drawable getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public int getPeople() {
        return people;
    }
}
