package com.example.administrator.litepaltest;

import org.litepal.crud.DataSupport;

/**
 * 简介类
 */
public class Introduction extends DataSupport{
    private int id;

    private String guide;

    private String digest;

    public String getGuide() {
        return guide;
    }

    public void setGuide(String guide) {
        this.guide = guide;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public int getId() {
        return id;

    }

    public void setId(int id) {
        this.id = id;
    }
}
