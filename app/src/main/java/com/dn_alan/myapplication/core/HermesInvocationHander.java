package com.dn_alan.myapplication.core;

import android.text.TextUtils;

import com.dn_alan.myapplication.Responce;
import com.dn_alan.myapplication.responce.ResponceBean;
import com.dn_alan.myapplication.responce.ResponceMake;
import com.google.gson.Gson;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class HermesInvocationHander implements InvocationHandler {

    private Class aClass;
    private Class hermeService;

    private Gson gson = new Gson();

    public HermesInvocationHander(Class aClass, Class hermeService) {
        this.aClass = aClass;
        this.hermeService = hermeService;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Responce responce = Hermes.getDefault().sendObjectRequest(hermeService, aClass, method, args);
        if(!TextUtils.isEmpty(responce.getData())){
            ResponceBean responceBean = gson.fromJson(responce.getData(), ResponceBean.class);
            if(responceBean.getData() != null){
                Object responceBeanData = responceBean.getData();
                String data = gson.toJson(responceBeanData);

                Class returnType = method.getReturnType();
                Object obj = gson.fromJson(data, returnType);
                return obj;
            }
        }
        return null;
    }
}
