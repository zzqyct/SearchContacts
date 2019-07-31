package com.zzq.cn;

import java.util.List;

/**
 * Author：zzq
 * Date:2019/6/25
 * Des:name and phone index location
 */
public class NamePhoneIndex {

    private List<IndexLocation> mNameLocations;//name index collection
    private IndexLocation mPhoneLocation;//number index

    public List<IndexLocation> getNameLocations() {
        return mNameLocations;
    }

    public void setLocations(List<IndexLocation> locations) {
        mNameLocations = locations;
    }

    public IndexLocation getPhoneLocation() {
        return mPhoneLocation;
    }

    public void setPhoneLocation(IndexLocation phoneLocation) {
        this.mPhoneLocation = phoneLocation;
    }
}
