package com.ayp.nudyim.budget;

/**
 * Created by Punnakhun on 10/18/2016.
 */

public class Income {
    private String detail;
    private int income;
    public Income(){

    }

    public Income(String detail, int income){
        this.detail = detail;
        this.income = income;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }
}
