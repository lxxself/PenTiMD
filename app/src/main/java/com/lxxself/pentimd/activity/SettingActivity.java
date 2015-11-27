package com.lxxself.pentimd.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.lxxself.pentimd.R;
import com.umeng.analytics.MobclickAgent;

public class SettingActivity extends AppCompatActivity {

    private android.widget.Switch swnight;
    private android.widget.Switch swjs;
    private SharedPreferences sp;


    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("Setting", MODE_PRIVATE);
        if (sp.getBoolean("nightMode", false)) {
            setTheme(R.style.NightTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_setting);
        initView();
        swnight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor edit = sp.edit();
                edit.putBoolean("nightMode", isChecked);
                edit.commit();
                if (isChecked) {
                    Toast.makeText(SettingActivity.this, "打开夜间模式", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SettingActivity.this, "关闭夜间模式", Toast.LENGTH_LONG).show();
                }
                recreate();
            }
        });
        swjs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor edit = sp.edit();
                edit.putBoolean("JS", isChecked);
                edit.commit();
            }
        });


    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    private void initView() {
        this.swjs = (Switch) findViewById(R.id.sw_js);
        this.swnight = (Switch) findViewById(R.id.sw_night);
        swnight.setChecked(sp.getBoolean("nightMode",false));
        swjs.setChecked(sp.getBoolean("JS", true));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

}
