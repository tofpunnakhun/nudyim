package com.ayp.nudyim.budget;

/**
 * Created by Punnakhun on 10/18/2016.
 */

public class Expend {
    private String detail;
    private int expend;
    public Expend(){

    }

    public Expend(String detail, int expend){
        this.detail = detail;
        this.expend = expend;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getExpend() {
        return expend;
    }

    public void setExpend(int expend) {
        this.expend = expend;
    }
}
