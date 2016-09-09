package com.zhouplus.plusreader.domains;

/**
 * Created by zhouplus
 * Time at 2016/9/9
 * Project name PlusReader
 * Description :
 * Author's email :
 * Version 1.0
 */

import android.support.annotation.NonNull;

import java.util.Arrays;

public class PlusFile implements Comparable {
    public String name;// 文件名(书名)
    public String path;// 父路径
    public String absolutePath;// 绝对路径
    public char[] permisson;// 访问权限,三个char分别是r,w,x，如果没有相应权限，就为-
    public long length;// 文件长度，如果为文件夹则是0
    public boolean isDictionary;// 是否是个文件夹
    public long lastModified;

    @Override
    public String toString() {
        return "PlusFile [name=" + name + ", path=" + path + ", absolutePath=" + absolutePath + ", permisson="
                + Arrays.toString(permisson) + ", length=" + length + ", isDictionary=" + isDictionary + "]";
    }

    @Override
    public int compareTo(@NonNull Object another) {
        boolean lenEqual = false;
        PlusFile other = (PlusFile) another;
        if (isDictionary && !other.isDictionary) {
            return -1;
        }
        if (!isDictionary && other.isDictionary) {
            return 1;
        }
        if (name.length() == other.name.length()) {
            lenEqual = true;
        }
        int length = name.length() >= other.name.length() ? other.name.length() : name.length();
        for (int i = 0; i < length; i++) {
            if (name.charAt(i) > other.name.charAt(i)) {
                return 1;
            }
            if (name.charAt(i) < other.name.charAt(i)) {
                return -1;
            }
        }
        if (lenEqual) {
            return 0;
        } else {
            if (name.length() > other.name.length()) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
