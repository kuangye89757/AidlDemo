// IOnNewBookArrivedListener.aidl
package com.kuangye.aidldemo;
import com.kuangye.aidldemo.Book;
interface IOnNewBookArrivedListener {
    /**
     * 有新书到时通知
     */
    void onNewBookArrived(in Book book);
}
