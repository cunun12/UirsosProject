package com.uirsos.www.uirsosproject.POJO;

/**
 * Created by cunun12 on 21/04/2018.
 */

public class User {

    public String image, name;

    public User(){

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User(String image, String name) {
        this.image = image;
        this.name = name;
    }
}
