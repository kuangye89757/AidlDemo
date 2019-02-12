package com.kuangye.clientapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kuangye.aidl.Book;
import com.kuangye.aidl.IBookManagerX;
import com.kuangye.aidl.IOnNewBookArrivedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户端
 * @author shijie9
 */
public class BookManagerActivity extends AppCompatActivity implements ServiceConnection {
    private static final String TAG = "BookManagerActivity";
    private IBookManagerX mBinder;
    private Intent serviceIntent;
    private static final int MSG_NEW_BOOK_ARRIVED = 0x0000;
    private static final int MSG_BOOK_LIST = 0x0001;
    private static final int MSG_GET_NUM = 0x0002;

    private TextView textView;
    private TextView textView1;
    private List<Book> books = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_manager);

        //显式绑定服务
        serviceIntent = new Intent();
        serviceIntent.setComponent(new ComponentName("com.kuangye.server","com.kuangye.server.BookManagerService"));
        bindService(serviceIntent,this, Context.BIND_AUTO_CREATE);

        //业务逻辑
        processHandle();
    }

    private void processHandle() {
        textView = (TextView) findViewById(R.id.tv);
        textView1 = (TextView) findViewById(R.id.tv1);


        /**向服务端添加数据*/
        findViewById(R.id.add_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (mBinder != null && mBinder.asBinder().isBinderAlive()) {
                            try {
                                books.clear();
                                books = mBinder.getList();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            if (books != null && books.size() > 0) {
                                Book book = new Book(books.size() + 1, "new book");
                                books.add(book);
                            }
                        }

                    }
                }).start();
            }
        });


        /**获取远端数据*/
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (mBinder != null && mBinder.asBinder().isBinderAlive()) {
                            try {
                                books.clear();
                                books = mBinder.getList();
                                mHandler.obtainMessage(MSG_BOOK_LIST,books).sendToTarget();

                                /**注册远程监听*/
                                mBinder.registerListener(mArrivedListener);

                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }).start();
            }
        });


        /**获取序列号*/
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (mBinder != null && mBinder.asBinder().isBinderAlive()) {
                            try {
                                mHandler.obtainMessage(MSG_GET_NUM,mBinder.getRemoteNumber()).sendToTarget();
                            } catch (RemoteException e) {

                            }
                        }

                    }
                }).start();
            }
        });
    }


    /**
     * 客户端绑定Service时在ServiceConnection.onServiceConnected获取Service端onBind返回的IBinder对象
     * 同本地Service不同,返回的IBinder对象,只是onBind返回的IBinder对象的代理对象
     * @param name
     * @param service
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mBinder = IBookManagerX.Stub.asInterface(service);
        try {
            //连接成功后注册死亡代理
            service.linkToDeath(mDeathRecipient,0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mBinder = null;
        Log.i(TAG, "binder died");
    }

    /**
     * 当操作远程对象时，你经常需要查看它们是否有效，有三种方法可以使用：
     １ transact()方法将在IBinder所在的进程不存在时抛出RemoteException异常。
     ２ 如果目标进程不存在，那么调用pingBinder()时返回false。
     ３ 可以用linkToDeath()方法向IBinder注册一个IBinder.DeathRecipient,在IBinder代表的进程退出时被调用。
     * 这里使用第三种情况:死亡代理
     *
     */
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if(mBinder == null){
                return;
            }
            //解绑死亡代理
            mBinder.asBinder().unlinkToDeath(mDeathRecipient,0);
            mBinder = null;

            //重新绑定
            bindService(serviceIntent,BookManagerActivity.this,BIND_AUTO_CREATE);
        }
    };


    @Override
    protected void onDestroy() {
        if(isBinderLive()){
            try {
                mBinder.unRegisterListener(mArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(this);
        mBinder = null;
        super.onDestroy();
    }

    /**
     * 这里一定要用Stub 因为它是要传递给服务端的通信的
     * 对于服务端而言,会获得mArrivedListener的代理;
     * 参考IBookManagerX
     *       transact :   _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
     *       onTransact : IOnNewBookArrivedListener _arg0 = IOnNewBookArrivedListener.Stub.asInterface(data.readStrongBinder());
     */
    private IOnNewBookArrivedListener mArrivedListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Book book) throws RemoteException {

        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_NEW_BOOK_ARRIVED:
                    /**新书通知*/
                    Toast.makeText(BookManagerActivity.this, "new book" + msg.obj.toString(), Toast.LENGTH_SHORT)
                            .show();
                    break;

                case MSG_BOOK_LIST:
                    /**书列表*/
                    textView.setText(msg.obj.toString());
                    break;

                case MSG_GET_NUM:
                    /**获取序列号*/
                    textView1.setText("序列号:" + msg.obj.toString());
                    break;

                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    private boolean isBinderLive(){
        return mBinder != null && mBinder.asBinder().isBinderAlive();
    }
}


