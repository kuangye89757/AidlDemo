// IBookManager.aidl
package com.kuangye.aidldemo;
import com.kuangye.aidldemo.Book;
import com.kuangye.aidldemo.IOnNewBookArrivedListener;
interface IBookManager {
    List<Book> getList();
    void addBook(in Book book);
    void registerListener(IOnNewBookArrivedListener listener);
    void unRegisterListener(IOnNewBookArrivedListener listener);
    String getRemoteNumber();
}
