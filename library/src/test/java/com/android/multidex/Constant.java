package com.android.multidex;

import java.io.File;

/**
 * @version V1.0
 * @author: lizhangqu
 * @date: 2016-08-07 17:22
 */
public class Constant {
    public static final File TEST_JAR = new File("/Users/lizhangqu/AndroidStudioProjects/MainDex/files/combined.jar");
    public static final File DEST_PATCH = new File("/Users/lizhangqu/AndroidStudioProjects/MainDex/files/patch.jar");
    public static final String TEST_CLASS = "a/b/c/d/e.class";
    public static final String PATCH_CLASS = "a/b/e/a/d.class";
}
