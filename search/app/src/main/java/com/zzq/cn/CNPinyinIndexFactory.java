package com.zzq.cn;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author：zzq
 * Date:2019/6/15
 * Des:Gets the horn of the keyword AND
 * Improve performance and efficiency
 */

public final class CNPinyinIndexFactory {
    private static final String TAG = "CNPinyinIndexFactory";
    // private static final String regex = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]";
    //  String regex = "\\p{P}";
    private static final String regex = "(?i)[^a-zA-Z0-9\\u4E00-\\u9FA5]";
    /*
     * Create a Pattern only once,Improve performance and efficiency
     */
    private static Pattern mPatternChinese = Pattern.compile("[\u4e00-\u9fa50-9]");
    private static Pattern mPatternNumber = Pattern.compile("[0-9]*");
    private static Pattern mPatternRegex = Pattern.compile(regex);

    /**
     * 转换搜索拼音集合, 考虑在子线程中运行
     *
     * @param cnPinyinList
     * @param keyword
     * @return
     */
    public static <T extends CN> ArrayList<CNPinyinIndex<T>> indexList(List<CNPinyin<T>> cnPinyinList, String keyword) {
        ArrayList<CNPinyinIndex<T>> cnPinyinIndexArrayList = new ArrayList<>();
        Pattern pattern_keyword = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
        long time = System.currentTimeMillis();
        boolean isNumber = isNumeric(keyword);
        String[] strs;
//        if (keyword.length() > 1) {
        strs = mPatternRegex.split(keyword);
//        } else {
//            strs = new String[]{keyword};
        boolean isSingleLength = false;
        if (strs.length == 1) {
            isSingleLength = true;
        }
        for (CNPinyin<T> cnPinyin : cnPinyinList) {
            CNPinyinIndex<T> index = matcherSplitBlank(cnPinyin, keyword, pattern_keyword, isNumber, strs, isSingleLength);
            if (index == null) {//把全部搜索到的都添加进来
                //index = new CNPinyinIndex<>(cnPinyin, null);
            }
            if (index != null) {
                cnPinyinIndexArrayList.add(index);
            }
        }
        return cnPinyinIndexArrayList;
    }

    private static boolean isContainChinese(String str) {
        Matcher m = mPatternChinese.matcher(str);
        return m.find();
    }

    private static boolean isNumeric(String str) {
        Matcher isNum = mPatternNumber.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 按空格和标点符号分割后查找
     *
     * @param cnPinyin
     * @param keyword
     * @return
     */
    public static CNPinyinIndex matcherSplitBlank(CNPinyin cnPinyin, String keyword, Pattern pattern_keyword, boolean isNumber, String[] strs, boolean isSingleLength) {
        if (TextUtils.isEmpty(keyword)) return null;
        IndexLocation phoneLocation = null;
        if (isNumber) {
            phoneLocation = matcherPhoneNumber(cnPinyin, keyword, pattern_keyword);
        }
        List<IndexLocation> indexList = new ArrayList<>();
        for (int i = 0; i < strs.length; i++) {
            IndexLocation index;
            if (strs[i].length() <= cnPinyin.pinyinsTotalLength) {
                if (isNumber || isNumeric(strs[i])) {//只有数字
                    if (!cnPinyin.data.chinese().contains(strs[i])) {
                        indexList.clear();
                        break;
                    }
                }
                //matcher chinese
                if (isContainChinese(strs[i])) {//包含中文和数字只匹配原字符
                    index = matcherChinese(cnPinyin, strs[i]);
                    if (index == null) {
                        indexList.clear();
                        break;
                    }
                    indexList.add(index);
                    continue;
                }
                //matcher first
                index = matcherFirst(cnPinyin, strs[i], pattern_keyword, isSingleLength);
                if (index != null) {
                    indexList.add(index);
                    continue;
                }
                //matcher pinyin
                index = matcherPinYins(cnPinyin, strs[i]);
                if (index == null) {
                    indexList.clear();
                    break;
                }
                indexList.add(index);
            }
        }
        if (indexList.size() == 0 && phoneLocation == null) {
            return null;
        }
        NamePhoneIndex indexLocation = new NamePhoneIndex();
        indexLocation.setLocations(indexList);
        indexLocation.setPhoneLocation(phoneLocation);
        return new CNPinyinIndex(cnPinyin, indexLocation);
    }

    /**
     * 匹配中文
     *
     * @param cnPinyin
     * @param keyword
     * @return
     */
    static IndexLocation matcherChinese(CNPinyin cnPinyin, String keyword) {
        int name_start = 0;
        int name_end = 0;
        if (keyword.length() <= cnPinyin.data.chinese().length()) {
            Matcher matcher = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE).matcher(cnPinyin.data.chinese());
            if (matcher.find()) {
                name_start = matcher.start();
                name_end = matcher.end();
            }
        }

        if (name_end > 0) {
            IndexLocation indexLocation = new IndexLocation(name_start, name_end);
            return indexLocation;
        }
        return null;
    }

    /**
     * 匹配首字母
     *
     * @param cnPinyin
     * @param keyword
     * @return
     */
    static IndexLocation matcherFirst(CNPinyin cnPinyin, String keyword, Pattern pattern_keyword, boolean isSingleLength) {
        if (keyword.length() <= cnPinyin.firstChars.length()) {
            Matcher matcher;
            if (isSingleLength) {
                matcher = pattern_keyword.matcher(cnPinyin.firstChars);
            } else {
                matcher = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE).matcher(cnPinyin.firstChars);
            }
            if (matcher.find()) {
                IndexLocation indexLocation = new IndexLocation(matcher.start(), matcher.end());
                return indexLocation;
            }
        }
        return null;
    }

    /**
     * 所有拼音匹配
     *
     * @param cnPinyin
     * @param keyword
     * @return
     */
    static IndexLocation matcherPinYins(CNPinyin cnPinyin, String keyword) {
        if (keyword.length() > cnPinyin.pinyinsTotalLength) return null;
        int start = -1;
        int end = -1;
        for (int i = 0; i < cnPinyin.pinyins.length; i++) {
            String pat = cnPinyin.pinyins[i];
            if (pat.length() >= keyword.length()) {//首个位置索引
                Matcher matcher = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE).matcher(pat);
                if (matcher.find() && matcher.start() == 0) {
                    start = i;
                    end = i + 1;
                    break;
                }
            } else {
                String patten = Pattern.quote("" + pat);//转换为普通字符串，不被当成做正则表达式(防止姓名为"()")
                Matcher matcher = Pattern.compile(patten, Pattern.CASE_INSENSITIVE).matcher(keyword);
                if (matcher.find() && matcher.start() == 0) {//全拼匹配第一个必须在0位置
                    start = i;
                    String left = matcher.replaceFirst("");
                    end = end(cnPinyin.pinyins, left, ++i);
                    break;
                }
            }
        }
        if (start >= 0 && end >= start) {
            IndexLocation index = new IndexLocation(start, end);
            return index;
        }
        return null;
    }

    /**
     * 根据匹配字符递归查找下一结束位置
     *
     * @param pinyinGroup
     * @param pattern
     * @param index
     * @return -1 匹配失败
     */
    private static int end(String[] pinyinGroup, String pattern, int index) {
        if (index < pinyinGroup.length) {
            String pinyin = pinyinGroup[index];
            if (pinyin.length() >= pattern.length()) {//首个位置索引
                Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(pinyin);
                if (matcher.find() && matcher.start() == 0) {
                    return index + 1;
                }
            } else {
                Matcher matcher = Pattern.compile(pinyin, Pattern.CASE_INSENSITIVE).matcher(pattern);
                if (matcher.find() && matcher.start() == 0) {//全拼匹配第一个必须在0位置
                    String left = matcher.replaceFirst("");
                    return end(pinyinGroup, left, index + 1);
                }
            }
        }
        return -1;
    }


    public static IndexLocation matcherPhoneNumber(CNPinyin cnPinyin, String keyword, Pattern pattern_keyword) {
        if (keyword.length() <= cnPinyin.data.phone().length()) {
            Matcher matcher = pattern_keyword.matcher(cnPinyin.data.phone());
            if (matcher.find()) {
                return new IndexLocation(matcher.start(), matcher.end(), true);
            }
            Matcher matcher2 = pattern_keyword.matcher(cnPinyin.data.phone().replaceAll(" ", ""));
            if (matcher2.find()) {
                return new IndexLocation(matcher2.start(), matcher2.end(), false);
            }
        }
        return null;
    }
}
