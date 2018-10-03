package com.example.micha.newsapp;

import android.graphics.Bitmap;

public class News {
    private String mSectionName;
    private String mPubDate;
    private String mWebTitle;
    private String mUrl;
    private String mAuthorName;
    private Bitmap mThumbnail;

    public News(String section, String date, String title, String url, String authorName, Bitmap thumbnail) {
        mSectionName = section;
        mPubDate = date;
        mWebTitle = title;
        mUrl = url;
        mAuthorName = authorName;
        mThumbnail = thumbnail;
    }

    public String getSection() {
        return mSectionName;
    }
    public String getDate() {
        return mPubDate;
    }
    public String getTitle() {
        return mWebTitle;
    }
    public String getUrl() {
        return mUrl;
    }
    public String getAuthorName() {
        return mAuthorName;
    }
    public Bitmap getThumbnail(){return mThumbnail;}
}