package com.noise.android.talkingthing;

/**
 * Created by Sourabh on 08/03/2018.
 */

public class Messages {

    private String message, from;
    private long timestamp;

    public Messages(String message,long timestamp,String from){
        this.message = message;
        this.timestamp = timestamp;
        this.from = from;
    }

    public Messages(){

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
