package com.dn_alan.myapplication.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.dn_alan.myapplication.MyEventBusService;
import com.dn_alan.myapplication.Request;
import com.dn_alan.myapplication.Responce;
import com.dn_alan.myapplication.core.Hermes;
import com.dn_alan.myapplication.responce.InstanceResponceMake;
import com.dn_alan.myapplication.responce.ObjectResponceMake;
import com.dn_alan.myapplication.responce.ResponceMake;

public class HermesService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private MyEventBusService.Stub mBinder = new MyEventBusService.Stub() {
        @Override
        public Responce send(Request request) throws RemoteException {
            ResponceMake responceMake = null;
            switch (request.getType()){
                case Hermes.TYPE_GET:  //获取单列
                    responceMake = new InstanceResponceMake();
                    break;
                case Hermes.TYPE_NEW:
                    responceMake = new ObjectResponceMake();
                    break;
            }
            return responceMake.makeResponce(request);
        }
    };
}
