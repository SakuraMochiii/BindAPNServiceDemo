package com.cloudpos.bindapnservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.wizarpos.wizarviewagentassistant.aidl.IAPNManagerService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindApn();
    }

    public void bindApn() {
        ServiceConnection serviceConnectionAPN1 = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                IAPNManagerService apnManagerService = IAPNManagerService.Stub.asInterface(service);
                try {
                    Log.d("IAPNManagerService","IAPNManagerService  bind success.");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    unbindService(this);
                }

            }
        };
        startConnectService(this, new ComponentName("com.wizarpos.wizarviewagentassistant", "com.wizarpos.wizarviewagentassistant.APNManagerService"), serviceConnectionAPN1);
    }


    private void startConnectService(Context context, ComponentName comp, ServiceConnection connection) {
        try {
            Intent intent = new Intent();
            intent.setPackage(comp.getPackageName());
            intent.setComponent(comp);
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
