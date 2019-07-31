package com.zzq.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zzq.SearchStyleSpan;
import com.zzq.cn.CNPinyin;
import com.zzq.cn.CNPinyinIndex;
import com.zzq.cn.Contact;
import com.zzq.cn.IndexLocation;
import com.zzq.searchcontact.R;
import com.zzq.stickyheader.StickyHeaderAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：zzq
 * Date:2019/7/16
 * Des:
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> implements StickyHeaderAdapter<ContactsAdapter.HeadHolder> {
    private Context mContext;
    private ArrayList<CNPinyin<Contact>> mList;
    private List<CNPinyinIndex<Contact>> mIndexList;
    public boolean mIsSearch = false;

    public ContactsAdapter(Context context, ArrayList<CNPinyin<Contact>> list) {
        this.mContext = context;
        this.mList = list;
    }

    public void setData(boolean isSearch, ArrayList<CNPinyin<Contact>> cnPinyinList) {
        this.mIsSearch = isSearch;
        this.mList = cnPinyinList;
        this.notifyDataSetChanged();
    }

    public void setIndexData(boolean isSearch, List<CNPinyinIndex<Contact>> cnPinyinList) {
        this.mIsSearch = isSearch;
        this.mIndexList = cnPinyinList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new MyViewHolder(view);
    }

    ForegroundColorSpan nameColorSpan = new ForegroundColorSpan(Color.BLUE);
    //    //文本字体绝对的大小
//    AbsoluteSizeSpan nameSizeSpan = new AbsoluteSizeSpan(20, true);
//    StyleSpan nameBoldSpan = new StyleSpan(Typeface.BOLD);
    SearchStyleSpan mStyleSpan = new SearchStyleSpan(Typeface.NORMAL);

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        if (!mIsSearch) {
            myViewHolder.mTvName.setText(mList.get(position).data.getName());
            myViewHolder.mTvNumber.setText(mList.get(position).data.getNumber());
        } else {
            CNPinyinIndex<Contact> cnPinyinIndex = mIndexList.get(position);
            Contact contact = cnPinyinIndex.cnPinyin.data;
            SpannableStringBuilder ssbName = new SpannableStringBuilder(contact.chinese());
            SpannableStringBuilder ssbPhone = new SpannableStringBuilder(contact.phone());
            if (cnPinyinIndex.mIndexLocation != null) {
                List<IndexLocation> nameLocations = cnPinyinIndex.mIndexLocation.getNameLocations();
                IndexLocation phoneLocation = cnPinyinIndex.mIndexLocation.getPhoneLocation();
                if (nameLocations != null) {
                    for (int i = 0; i < nameLocations.size(); i++) {
                        int nameStart = nameLocations.get(i).getStart();
                        int nameEnd = nameLocations.get(i).getEnd();
                        if (nameLocations.size() == 1) {
                            ssbName.setSpan(mStyleSpan, nameStart, nameEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            ssbName.setSpan(nameSizeSpan, nameStart, nameEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            ssbName.setSpan(nameBoldSpan, nameStart, nameEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } else {
//                            nameColorSpan = new ForegroundColorSpan(Color.BLUE);
//                            //文本字体绝对的大小
//                            nameSizeSpan = new AbsoluteSizeSpan(20, true);
//                            nameBoldSpan = new StyleSpan(Typeface.BOLD);
                            mStyleSpan = new SearchStyleSpan(Typeface.NORMAL);
                            ssbName.setSpan(mStyleSpan, nameStart, nameEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            ssbName.setSpan(nameSizeSpan, nameStart, nameEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            ssbName.setSpan(nameBoldSpan, nameStart, nameEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                }
                if (phoneLocation != null) {
                    if (phoneLocation.getEnd() > phoneLocation.getStart()) {
                        if (!phoneLocation.isFormat()) {
                            ssbPhone = new SpannableStringBuilder(contact.phone().replaceAll(" ", "").trim());
                        }
                        // ForegroundColorSpan span = new ForegroundColorSpan(Color.BLUE);
                        ssbPhone.setSpan(nameColorSpan, phoneLocation.getStart(), phoneLocation.getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
            myViewHolder.mTvName.setText(ssbName);
            myViewHolder.mTvNumber.setText(ssbPhone);
        }
    }

    @Override
    public int getItemCount() {
        return mIsSearch ? (mIndexList == null ? 0 : mIndexList.size()) :
                (mList == null ? 0 : mList.size());
    }

    @Override
    public long getHeaderId(int childAdapterPosition) {
        return mList.get(childAdapterPosition).getFirstChar();
    }

    @Override
    public HeadHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new HeadHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_header, parent, false));
    }

    @Override
    public void onBindHeaderViewHolder(HeadHolder holder, int childAdapterPosition) {
        holder.mTvHeader.setText("" + String.valueOf(mList.get(childAdapterPosition).getFirstChar()));
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvName;
        private TextView mTvNumber;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvNumber = itemView.findViewById(R.id.tv_number);
        }
    }

    public class HeadHolder extends RecyclerView.ViewHolder {
        private TextView mTvHeader;

        public HeadHolder(@NonNull View itemView) {
            super(itemView);
            mTvHeader = itemView.findViewById(R.id.tv_header);
        }
    }
}
