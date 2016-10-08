package com.kuangye.aidldemo;

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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by wangshijie on 2016/10/8.
 */
public class BookManagerService extends Service {

    private static final String TAG = "BookManagerService";
    private AtomicBoolean mIsServiceDestoryed = new AtomicBoolean(false);

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<>();


    /**
     * 在服务端实现AIDLService.Stub抽象类
     */
    private Binder mBind = new IBookManager.Stub() {
        @Override
        public List<Book> getList() throws RemoteException {
            /**模拟耗时操作*/
            SystemClock.sleep(5000);
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            /**客户端向服务端添加*/
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.register(listener);
            int N = mListenerList.beginBroadcast();
            mListenerList.finishBroadcast();
            Log.i(TAG,"registerListener " + listener);
            Log.i(TAG,"registerListener size " + N+"");
        }

        @Override
        public void unRegisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.unregister(listener);
            int N = mListenerList.beginBroadcast();
            mListenerList.finishBroadcast();
            Log.i(TAG,"registerListener " + listener);
            Log.i(TAG,"registerListener size " + N+"");
        }

        /**
         * 获取远端序列号
         * @return
         * @throws RemoteException
         */
        @Override
        public String getRemoteNumber() throws RemoteException {
            return UUID.randomUUID().toString();
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            /**远程调用验证权限*/
            int checkid = checkCallingOrSelfPermission("com.kuangye.permission.BOOK_SERVICE");
            if(checkid == PackageManager.PERMISSION_DENIED){
                //未授权
                Log.i(TAG,"没有权限访问");
                return false;
            }
            return super.onTransact(code, data, reply, flags);
        }
    };

    /**
     * 在服务端onBind方法中返回该实现类
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBind;
    }

    @Override
    public void onCreate() {
        /**开启线程*/
        super.onCreate();
        new Thread(new ServiceWork()).start();
    }

    /**
     * 每隔5s添加一本书通知给客户端
     */
    private class ServiceWork implements Runnable{

        @Override
        public void run() {
            while(!mIsServiceDestoryed.get()){
                SystemClock.sleep(5000);

                int bookId = mBookList.size() + 1;
                onNewBookArrived(new Book(bookId,"newBook"+bookId));
            }
        }
    }


    private void onNewBookArrived(Book book){
        mBookList.add(book);
        final int N = mListenerList.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IOnNewBookArrivedListener listener = mListenerList.getBroadcastItem(i);
            if (listener != null) {
                Log.i(TAG,"通知");
                try {
                    listener.onNewBookArrived(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        mListenerList.finishBroadcast();
    }
}
