package com.mdy.android.dailythreethanks.domain;

/**
 * Created by MDY on 2017-07-06.
 */

public class Memo {
    public String title1;
    public String title2;
    public String title3;

    public String content1;
    public String content2;
    public String content3;

    public String fileUriString;
    public String date;

    public Memo(){

    }

    public Memo(String title1, String title2, String title3, String content1, String content2, String content3){
        this.title1 = title1;
        this.title2 = title2;
        this.title3 = title3;
        this.content1 = content1;
        this.content2 = content2;
        this.content3 = content3;
    }
}
