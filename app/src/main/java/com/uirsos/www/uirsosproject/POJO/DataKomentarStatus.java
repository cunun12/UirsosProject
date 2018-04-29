package com.uirsos.www.uirsosproject.POJO;


/**
 * Created by cunun12 on 23/03/2018.
 */

public class DataKomentarStatus extends PostId {
    private String user_id;
    private String komentar;
    private String timestamp;


    public DataKomentarStatus() {
    }

    /**
     * Constructor
     */
    public DataKomentarStatus(String user_id, String komentar, String timestamp) {
        this.user_id = user_id;
        this.komentar = komentar;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "DataKomentarStatus{" +
                "PostId='" + PostId + '\'' +
                ", user_id='" + user_id + '\'' +
                ", komentar='" + komentar + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
