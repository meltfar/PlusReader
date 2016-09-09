package com.zhouplus.plusreader.domains;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.zhouplus.plusreader.utils.PreferenceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by zhouplus
 * Time at 2016/9/9
 * Project name PlusReader
 * Description :
 * Author's email :
 * Version 1.0
 */
public class FileExplorer {

    private String mRootPath;
    private Context mContext;

    private String mLastHandled;

    private final String LAST_VIEW_PATH = "FileLastViewed";

    public FileExplorer(Context context, @Nullable String path) {
        String lastPath = PreferenceManager.getPreferenceString(context, LAST_VIEW_PATH, "");
        if (path != null) {
            File f = new File(path);
            if (f.exists() && f.isDirectory()) {
                this.mRootPath = path;
            } else if (lastPath.equals("")) {
                this.mRootPath = Environment.getExternalStorageDirectory().getPath() + "/";
            } else {
                this.mRootPath = lastPath;
            }
        } else {
            if (lastPath.equals(""))
                this.mRootPath = Environment.getExternalStorageDirectory().getPath() + "/";
            else
                this.mRootPath = lastPath;
        }
        this.mContext = context;
    }

    private String[] receiveExtensions = new String[]{".txt", ".chm"};

    /**
     * 检查文件夹路径的最后有没有/,如果没有就加上
     *
     * @param path dictionary
     */
    private String checkDictionaryPath(String path) {
        if (path.charAt(path.length() - 1) != '/') {
            return path + "/";
        } else {
            return path;
        }
    }

    public List<PlusFile> getDefaultFiles() {
        return walkDictionary(mRootPath);
    }

    public String getLastHandled() {
        return mLastHandled;
    }

    @SuppressWarnings("unchecked")
    public void sortFiles(List<PlusFile> files) {
        Collections.sort(files);
    }

    /**
     * 遍历给定目录下的所有文件，并返回可以当做文本阅读的PlusFile类
     *
     * @param path 要遍历的目录
     * @return 如果失败就返回null
     */
    public List<PlusFile> walkDictionary(String path) {
        if (path == null) {
            return null;
        }
        path = checkDictionaryPath(path);
        File root = new File(path);
        if (!root.exists() || !root.isDirectory()) {
            return null;
        }
        mLastHandled = path;
        List<PlusFile> books = new ArrayList<>();
        String[] list = root.list();
        for (String file : list) {
            // 给出绝对路径
            String pathName = path + file;
            // 如果是文件夹
            File tmp = new File(pathName);
            if (tmp.isDirectory()) {
                PlusFile pf = new PlusFile();
                pf.isDictionary = true;
                pf.length = 0;
                pf.name = file;
                pf.absolutePath = pf.path = path + file;
                pf.permisson = new char[]{tmp.canRead() ? 'r' : '-', tmp.canWrite() ? 'w' : '-',
                        tmp.canExecute() ? 'x' : '-'};
                pf.lastModified = tmp.lastModified();
                books.add(pf);
                continue;
            }
            // 获取拓展名
            String ext = getExtensions(file);
            // 如果没有拓展名
            if (ext == null) {
                continue;
            }
            // 如果有拓展名
            for (String e : receiveExtensions) {
                // 如果这个文件是可阅读的文本
                if (e.equals(ext.toLowerCase())) {
                    File tmpFile = new File(pathName);
                    PlusFile pf = new PlusFile();
                    pf.isDictionary = false;
                    pf.length = tmpFile.length();
                    pf.name = getFileName(file);
                    pf.path = path;
                    pf.absolutePath = pathName;
                    pf.permisson = new char[]{tmpFile.canRead() ? 'r' : '-', tmpFile.canWrite() ? 'w' : '-',
                            tmpFile.canExecute() ? 'x' : '-'};
                    pf.lastModified = tmp.lastModified();
                    books.add(pf);
                }
            }
        }
        sortFiles(books);
        return books;
    }

    /**
     * 获取文件后缀名
     *
     * @param pathName 文件名或者全路径
     * @return 后缀名，如果没有后缀名就返回NULL
     */
    private String getExtensions(String pathName) {
        int point = pathName.lastIndexOf('.');
        if (point != -1) {
            return pathName.substring(point, pathName.length());
        } else {
            return null;
        }
    }

    /**
     * 返回给定路径或者文件名的，不包含后缀名的文件名
     *
     * @param pathName 路径或者文件名
     * @return 出错返回NULL
     */
    private String getFileName(String pathName) {
        if (pathName == null) {
            return null;
        }
        int lastSpe = pathName.lastIndexOf('/');
        String fullName;
        // 说明给定的是一个单纯文件名
        if (lastSpe == -1) {
            fullName = pathName;
        } else {
            // 说明是一个完整路径
            fullName = pathName.substring(lastSpe + 1, pathName.length());
        }

        int point = fullName.lastIndexOf('.');
        if (point == -1) {
            return fullName;
        } else {
            return fullName.substring(0, point);
        }
    }

    public void setRoot(String path) {
        File f = new File(path);
        if (f.exists() && f.isDirectory()) {
            this.mRootPath = path;
            PreferenceManager.setPreferenceString(mContext, LAST_VIEW_PATH, path);
        }
    }
}
