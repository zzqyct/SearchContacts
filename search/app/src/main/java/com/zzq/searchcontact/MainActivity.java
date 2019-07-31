package com.zzq.searchcontact;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zzq.adapter.ContactsAdapter;
import com.zzq.cn.CNPinyin;
import com.zzq.cn.CNPinyinFactory;
import com.zzq.cn.CNPinyinIndex;
import com.zzq.cn.CNPinyinIndexFactory;
import com.zzq.cn.Contact;
import com.zzq.constant.Constant;
import com.zzq.stickyheader.StickyHeaderDecoration;
import com.zzq.view.LettersView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements TextWatcher, LettersView.WordsChangeListener {
    private EditText mEditText;
    private RecyclerView mRecyclerView;
    private ContactsAdapter mContactsAdapter;
    private LettersView mLettersView;
    private TextView mTvLetter;
    private ArrayList<CNPinyin<Contact>> mContactList;
    private StickyHeaderDecoration mHeaderDecoration;
    private LinearLayoutManager mManager;
    private MyHandler mHandler;
    private static final int UPDATE_SEARCH = 0;
    private static final int UPDATE_CONTACT = 1;
    private ExecutorService mCachedThreadPool;
    private int mThreadFlag;

    private static class MyHandler extends Handler {
        WeakReference<MainActivity> mWeakReference;

        public MyHandler(MainActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mWeakReference.get();
            switch (msg.what) {
                case UPDATE_SEARCH:
                    if (msg.arg1 == activity.mThreadFlag) {
                        ArrayList<CNPinyinIndex<Contact>> cnPinyinIndices = (ArrayList<CNPinyinIndex<Contact>>) msg.obj;
                        activity.mContactsAdapter.setIndexData(true, cnPinyinIndices);
                        activity.mLettersView.setVisibility(View.GONE);
                        activity.mRecyclerView.removeItemDecoration(activity.mHeaderDecoration);
                    }
                    break;
                case UPDATE_CONTACT:
                    if (msg.arg1 == activity.mThreadFlag) {
                        activity.mRecyclerView.addItemDecoration(activity.mHeaderDecoration);
                        activity.mContactsAdapter.setData(false, activity.mContactList);
                        activity.mLettersView.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new MyHandler(this);
        mCachedThreadPool = Executors.newCachedThreadPool();
        initView();
        initData();
    }

    private void initData() {
        List<Contact> contacts = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            String number = "1";
            String name;
            for (int j = 0; j < 10; j++) {
                number = number + new Random().nextInt(10);
            }
            name = Constant.familyNames[new Random().nextInt(Constant.familyNames.length - 1)] + Constant.names[new Random().nextInt(Constant.names.length - 1)];
            contacts.add(new Contact(name, number));
        }
        mContactList = CNPinyinFactory.createCNPinyinList(contacts);
        Collections.sort(mContactList);
        mContactsAdapter.setData(false, mContactList);
        mEditText.setHint("搜索" + mContactList.size() + "位联系人");
    }

    private void initView() {
        mEditText = findViewById(R.id.edit_text);
        mEditText.addTextChangedListener(this);
        mTvLetter = findViewById(R.id.tv_letter);
        mLettersView = findViewById(R.id.letters_view);
        mLettersView.setOnWordsChangeListener(this);
        mRecyclerView = findViewById(R.id.recycle_view);
        mManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mManager);
        mContactList = new ArrayList<>();
        mContactsAdapter = new ContactsAdapter(this, mContactList);
        mRecyclerView.setAdapter(mContactsAdapter);
        mHeaderDecoration = new StickyHeaderDecoration(mContactsAdapter);
        mRecyclerView.addItemDecoration(mHeaderDecoration);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItemPosition = mManager.findFirstVisibleItemPosition();
                if (!mContactsAdapter.mIsSearch) {
                    mLettersView.setScrollIndex(String.valueOf(mContactList.get(firstVisibleItemPosition).getFirstChar()));
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(final CharSequence s, int start, int before, int count) {
        mThreadFlag = new Random().nextInt(100000);
        final int flag = mThreadFlag;
        Log.e("hello", "mThreadFlag" + mThreadFlag);
        mCachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (s.toString().length() == 0) {
                    Message message = mHandler.obtainMessage(UPDATE_CONTACT);
                    message.arg1 = flag;
                    Log.e("hello", "message.arg1" + message.arg1);
                    mHandler.sendMessage(message);
                } else {
                    ArrayList<CNPinyinIndex<Contact>> cnPinyinIndices = new ArrayList<>();
                    if (mContactList != null) {
                        cnPinyinIndices = CNPinyinIndexFactory.indexList(mContactList, s.toString().trim());
                    }
                    mHandler.removeMessages(UPDATE_SEARCH);
                    Message message = mHandler.obtainMessage(UPDATE_SEARCH, cnPinyinIndices);
                    message.arg1 = flag;
                    Log.e("hello", "message" + message.arg1);
                    mHandler.sendMessage(message);
                }
            }
        });


    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void updateWordsChange(String letter, int index) {
        mTvLetter.setVisibility(View.VISIBLE);
        mTvLetter.setText(letter);
        for (int i = 0; i < mContactList.size(); i++) {
            if (String.valueOf(mContactList.get(i).getFirstChar()).equals(letter)) {
                mManager.scrollToPositionWithOffset(i, 0);
                mLettersView.mSelectIndex = index;
                return;
            }
        }
    }

    @Override
    public void actionUpEvent(String s) {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTvLetter.setVisibility(View.GONE);
            }
        }, 1000);
    }
}
