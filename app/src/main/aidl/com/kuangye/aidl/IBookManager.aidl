// IBookManager.aidl
package com.kuangye.aidl;
import com.kuangye.aidl.Book;
import com.kuangye.aidl.IOnNewBookArrivedListener;
interface IBookManager {
    List<Book> getList();
    void addBook(in Book book);
    void registerListener(IOnNewBookArrivedListener listener);
    void unRegisterListener(IOnNewBookArrivedListener listener);
    String getRemoteNumber();
}
