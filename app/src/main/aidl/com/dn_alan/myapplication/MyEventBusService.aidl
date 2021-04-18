// MyEventBusService.aidl
package com.dn_alan.myapplication;

// Declare any non-default types here with import statements
import com.dn_alan.myapplication.Request;
import com.dn_alan.myapplication.Responce;
interface MyEventBusService {
    Responce send(in Request request);
}
