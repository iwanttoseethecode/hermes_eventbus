package com.dn_alan.myapplication.core;

import android.app.admin.DnsEvent;
import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DNEventbus {
    private static DNEventbus instance = new DNEventbus();


    private Map<Object, List<SubscribleMethod>> cacheMap;

    private Handler handler;

    //线程池
    private ExecutorService executorService;

    public static DNEventbus getDefault() {
        return instance;
    }

    private DNEventbus() {
        this.cacheMap = new HashMap<>();
        handler = new Handler(Looper.getMainLooper());
        executorService = Executors.newCachedThreadPool();
    }

    //注册
    public void register(Object subscriber) {
        Class<?> aClass = subscriber.getClass();
        List<SubscribleMethod> subscribleMethods = cacheMap.get(subscriber);
        //如果已经注册，就不需要注册
        if (subscribleMethods == null) {
            subscribleMethods = getSubscribleMethods(subscriber);
            cacheMap.put(subscriber, subscribleMethods);
        }
    }

    //遍历能够接收事件的方法
    private List<SubscribleMethod> getSubscribleMethods(Object subscriber) {
        List<SubscribleMethod> list = new ArrayList<>();
        Class<?> aClass = subscriber.getClass();
        //需要 subscriber --- 》BaseActivity    ------->Activitiy
        while (aClass != null) {
            //判断分类是在那个报下，（如果是系统的就不需要）
            String name = aClass.getName();
            if (name.startsWith("java.") ||
                    name.startsWith("javax.") ||
                    name.startsWith("android.") ||
                    name.startsWith("androidx.")) {
                break;
            }

            Method[] declaredMethods = aClass.getDeclaredMethods();
            for (Method method : declaredMethods) {
                DNSubscribe annotation = method.getAnnotation(DNSubscribe.class);
                if (annotation == null) {
                    continue;
                }

                //检测这个方法合不合格
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
                    throw new RuntimeException("eventbusz只能接收一个参数");
                }

                //符合要求
                DNThreadMode dnThreadMode = annotation.threadMode();
                SubscribleMethod subscribleMethod = new SubscribleMethod(method, dnThreadMode, parameterTypes[0]);
                list.add(subscribleMethod);
            }
            aClass = aClass.getSuperclass();
        }
        return list;
    }


    //取消注册
    public void unregister(Object subscriber) {
        Class<?> aClass = subscriber.getClass();
        List<SubscribleMethod> list = cacheMap.get(subscriber);
        //如果获取到
        if (list != null) {
            cacheMap.remove(subscriber);
        }
    }

    public void post(final Object obj) {
        Set<Object> set = cacheMap.keySet();
        Iterator<Object> iterator = set.iterator();
        while (iterator.hasNext()) {
            //拿到注册类
            final Object next = iterator.next();

            //获取类中所有添加注解的方法
            List<SubscribleMethod> list = cacheMap.get(next);
            for (final SubscribleMethod subscribleMethod : list) {
                //判断这个方法是否应该接收事件
                if (subscribleMethod.getEventType().isAssignableFrom(obj.getClass())) {
                    switch (subscribleMethod.getThreadMode()) {
                        case MAIN:
                            //如果接收方法在主线程执行的情况
                            if(Looper.myLooper() == Looper.getMainLooper()){
                                invoke(subscribleMethod, next, obj);
                            } else {
                                //post方法执行在子线程中，接收消息在主线程中
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(subscribleMethod, next, obj);
                                    }
                                });
                            }
                            break;
                        //接收方法在子线程种情况
                        case ASYNC:
                            //post方法执行在主线程中
                            if(Looper.myLooper() == Looper.getMainLooper()){
                                executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(subscribleMethod, next, obj);
                                    }
                                });
                            } else {
                                //post方法执行在子线程中
                                invoke(subscribleMethod, next, obj);
                            }
                            break;

                        case POSTING:
                            break;
                    }

                }
            }
        }
    }

    private void invoke(SubscribleMethod subscribleMethod, Object next, Object obj) {
        Method method = subscribleMethod.getMethod();
        try {
            method.invoke(next, obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
