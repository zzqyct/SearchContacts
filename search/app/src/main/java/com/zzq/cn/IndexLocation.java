package com.zzq.cn;

/**
 * Author：zzq
 * Date:2019/6/19
 * Des:name index location
 */
public class IndexLocation {
    private int start;
    private int end;


    private boolean isFormat;


    public IndexLocation(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public IndexLocation(int start, int end, boolean isFormat) {
        this.start = start;
        this.end = end;
        this.isFormat = isFormat;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public boolean isFormat() {
        return isFormat;
    }

    public void setFormat(boolean format) {
        isFormat = format;
    }
}
