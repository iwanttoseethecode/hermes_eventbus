package com.dn_alan.myapplication.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.dn_alan.myapplication.MyEventBusService;
import com.dn_alan.myapplication.Request;
import com.dn_alan.myapplication.Responce;
import com.dn_alan.myapplication.service.HermesService;

import java.util.concurrent.ConcurrentHashMap;

public class ServiceConnectionManager {
    private static final ServiceConnectionManager ourInstance = new ServiceConnectionManager();

    //对应得binder 对象保存到hasmap中
    private final ConcurrentHashMap<Class<? extends HermesService>,
            MyEventBusService> mHermesService = new ConcurrentHashMap<>();


    private final ConcurrentHashMap<Class<? extends HermesService>,
            HermesServiceConnection> mHermesServiceConnection = new ConcurrentHashMap<>();

    public static ServiceConnectionManager getInstance(){
        return ourInstance;
    }

    public ServiceConnectionManager() {
    }

    public void bind(Context context, String packageName,
                     Class<HermesService> hermesServiceClass) {
        HermesServiceConnection hermesServiceConnection =
                new HermesServiceConnection(hermesServiceClass);
        mHermesServiceConnection.put(hermesServiceClass, hermesServiceConnection);
        Intent intent;
        if(TextUtils.isEmpty(packageName)){
            intent = new Intent(context, hermesServiceClass);
        }else {
            intent = new Intent();
            intent.setClassName(packageName, hermesServiceClass.getName());
        }
        context.bindService(intent, hermesServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public Responce request(Class<HermesService> hermesServiceClass, Request request) {
        MyEventBusService myEventBusService = mHermesService.get(hermesServiceClass);
        if(myEventBusService != null){
            try {
                Responce responce = myEventBusService.send(request);
                return responce;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private class HermesServiceConnection implements ServiceConnection {
        private Class<? extends HermesService> mClass;

        HermesServiceConnection(Class<? extends HermesService> service){
            this.mClass = service;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyEventBusService myEventBusService = MyEventBusService.Stub.asInterface(service);
            mHermesService.put(mClass,myEventBusService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mHermesService.remove(mClass);
        }
    }
}
