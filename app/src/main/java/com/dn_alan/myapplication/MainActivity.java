package com.dn_alan.myapplication;

import android.content.Intent;
import android.os.UserManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dn_alan.myapplication.core.DNEventbus;
import com.dn_alan.myapplication.core.DNSubscribe;
import com.dn_alan.myapplication.core.DNThreadMode;
import com.dn_alan.myapplication.core.Hermes;
import com.dn_alan.myapplication.manager.DnUserManager;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        EventBus.getDefault().register(this);
//        DNEventbus.getDefault().register(this);
//
//        textView = (TextView) findViewById(R.id.tv);

        Hermes.getDefault().init(this);
        Hermes.getDefault().register(DnUserManager.class);
        DnUserManager.getInstance().setFriend(new Friend("alan", 18));
    }

    public void change(View view) {
        startActivity(new Intent(this, SecondActivity.class));
    }


    @DNSubscribe(threadMode = DNThreadMode.MAIN)
    public void receive(Friend friend){
//        textView.setText(friend.toString() + "==" + Thread.currentThread().getName());
        Toast.makeText(this, friend.toString(), Toast.LENGTH_SHORT).show();
    }

//    @Subscribe
//    public void receive(String s){
//        textView.setText(s);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
        DNEventbus.getDefault().unregister(this);
    }
}
