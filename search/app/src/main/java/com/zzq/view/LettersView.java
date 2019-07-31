package com.zzq.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Author：zzq
 * Date:2019/7/16
 * Des:字母列表
 */
public class LettersView extends View {
    private static final String[] LETTERS = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};
    private Paint mBgPaint;
    private Paint mTextPaint;
    private float mItemHeight;
    private float mItemWidth;
    private float mTextY;
    public int mSlideIndex = -1;
    public int mSelectIndex = -1;
    private float mTextSize = 20.f;
    private WordsChangeListener mWordsChangeListener;

    public LettersView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LettersView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBgPaint = new Paint();
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(Color.parseColor("#ff8b3c"));
        mTextPaint.setTextSize(mTextSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //计算单个条目宽度和高度
        mItemWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mItemHeight = (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) / (float) LETTERS.length;
        Log.e("test", "getMeasuredHeight():" + getMeasuredHeight() + "mItemHeight:" + mItemHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        float textHeight = fm.bottom - fm.top;
//        int height = getHeight() - getPaddingTop() - getPaddingBottom();
//        mItemHeight = height / (float) LETTERS.length;
        mTextY = mItemHeight - (mItemHeight - textHeight) / 2 - fm.bottom;
        float centerX = getPaddingLeft() + (getWidth() - getPaddingLeft() - getPaddingRight()) / 2.0f;
        float centerY = getPaddingTop() + mTextY;
        if (centerX <= 0 || centerY <= 0) return;
        for (int i = 0; i < LETTERS.length; i++) {
//            if (i % 2 == 0) {
//                Rect rect = new Rect();
//                rect.left = 0;
//                rect.right = (int) mItemWidth;
//                rect.top = (int) (i * mItemHeight);
//                rect.bottom = (int) ((i + 1) * mItemHeight);
//                canvas.drawRect(rect, mBgPaint);
//            }
            if (i == mSlideIndex) {
//                canvas.drawCircle(mItemWidth / 2, mItemHeight / 2 + mItemHeight * i, 12, mBgPaint);
                mTextPaint.setColor(Color.RED);
            } else {
                mTextPaint.setColor(Color.GRAY);
            }
            Rect rect = new Rect();
            mTextPaint.getTextBounds(LETTERS[i], 0, 1, rect);
            int wordWidth = rect.width();
            //绘制字母
            float wordX = mItemWidth / 2 - wordWidth / 2;
            canvas.drawText(LETTERS[i], wordX, centerY, mTextPaint);
            centerY += mItemHeight;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                //执行滑动操作
                int currentY = (int) (event.getY() / mItemHeight);
                if (currentY < 0) {
                    currentY = 0;
                }
                if (currentY > LETTERS.length - 1) {
                    currentY = LETTERS.length - 1;
                }
                if (mSlideIndex != currentY) {
                    mSlideIndex = currentY;
                    if (mWordsChangeListener != null) {
                        mWordsChangeListener.updateWordsChange(LETTERS[mSlideIndex], mSlideIndex);
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mSlideIndex != mSelectIndex) {
                    mSlideIndex = mSelectIndex;
                    invalidate();
                }
                if (mWordsChangeListener != null) {
                    mWordsChangeListener.actionUpEvent(LETTERS[mSlideIndex]);
                }
                break;
        }
        return true;
    }


    public interface WordsChangeListener {
        void updateWordsChange(String s, int index);

        void actionUpEvent(String s);
    }


    public void setOnWordsChangeListener(WordsChangeListener listener) {
        this.mWordsChangeListener = listener;
    }

    public void setScrollIndex(String s) {
        for (int i = 0; i < LETTERS.length - 1; i++) {
            if (s.equals(LETTERS[i])) {
                mSlideIndex = i;
                mSelectIndex = i;
                invalidate();
                return;
            }
        }
    }
}
