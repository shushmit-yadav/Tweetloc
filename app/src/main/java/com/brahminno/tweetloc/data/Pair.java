package com.brahminno.tweetloc.data;

/**
 * Created by Shushmit on 30-05-2015.
 */
public class Pair {
    public String key;
    public String value;
    public Pair(String key,String value){
        this.key = key;
        this.value = value;
    }
    public String toString() {
        return "{" +  key + ", " + value + "}";
    }
}
