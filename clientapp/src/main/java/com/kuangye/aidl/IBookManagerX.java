package com.kuangye.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * 徒手写AIDL生成的java文件 (Ixxx.java)
 */
public interface IBookManagerX extends IInterface {

    /**
     * 静态抽象Stub 继承Binder 实现Ixxx (服务端本地对象)
     */
    public static abstract class Stub extends Binder implements IBookManagerX {

        private static final String DESCRIPTOR = "com.kuangye.aidl.IBookManagerX";

        public Stub() {
            /**
             * public void attachInterface(IInterface owner, String descriptor) {
             *         mOwner = owner;
             *         mDescriptor = descriptor;
             *     }
             */
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * 同进程下   返回Stub
         * 不同进程下 返回Proxy代理
         */
        public static IBookManagerX asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }

            /**
             * public IInterface queryLocalInterface(String descriptor) {
             *         if (mDescriptor.equals(descriptor)) {
             *             return mOwner;
             *         }
             *         return null;
             *     }
             */
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin instanceof IBookManagerX) {
                return (IBookManagerX) iin;
            }
            return new Proxy(obj);
        }

        /**
         * 返回Stub
         */
        @Override
        public IBinder asBinder() {
            return this;
        }

        /**
         * 重写Binder的onTransact方法
         */
        @Override
        protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION:
                    //询问交互接收方的规范接口描述符
                    reply.writeString(DESCRIPTOR);
                    return true;


                case TRANSACTION_getList:

                    //1.对应writeInterfaceToken方法 用于校验
                    data.enforceInterface(DESCRIPTOR);

                    //2.通过实现类获取数据 _result
                    List<Book> _result = getList();

                    //3.对应readException 这里设置int值为0 表示无异常 (执行完业务代码之后调用)
                    reply.writeNoException();

                    //4.设置数据给客户端使用 对应createTypedArrayList
                    /**
                     * public final <T extends Parcelable> void writeTypedList(List<T> val) {
                     *         if (val == null) {
                     *             writeInt(-1);
                     *             return;
                     *         }
                     *         int N = val.size();
                     *         int i = 0;
                     *         writeInt(N);
                     *         while (i < N) {
                     *             T item = val.get(i);
                     *             if (item != null) {
                     *                 writeInt(1);
                     *                 item.writeToParcel(this, 0);
                     *             } else {
                     *                 writeInt(0);
                     *             }
                     *             i++;
                     *         }
                     *     }
                     */
                    reply.writeTypedList(_result);
                    return true;


                case TRANSACTION_registerListener:
                    //1.对应writeInterfaceToken方法 用于校验
                    data.enforceInterface(DESCRIPTOR);

                    //2.对应writeStrongBinder 获取Binder 这里需要通过asInterface方法获取系统自动判断是代理对象还是本地对象
                    IOnNewBookArrivedListener _arg0 = IOnNewBookArrivedListener.Stub.asInterface(data.readStrongBinder());
                    registerListener(_arg0);

                    //3.对应readException 这里设置int值为0 表示无异常
                    reply.writeNoException();
                    return true;


                case TRANSACTION_unRegisterListener:
                    data.enforceInterface(DESCRIPTOR);
                    IOnNewBookArrivedListener _arg1 = IOnNewBookArrivedListener.Stub.asInterface(data.readStrongBinder());
                    unRegisterListener(_arg1);
                    reply.writeNoException();
                    return true;


                case TRANSACTION_addBookIn:
                    data.enforceInterface(DESCRIPTOR);
                    Book book;

                    /**
                     * 【in流向 服务端接收客户端book】
                     */
                    if (data.readInt() != 0) {
                        book = Book.CREATOR.createFromParcel(data);
                    } else {
                        book = null;
                    }
                    Book _result1 = addBookIn(book);
                    reply.writeNoException();

                    if (_result1 != null) {
                        reply.writeInt(1);
                        _result1.writeToParcel(reply, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    } else {
                        reply.writeInt(0);
                    }

                    /**
                     * 【in流向 服务端不单独传回客户端book】
                     */
                    return true;


                case TRANSACTION_addBookOut:
                    data.enforceInterface(DESCRIPTOR);

                    /**
                     * 【out流向 服务端不接收客户端book】
                     */
                    //由于客户端流向为out 所以服务端自己new一个对象
                    Book _arg2 = new Book();
                    Book _result2 = addBookInout(_arg2);
                    reply.writeNoException();
                    if (_result2 != null) {
                        reply.writeInt(1);
                        _result2.writeToParcel(reply, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    } else {
                        reply.writeInt(0);
                    }

                    /**
                     * 【out流向 服务端单独传回客户端一个book】
                     */
                    //同时把Book也传回给客户端 (注意顺序 先设置的reply内容是_result2 之后是_arg2)
                    if (_arg2 != null) {
                        reply.writeInt(1);
                        _arg2.writeToParcel(reply, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;

                case TRANSACTION_addBookInout:
                    data.enforceInterface(DESCRIPTOR);
                    Book _arg3;
                    /**
                     * 【inout流向 服务端接收客户端book】
                     */
                    if (data.readInt() != 0) {
                        _arg3 = Book.CREATOR.createFromParcel(data);
                    } else {
                        _arg3 = null;
                    }

                    Book _result3 = addBookInout(_arg3);
                    reply.writeNoException();

                    if (_result3 != null) {
                        reply.writeInt(1);
                        _result3.writeToParcel(reply, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    } else {
                        reply.writeInt(0);
                    }

                    /**
                     * 【inout流向 服务端单独传回客户端一个book】
                     */
                    if(_arg3!=null){
                        reply.writeInt(1);
                        _arg3.writeToParcel(reply, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    }else {
                        reply.writeInt(0);
                    }
                    return true;

                default:
                    break;
            }

            return super.onTransact(code, data, reply, flags);
        }

        /**
         * Stub静态内部类Proxy 实现Ixxx  (客户端代理对象)
         */
        private static class Proxy implements IBookManagerX {

            private IBinder mRemote;

            Proxy(IBinder remote) {
                mRemote = remote;
            }

            /**
             * 返回Stub
             */
            @Override
            public IBinder asBinder() {
                return mRemote;
            }

            /**
             * 对外方法
             */
            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }


            /**
             * Proxy类的方法里面一般的工作流程:
             * 1.生成 _data和_reply 数据流，并向_data中存入客户端的数据
             * 2.通过transact()方法将它们传递给服务端,并请求服务端调用指定方法
             * 3.接收_reply数据流，并从中取出服务端传回来的数据
             */
            @Override
            public List<Book> getList() throws RemoteException {
                //将方法的传参的数据存入_data 
                Parcel _data = Parcel.obtain();
                //将方法的返回值的数据存入 _reply
                Parcel _reply = Parcel.obtain();

                //定义返回值结果
                List<Book> _result;
                try {
                    //1.数据内容打包到Parcel对象中
                    _data.writeInterfaceToken(DESCRIPTOR);

                    //2.数据发送
                    //transact() 方法：这是客户端和服务端通信的核心方法。
                    //调用这个方法之后，客户端将会挂起当前线程，
                    //等候服务端执行完相关任务后通知并接收返回的 _reply 数据流

                    /**
                     * 异常抛出:
                     *      transact()方法将在IBinder所在的进程不存在时抛出RemoteException
                     *      目标进程不存在，那么调用pingBinder()时返回false
                     *      linkToDeath()方法可以用于向IBinder注册一个DeathRecipient，当持有它的进程结束，该方法会被调用
                     */

                    //第一个参数是一个方法ID (客户端与服务端约定好的code  标识客户端期望调用服务端的哪个函数)
                    //第四个参数是一个int值 
                    //  0 -- 表示数据可以双向流通 即服务端回来的_reply可以正常的携带数据回来
                    //  1 -- 表示数据只能单向流通 即服务端回来的_reply将不携带任何数据
                    mRemote.transact(Stub.TRANSACTION_getList, _data, _reply, 0);

                    //3._reply为远程进程的返回内容封装 若返回值_reply获取的int值不为0 则有异常抛出,用于定位服务端问题 对应writeNoException
                    _reply.readException();

                    /**
                     * public final <T> ArrayList<T> createTypedArrayList(Parcelable.Creator<T> c) {
                     *         int N = readInt();
                     *         if (N < 0) {
                     *             return null;
                     *         }
                     *         ArrayList<T> l = new ArrayList<T>(N);
                     *         while (N > 0) {
                     *             if (readInt() != 0) {
                     *                 l.add(c.createFromParcel(this));
                     *             } else {
                     *                 l.add(null);
                     *             }
                     *             N--;
                     *         }
                     *         return l;
                     *     }
                     */
                    //4.从远程进程中返回的_reply数据封装中读取出结果 createTypedArrayList该方法可循环读取
                    //故Parcelable对象需要实现Parcelable.Creator
                    _result = _reply.createTypedArrayList(Book.CREATOR);
                } finally {
                    //5.回收Parcel对象
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
                //将方法的传参的数据存入_data 
                Parcel _data = Parcel.obtain();
                //将方法的返回值的数据存入 _reply
                Parcel _reply = Parcel.obtain();
                try {
                    //1.数据内容打包到Parcel对象中
                    _data.writeInterfaceToken(DESCRIPTOR);
                    // 将IOnNewBookArrivedListener的Binder对象打包到_data
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);

                    //2.数据发送
                    mRemote.transact(Stub.TRANSACTION_registerListener, _data, _reply, 0);

                    //3._reply为远程进程的返回内容封装 若返回值为有异常则会抛出,用于定位问题
                    _reply.readException();

                } finally {
                    //4.回收Parcel对象
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void unRegisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
                //将方法的传参的数据存入_data 
                Parcel _data = Parcel.obtain();
                //将方法的返回值的数据存入 _reply
                Parcel _reply = Parcel.obtain();
                try {
                    //1.数据内容打包到Parcel对象中
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);

                    //2.数据发送
                    mRemote.transact(Stub.TRANSACTION_unRegisterListener, _data, _reply, 0);

                    //3._reply为远程进程的返回内容封装 若返回值为有异常则会抛出,用于定位问题
                    _reply.readException();

                } finally {
                    //4.回收Parcel对象
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public Book addBookIn(Book book) throws RemoteException {
                //将方法的传参数据存入_data
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                //定义返回值
                Book _result;
                try {
                    //1.数据内容打包到Parcel对象中
                    _data.writeInterfaceToken(DESCRIPTOR);

                    /**
                     * 设置int值用于同服务端约定 【in流向 传给服务端book】
                     */
                    if (book != null) {
                        _data.writeInt(1);//int值为1 则服务端获取_data数据
                        book.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);//int值为0 则服务端将该数据置为null
                    }


                    //2.数据发送
                    mRemote.transact(Stub.TRANSACTION_addBookIn, _data, _reply, 0);

                    //3._reply为远程进程的返回内容封装 若返回值为有异常则会抛出,用于定位问题
                    _reply.readException();

                    //_reply.readInt()不为0,则有数据 (同服务端约定)
                    if (_reply.readInt() != 0) {
                        _result = Book.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }

                    /**【in流向 不接收服务端的book】*/

                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public Book addBookOut(Book book) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                Book _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(TRANSACTION_addBookOut, _data, _reply, 0);
                    /**
                     * 【out流向  不传给服务端book】
                     */
                    _reply.readException();

                    //获取服务端返回
                    if (_reply.readInt() != 0) {
                        _result = Book.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }

                    /**
                     * 【out流向 接收服务端的book】
                     */
                    if (_reply.readInt() != 0) {
                        book.readFromParcel(_reply);
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public Book addBookInout(Book book) throws RemoteException {

                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                Book _result;

                try {
                    _data.writeInterfaceToken(DESCRIPTOR);

                    /**
                     * 【inout流向  传给服务端book】
                     */
                    if (book != null) {
                        _data.writeInt(1);
                        book.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(TRANSACTION_addBookInout, _data, _reply, 0);
                    _reply.readException();

                    if (_reply.readInt() != 0) {
                        _result = Book.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }

                    /**
                     * 【inout流向 接收服务端的book】
                     */
                    if (_reply.readInt() != 0) {
                        book.readFromParcel(_reply);
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public String getRemoteNumber() {
                return null;
            }
        }

        //code
        static final int TRANSACTION_getList = FIRST_CALL_TRANSACTION + 0;
        static final int TRANSACTION_registerListener = FIRST_CALL_TRANSACTION + 1;
        static final int TRANSACTION_unRegisterListener = FIRST_CALL_TRANSACTION + 2;
        static final int TRANSACTION_addBookIn = FIRST_CALL_TRANSACTION + 3;
        static final int TRANSACTION_addBookOut = FIRST_CALL_TRANSACTION + 4;
        static final int TRANSACTION_addBookInout = FIRST_CALL_TRANSACTION + 5;
        static final int TRANSACTION_getRemoteNumber = FIRST_CALL_TRANSACTION + 6;

    }

    /****************AIDL中定义的方法,由于调用transact方法这里需要抛出RemoteException****************/

    List<Book> getList() throws RemoteException;

    void registerListener(IOnNewBookArrivedListener listener) throws RemoteException;

    void unRegisterListener(IOnNewBookArrivedListener listener) throws RemoteException;

    //通过三种定位tag做对比试验，观察输出的结果
    Book addBookIn(Book book) throws RemoteException;

    Book addBookOut(Book book) throws RemoteException;

    Book addBookInout(Book book) throws RemoteException;

    String getRemoteNumber() throws RemoteException;
}