package com.zzq.cn;

import java.io.Serializable;

/**
 * Author：zzq
 * Date:2019/6/18
 * Des:Convert to full pinyin
 */

public class CNPinyinIndex<T extends CN> implements Serializable {

    public final CNPinyin<T> cnPinyin;



    public NamePhoneIndex mIndexLocation;

    CNPinyinIndex(CNPinyin cnPinyin, NamePhoneIndex indexLocation) {
        this.cnPinyin = cnPinyin;
        this.mIndexLocation = indexLocation;
    }


}
