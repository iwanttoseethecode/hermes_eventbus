package com.dn_alan.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.dn_alan.myapplication.core.Hermes;
import com.dn_alan.myapplication.manager.IUserManager;
import com.dn_alan.myapplication.service.HermesService;

public class SecondActivity extends AppCompatActivity {
    private IUserManager iUerManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Hermes.getDefault().connect(this, HermesService.class);
    }

    public void send(View view) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void userManager(View view) {
        iUerManager = Hermes.getDefault().getInstance(IUserManager.class);

//        DnUserManager.getInstance()
//                .setFriend(new Friend());
    }

    public void getSend(View view) {
        Toast.makeText(this,  iUerManager.getFriend().toString(), Toast.LENGTH_SHORT).show();
    }
}
