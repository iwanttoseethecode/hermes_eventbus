package com.dn_alan.myapplication.core;

import android.content.Context;

import com.dn_alan.myapplication.Request;
import com.dn_alan.myapplication.Responce;
import com.dn_alan.myapplication.annotion.ClassId;
import com.dn_alan.myapplication.bean.RequestBean;
import com.dn_alan.myapplication.bean.RequestParameter;
import com.dn_alan.myapplication.manager.DnUserManager;
import com.dn_alan.myapplication.service.HermesService;
import com.dn_alan.myapplication.util.TypeUtils;
import com.google.gson.Gson;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Hermes {
    private Context mContext;
    private TypeCenter typeCenter;
    private ServiceConnectionManager serviceConnectionManager;

    private Gson gson = new Gson();

    //得到对象
    public static final int TYPE_NEW = 0;
    //得到单例
    public static final int TYPE_GET = 1;

    private static final Hermes ourInstance = new Hermes();

    public Hermes() {
        serviceConnectionManager = ServiceConnectionManager.getInstance();
        typeCenter = TypeCenter.getInstance();
    }

    public static Hermes getDefault() {
        return ourInstance;
    }

    public void init(Context context) {
        this.mContext = context.getApplicationContext();
    }

    //----------------------------服务端-------------------------
    public void register(Class<DnUserManager> clazz) {
        typeCenter.register(clazz);
    }


    //----------------------------客户端-------------------------
    public void connect(Context context,
                        Class<HermesService> hermesServiceClass) {
        connectApp(context, null, hermesServiceClass);
    }

    private void connectApp(Context context, String packageName, Class<HermesService> hermesServiceClass) {
        init(context);
        serviceConnectionManager.bind(context.getApplicationContext(), packageName, hermesServiceClass);
    }

    public <T> T getInstance(Class<T> tClass, Object... parameters) {
        Responce responce = sendRequest(HermesService.class, tClass, null, parameters);
        return getProxy(HermesService.class, tClass);
    }

    private <T> T getProxy(Class<HermesService> hermesServiceClass, Class<T> tClass) {
        ClassLoader classLoader = hermesServiceClass.getClassLoader();
        T proxy = (T) Proxy.newProxyInstance(classLoader, new Class<?>[]{tClass},
                new HermesInvocationHander(hermesServiceClass, tClass));
        return proxy;
    }

    private <T> Responce sendRequest(Class<HermesService> hermesServiceClass,
                                     Class<T> tClass, Method method, Object[] parameters) {
        RequestBean requestBean = new RequestBean();

        //set全类名
        String className = null;
        if (tClass.getAnnotation(ClassId.class) == null) {
            requestBean.setClassName(tClass.getName());
            requestBean.setResultClassName(tClass.getName());
        } else {
            requestBean.setClassName(tClass.getAnnotation(ClassId.class).value());
            requestBean.setResultClassName(tClass.getAnnotation(ClassId.class).value());
        }


        //set方法
        if (method != null) {
            requestBean.setMethodName(TypeUtils.getMethodId(method));
        }

        //set参数
        RequestParameter[] requestParameters = null;
        if (parameters != null && parameters.length > 0) {
            requestParameters = new RequestParameter[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Object parameter = parameters[i];
                String parameterClassName = parameter.getClass().getName();
                String parameterValue = gson.toJson(parameter);
                RequestParameter requestParameter = new RequestParameter(parameterClassName, parameterValue);
                requestParameters[i] = requestParameter;
            }
        }
        if (requestParameters != null) {
            requestBean.setRequestParameter(requestParameters);
        }

        Request request = new Request(gson.toJson(requestBean), TYPE_GET);

        return serviceConnectionManager.request(hermesServiceClass, request);
    }

    public <T> Responce sendObjectRequest(Class hermeService, Class<T> aClass,
                                          Method method, Object[] args) {
        RequestBean requestBean = new RequestBean();

        //set全类名
        String className = null;
        if (aClass.getAnnotation(ClassId.class) == null) {
            requestBean.setClassName(aClass.getName());
            requestBean.setResultClassName(aClass.getName());
        } else {
            requestBean.setClassName(aClass.getAnnotation(ClassId.class).value());
            requestBean.setResultClassName(aClass.getAnnotation(ClassId.class).value());
        }

        //set方法
        if (method != null) {
            requestBean.setMethodName(TypeUtils.getMethodId(method));
        }

        //set参数
        RequestParameter[] requestParameters = null;
        if (args != null && args.length > 0) {
            requestParameters = new RequestParameter[args.length];
            for (int i = 0; i < args.length; i++) {
                Object parameter = args[i];
                String parameterClassName = parameter.getClass().getName();
                String parameterValue = gson.toJson(parameter);

                RequestParameter requestParameter = new RequestParameter(parameterClassName, parameterValue);
                requestParameters[i] = requestParameter;
            }
        }

        if (requestParameters != null) {
            requestBean.setRequestParameter(requestParameters);
        }

        Request request = new Request(gson.toJson(requestBean), TYPE_NEW);

        return serviceConnectionManager.request(hermeService, request);
    }
}
