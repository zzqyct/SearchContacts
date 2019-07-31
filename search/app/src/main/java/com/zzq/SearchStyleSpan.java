package com.zzq;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.StyleSpan;

/**
 * Created by TitleZWC on 2017/3/26.
 */

@SuppressLint("ParcelCreator")
public class SearchStyleSpan extends StyleSpan {
    public SearchStyleSpan(int style) {
        super(style);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setFakeBoldText(true);
        ds.setTextSize(40);
        ds.setColor(Color.BLUE);
        //FIXME 这里还可以做其他差异性设置（修改文字大小等）
        super.updateDrawState(ds);
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        paint.setFakeBoldText(true);
        super.updateMeasureState(paint);
    }
}
