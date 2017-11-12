package com.chan.kingdom3;

import android.graphics.Bitmap;

import org.litepal.crud.DataSupport;

/**
 * Created by 61915 on 17/11/12.
 */

public class character extends DataSupport {
    private int id;
    private Bitmap image;
    private String Name;
    private String gender;
    private String Kingdom;
    private String native_place;
    private String birth;
    private String death;

    public int getId() {
        return id;
    }

    public Bitmap getImage() {

        return image;
    }

    public String getName() {
        return Name;
    }

    public String getGender() {
        return gender;
    }

    public String getKingdom() {
        return Kingdom;
    }

    public String getNative_place() {
        return native_place;
    }

    public String getBirth() {
        return birth;
    }

    public String getDeath() {
        return death;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setKingdom(String kingdom) {
        Kingdom = kingdom;
    }

    public void setNative_place(String native_place) {
        this.native_place = native_place;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public void setDeath(String death) {
        this.death = death;
    }
}
