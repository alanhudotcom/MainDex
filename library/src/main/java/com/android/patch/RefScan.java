package com.android.patch;

import java.util.Set;

/**
 * @version V1.0
 * @author: lizhangqu
 * @date: 2016-08-07 18:04
 */
public class RefScan {
    /**
     * 判断className是否调用了补丁,如果是返回true
     *
     * @param className    调用类
     * @param allref       调用类的依赖集
     * @param patchClasses 所有补丁类
     */
    public static boolean hasCallPatch(String className, Set<String> allref, Set<String> patchClasses) {
        if (allref == null || allref.size() == 0) {
            return false;
        }
        for (String ref : allref) {
            if (patchClasses.contains(ref)) {
                //该类如果引用了补丁
                return true;
            }
        }
        return false;
    }
}
