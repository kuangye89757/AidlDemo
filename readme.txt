使用AIDL时

    创建aidl的package  选中module后右键new--Folder--AIDL Folder
        (先在server端定义好AIDL,再拷贝给client端)
    
    创建aidl文件 选中aidl的package后右键new--AIDL--AIDL File 
        (这时会在AndroidManifest.xml的指定的package下创建出AIDL文件,这里统一移到aidl的package下)
    
        1.并将aidl文件放在aidl的package下
        2.修改aidl文件中package为aidl的package
        3.之后Rebuild Project
        4.切换成Project视图,查看build--generated--source--aidl--debug下是否生成java文件 (这里只会有interface生成的文件,不会出现Book.java)

    若aidl文件中引用自定义类型来传输需要注意:
        1.例如IBookManager.aidl文件中使用了Book类,需要将其序列化成Parcelable
            并创建Book.aidl声明parcelable Book;此处切记parcelable的p是小写
            再在IBookManager.aidl中显式导入import
			
			aidl中使用的Book包名需要与Book.java的包名一致
			(因为实质就是将aidl文件编译成了对应的java文件,而java文件中需要的Book 就需要我们自行编写了)

        2.拷贝aidl包下文件到客户端,保证客户端和服务端一致
               这里拷贝的有java/包名/aidl下的文件
                          aidl/包名/aidl下的文件

        3.AIDL中除了基本数据类型以外，其他类型的参数必须标上方向：in、out或者inout
        4.服务端启动一次即可,因为客户端访问的是服务端的service
        5.为访问服务端设置权限
            <!--自定义服务端访问权限-->
            <permission
                android:name="com.kuangye.permission.BOOK_SERVICE"
                android:protectionLevel="normal"/>

            <!-- 将该权限设置到远端服务上 -->
            <service
                android:name=".aidl.BookManagerService"
                android:enabled="true"
                android:exported="true"
                android:permission="com.kuangye.permission.BOOK_SERVICE"
                >

            通过onTransact方法来进行权限验证

            <!--客户端在清单文件中设置远端访问权限的才能访问-->
            <uses-permission android:name="com.kuangye.permission.BOOK_SERVICE" />


    工作原理:
        clientapp(客户端)通过与app(远程服务端)中的service建立连接关系;
        BookManagerActivity中onServiceConnected的第二参数IBinder,
        调用asInterface()方法,如果bindService绑定的是同一进程的service,返回的是服务端Stub对象本身，即Stub类
                             那么在客户端是直接操作Stub对象，并不进行进程通信了,
                             自己实现Stub的具体实现,并通过onBind方法返回

                             如果bindService绑定的不是同一进程的service，返回的是代理对象，即Proxy类
                             obj==android.os.BinderProxy对象，被包装成一个AIDLService.Stub.Proxy代理对象
                             服务端来实现Stub的具体实现, 并通过onBind方法返回

        具体参考IBookManager的asInterface方法

        IBinder主要的API是transact()，与之对应的API是Binder.onTransact()
        前者能向远程IBinder对象发送发出调用，后者使你的远程对象能够响应接收到的调用。
        IBinder的API都是 Syncronous(同步)执行的，
            比如transact()直到对方的Binder.onTransact()方法调用玩 后才返回。 调用发生在进程内时无疑是一样的



===================================================================================================
        栗子:点击获取序列号
            if (mBinder != null && mBinder.asBinder().isBinderAlive()) {
                mBinder.getRemoteNumber() ---第一步  返回值为第六步 返回的字符串
            }

            //第一步 实际调用Proxy类中getRemoteNumber方法
            @Override
            public java.lang.String getRemoteNumber() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                java.lang.String _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    /**mRemote即IBookManager的asInterface方法传过去的IBinder 即服务端onBind方法返回的Stub对象
                        即实际调用的是IBinder的transact方法

                    */
                    mRemote.transact(Stub.TRANSACTION_getRemoteNumber, _data, _reply, 0);//第二步
                    _reply.readException();
                    _result = _reply.readString();//第五步 解包reply
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;//第六步 返回字符串
            }


            //第二步  实际调用Stub的onTransact()方法,因为Stub继承Binder
            @Override
            public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
                switch (code) {
                    case TRANSACTION_getRemoteNumber: {
                        data.enforceInterface(DESCRIPTOR);
                        java.lang.String _result = this.getRemoteNumber();//第三步
                        reply.writeNoException();
                        reply.writeString(_result);//第四步 装包到reply
                        return true;
                    }
                }
                return super.onTransact(code, data, reply, flags);
            }


            //第三步 服务端实现的Stub对象
            private Binder mBind = new IBookManager.Stub() {
                /**
                 * 获取远端序列号
                 * @return
                 * @throws RemoteException
                 */
                @Override
                public String getRemoteNumber() throws RemoteException {
                    return UUID.randomUUID().toString();
                }
            }