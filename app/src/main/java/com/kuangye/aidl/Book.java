package com.kuangye.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author shijie9
 */
public class Book implements Parcelable {

    //自定义的类型具体包含的数据
    private int bookId;
    private String bookName;

    public Book() {
    }

    public Book(int bookId, String bookName) {
        this.bookId = bookId;
        this.bookName = bookName;
    }

    /**
     * 实现Parcelable接口，需要实现writeToParcel()和readFromParcel()，
     * 实现将对象（数据）写入Parcel，和从Parcel中读出对象
     * 
     * 写的顺序和读的顺序必须一致，因为Parcel类是快速serialization和deserialization机制，
     * 和bundle不同，没有索引机制，是线性的数据存贮和读取
     */
    protected Book(Parcel in) {
        readFromParcel(in);
    }

    /**
     * 实现Parcelable接口的类必须要有一个static field称为CREATOR，用于实现Parcelable.Creator接口的对象。
     * 在AIDL文件自动生成的Java接口中，
     * IBinder将调用Parcelable.Creator来获得传递对象
     */
    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(bookId);
        out.writeString(bookName);
    }

    /**
     * readFromParcel()并不是overrider，而是我们自己提供的方法 
     * 若aidl中该bean流向为out 会在生成的aidl对应的java文件中调用该名称的方法
     */
    public void readFromParcel(Parcel in){
        bookId = in.readInt();
        bookName = in.readString();
    }


    /**
     * setter and getter
     */
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
}
