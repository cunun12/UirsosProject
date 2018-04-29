package com.uirsos.www.uirsosproject.POJO;

/**
 * Created by cunun12
 */

public class DataItemStatus extends PostId{

    private String user_id, image_post, desc, image_thumb;
    private String timestamp;

    public DataItemStatus() {}

    public DataItemStatus(String user_id, String image_post, String desc, String image_thumb, String timestamp) {
        this.user_id = user_id;
        this.image_post = image_post;
        this.desc = desc;
        this.image_thumb = image_thumb;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_post() {
        return image_post;
    }

    public void setImage_post(String image_post) {
        this.image_post = image_post;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "DataItemStatus{" +
                "user_id='" + user_id + '\'' +
                ", image_post='" + image_post + '\'' +
                ", desc='" + desc + '\'' +
                ", image_thumb='" + image_thumb + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", PostId='" + PostId + '\'' +
                '}';
    }
}
