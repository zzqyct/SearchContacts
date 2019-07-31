package com.zzq.cn;

/**
 * Author：zzq
 * Date:2019/6/3
 * Des:
 */
public class Contact implements CN {
    public String name;
    public String number;
    public String label;

    public Contact(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String chinese() {
        return name;
    }

    @Override
    public String phone() {
        return number;
    }
}
