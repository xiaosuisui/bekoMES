package com.mj.beko.tcs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jc on 2017/8/3.
 */

public class StrPatternUtil {
    /**
     * 正则判断某个字符串出现的次数
     * @param sourceXml
     * @param regex
     * @return
     */
    public static int count(String sourceXml,Pattern regex){
        int count =0;
        Matcher matcher=regex.matcher(sourceXml);
        while (matcher.find()){
            count++;
        }
        return count;
    }
    /**
     * 正则查找某个字符串
     * @param sourceXml
     * @param regex
     * @return
     */
    public static String matchStr(String sourceXml,Pattern regex){
        String matchStr="";
        Matcher matcher=regex.matcher(sourceXml);
        while (matcher.find()){

            matchStr=matcher.group(1);
        }
        return matchStr;
    }
}
