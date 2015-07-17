package com.brahminno.tweetloc;

/**
 * Created by Shushmit on 11-07-2015.
 */
public class Message {
    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    private String fromName;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    public boolean isSelf() {
        return isSelf;
    }

    public void setIsSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }

    private boolean isSelf;
    public Message(){

    }
    public Message(String fromName,String message,boolean isSelf){
        this.fromName = fromName;
        this.message = message;
        this.isSelf = isSelf;
    }
}
