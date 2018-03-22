package org.leave.framework.util;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {
    /**
     * 字符串分隔符
     */
    public static final String SEPARATOR = String.valueOf((char)29);


    public static boolean isEmpty(String str){
        if(str != null){
            str = str.trim();
        }
        return StringUtils.isEmpty(str);
    }
    public static boolean isNotEmpay(String str){
        return !isEmpty(str);
    }

    public static String[] splitString(String str, String separatorChars){
        return StringUtils.split(str, separatorChars);
    }
}
