// IOnNewBookArrivedListener.aidl
package com.kuangye.aidl;
import com.kuangye.aidl.Book;
interface IOnNewBookArrivedListener {
    /**
     * 有新书到时通知
     */
    void onNewBookArrived(in Book book);
}
