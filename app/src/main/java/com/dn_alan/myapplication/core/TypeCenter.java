package com.dn_alan.myapplication.core;

import android.text.TextUtils;

import com.dn_alan.myapplication.bean.RequestBean;
import com.dn_alan.myapplication.bean.RequestParameter;
import com.dn_alan.myapplication.manager.DnUserManager;
import com.dn_alan.myapplication.util.TypeUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class TypeCenter {

    //为了减少反射，所以保存起来
    private final ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, Method>> mRawMethods;
    private final ConcurrentHashMap<String, Class<?>> mClazz;

    private static final TypeCenter ourInstance = new TypeCenter();

    public TypeCenter() {
        mRawMethods = new ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, Method>>();
        mClazz = new ConcurrentHashMap<>();
    }

    public static TypeCenter getInstance() {
        return ourInstance;
    }

    public void register(Class<DnUserManager> clazz) {
        //注册--》类， 注册--》方法
        registerClass(clazz);
        registerMethod(clazz);
    }

    //缓存class
    private void registerClass(Class<DnUserManager> clazz) {
        String name = clazz.getName();
        mClazz.putIfAbsent(name, clazz);
    }

    private void registerMethod(Class<DnUserManager> clazz) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            mRawMethods.putIfAbsent(clazz, new ConcurrentHashMap<String, Method>());
            ConcurrentHashMap<String, Method> map = mRawMethods.get(clazz);
            String methodId = TypeUtils.getMethodId(method);
            map.put(methodId, method);
        }
    }

    public Class<?> getClassType(String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        Class<?> clazz = mClazz.get(name);
        if (clazz == null) {
            try {
                clazz = Class.forName(name);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return clazz;
    }

    public Method getMethod(Class<?> aClass, RequestBean requestBean) {
        String methodName = requestBean.getMethodName();//setFriend()
        if (methodName != null) {
            mRawMethods.putIfAbsent(aClass, new ConcurrentHashMap<String, Method>());
            ConcurrentHashMap<String, Method> methods = mRawMethods.get(aClass);
            Method method = methods.get(methodName);
            if(method != null){
                return method;
            }
            int pos = methodName.indexOf('(');

            Class[] paramters = null;
            RequestParameter[] requestParameters = requestBean.getRequestParameter();
            if (requestParameters != null && requestParameters.length > 0) {
                paramters = new Class[requestParameters.length];
                for (int i=0;i<requestParameters.length;i++) {
                    paramters[i]=getClassType(requestParameters[i].getParameterClassName());
                }
            }
            method = TypeUtils.getMethod(aClass, methodName.substring(0, pos), paramters);
            methods.put(methodName, method);
            return method;
        }
        return null;
    }
}
