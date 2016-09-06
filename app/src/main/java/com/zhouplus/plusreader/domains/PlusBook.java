package com.zhouplus.plusreader.domains;

import java.io.Serializable;

/**
 * Created by zhouplus
 * Time at 2016/8/22
 * Project name PlusReader
 * Description :书的基本类
 * Author's email :
 * Version 1.0
 */
public class PlusBook implements Serializable {
    public int id;
    public String name;
    public String path;
    public String description;
    public String add_date;
    public int length;
    public int read_begin;
    public int read_end;

    @Override
    public String toString() {
        return "PlusBook{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", description='" + description + '\'' +
                ", add_date='" + add_date + '\'' +
                ", length=" + length +
                ", read_begin=" + read_begin +
                ", read_end=" + read_end +
                '}';
    }
}
