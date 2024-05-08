package com.cloudpos.bindapnservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.cloudpos.util.TextViewUtil;
import com.wizarpos.wizarviewagentassistant.aidl.IAPNManagerService;

import java.util.Map;

public class MainActivity extends AbstractActivity implements View.OnClickListener {
    IAPNManagerService apnManagerService ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        log_text = (TextView) this.findViewById(R.id.text_result);
        log_text.setMovementMethod(ScrollingMovementMethod.getInstance());
        log_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int lines = log_text.getHeight();
                int lastLine = log_text.getLayout().getHeight();
                Log.i("total lines: ",""+lines);
                Log.i("text lines: ",""+lastLine);
                if (lastLine >= lines) {
                    log_text.scrollTo(0, log_text.getLayout().getLineTop(log_text.getLineCount()) - log_text.getHeight());
                }
            }
        });
        findViewById(R.id.getSelected).setOnClickListener(this);
        findViewById(R.id.setSelected).setOnClickListener(this);
        findViewById(R.id.addByAllArgs).setOnClickListener(this);
        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.addByMCCAndMNC).setOnClickListener(this);
        findViewById(R.id.clear).setOnClickListener(this);
        findViewById(R.id.settings).setOnClickListener(this);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == R.id.log_default) {
                    log_text.append("\t" + msg.obj + "\n");
                } else if (msg.what == R.id.log_success) {
                    String str = "\t" + msg.obj + "\n";
                    TextViewUtil.infoBlueTextView(log_text, str);
                } else if (msg.what == R.id.log_failed) {
                    String str = "\t" + msg.obj + "\n";
                    TextViewUtil.infoRedTextView(log_text, str);
                } else if (msg.what == R.id.log_clear) {
                    log_text.setText("");
                    log_text.scrollTo(0, 0);
                }

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindApn();
        Toast.makeText(this, "IAPNManagerService  bind success.", Toast.LENGTH_SHORT).show();
    }

    public void bindApn() {
        ServiceConnection serviceConnectionAPN1 = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                try {
                    apnManagerService = IAPNManagerService.Stub.asInterface(service);
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

    @Override
    public void onClick(View v) {
        int index = v.getId();
        String btnText = "";
        if (v instanceof Button) {
            Button button = (Button) v;
            btnText = button.getText().toString();
        }
        try{
            boolean flag = false;
            writerInLog(btnText, R.id.log_default);
            if (index == R.id.getSelected) {
                Map map = apnManagerService.getSelected();
                writerInSuccessLog("result is : "+ JSON.toJSONString(map));
                return;
            } else if (index == R.id.setSelected) {
                flag = apnManagerService.setSelected("");
            } else if (index == R.id.addByAllArgs) {
                String rs = apnManagerService.addByAllArgs("","","","","","","","","","","","","","","","","","","");
                writerInSuccessLog("result is : "+ rs);
                return;
            } else if (index == R.id.add) {
                String rs = apnManagerService.add("","");
                writerInSuccessLog("result is : "+ rs);
                return;
            } else if (index == R.id.addByMCCAndMNC) {
                String rs = apnManagerService.addByMCCAndMNC("","","","");
                writerInSuccessLog("result is : "+ rs);
                return;
            } else if (index == R.id.clear) {
                flag = apnManagerService.clear();
            } else if (index == R.id.settings) {
                writerInLog("", R.id.log_clear);
            }
            if(flag){
                writerInSuccessLog("result is true!");
            }else if (index!=R.id.settings){
                writerInFailedLog("result is false!");
            }
        }catch (Exception e){
            e.printStackTrace();
            writerInFailedLog("test failed!");
        }
    }
}
