package com.dn_alan.myapplication.manager;

import com.dn_alan.myapplication.Friend;
import com.dn_alan.myapplication.annotion.ClassId;

@ClassId("com.dn_alan.myapplication.manager.DnUserManager")
public class DnUserManager implements IUserManager {
    Friend friend;

    private static DnUserManager sInstance = null;

    private DnUserManager() {}

    public static synchronized DnUserManager getInstance(){
        if(sInstance == null){
            sInstance = new DnUserManager();
        }
        return sInstance;
    }

    public static synchronized DnUserManager getInstance(String s){
        if(sInstance == null){
            sInstance = new DnUserManager();
        }
        return sInstance;
    }

    public Friend getFriend() {
        return friend;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }

    public static DnUserManager getsInstance() {
        return sInstance;
    }

    public static void setsInstance(DnUserManager sInstance) {
        DnUserManager.sInstance = sInstance;
    }
}
