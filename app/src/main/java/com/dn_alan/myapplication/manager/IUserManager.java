package com.dn_alan.myapplication.manager;

import com.dn_alan.myapplication.Friend;
import com.dn_alan.myapplication.annotion.ClassId;

@ClassId("com.dn_alan.myapplication.manager.DnUserManager")
public interface IUserManager {
    public Friend getFriend();
    public void setFriend(Friend friend);
}
