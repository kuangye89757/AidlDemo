package com.kuangye.server;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.kuangye.aidl.Book;
import com.kuangye.aidl.IBookManagerX;
import com.kuangye.aidl.IOnNewBookArrivedListener;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 服务端
 *
 * @author shijie9
 */
public class BookManagerService extends Service {

    private static final String TAG = "BookManagerService";
    private CopyOnWriteArrayList<Book> mBooks = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IOnNewBookArrivedListener> mCallbackList = new RemoteCallbackList<>();
    private AtomicBoolean mIsServiceDestoryed = new AtomicBoolean(false);


    public BookManagerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    /**
     * 服务端实现对应的Stub
     */
    private Binder mBinder = new IBookManagerX.Stub() {
        @Override
        public List<Book> getList(){
            //模拟耗时
            SystemClock.sleep(5000);
            return mBooks;
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener){
            mCallbackList.register(listener);
            int N = mCallbackList.beginBroadcast();
            mCallbackList.finishBroadcast();
            Log.i(TAG, "registerListener " + listener);
            Log.i(TAG, "registerListener size " + N + "");
        }

        @Override
        public void unRegisterListener(IOnNewBookArrivedListener listener)  {
            mCallbackList.unregister(listener);
            int N = mCallbackList.beginBroadcast();
            mCallbackList.finishBroadcast();
            Log.i(TAG, "unRegisterListener " + listener);
            Log.i(TAG, "registerListener size " + N + "");
        }

        @Override
        public Book addBookIn(Book book)  {
            //客户端向服务端添加数据 修改参数后返回
            book.setBookName(book.getBookName() + " In");
            mBooks.add(book);
            Log.i(TAG, "invoking addBookIn method , now the list is : " + mBooks.toString());
            return book;
        }

        @Override
        public Book addBookOut(Book book) {
            //客户端向服务端添加数据 修改参数后返回
            book.setBookName(book.getBookName() + " Out");
            mBooks.add(book);
            Log.i(TAG, "invoking addBookOut method , now the list is : " + mBooks.toString());
            return book;
        }

        @Override
        public Book addBookInout(Book book)  {
            //客户端向服务端添加数据 修改参数后返回
            book.setBookName(book.getBookName() + " Inout");
            mBooks.add(book);
            Log.i(TAG, "invoking addBookInout( method , now the list is : " + mBooks.toString());
            return book;
        }


        @Override
        public String getRemoteNumber()  {
            //获取远端序列号
            return UUID.randomUUID().toString();
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            //远程校验权限
            int chkId = checkCallingOrSelfPermission("com.kuangye.permission.BOOK_SERVICE");
            if(chkId == PackageManager.PERMISSION_DENIED){
                //未授权
                Log.i(TAG,"没有权限访问");
                return false;
            }
            return super.onTransact(code, data, reply, flags);
        }
    };


    @Override
    public void onCreate() {
        /**开启线程*/
        super.onCreate();
        new Thread(new ServiceWork()).start();
    }

    /**
     * 每隔5s添加一本书通知给客户端
     */
    private class ServiceWork implements Runnable {

        @Override
        public void run() {
            while (!mIsServiceDestoryed.get()) {
                SystemClock.sleep(5000);
                int bookId = mBooks.size() + 1;
                onNewBookArrived(new Book(bookId, "newBook " + bookId));
            }
        }
    }


    private void onNewBookArrived(Book book) {
        mBooks.add(book);
        final int N = mCallbackList.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IOnNewBookArrivedListener listener = mCallbackList.getBroadcastItem(i);
            if (listener != null) {
                Log.i(TAG, "通知");
                try {
                    listener.onNewBookArrived(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        mCallbackList.finishBroadcast();
    }
}
