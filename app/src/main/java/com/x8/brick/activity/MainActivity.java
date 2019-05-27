package com.x8.brick.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.x8.brick.R;
import com.x8.brick.activity.interceptor.InterceptorActivity;
import com.x8.brick.activity.simple.SimpleActivity;
import com.x8.brick.activity.multihost.MultiHostActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        findViewById(R.id.simple).setOnClickListener(this);
        findViewById(R.id.multi_host).setOnClickListener(this);
        findViewById(R.id.interceptor).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.simple:
                startActivity(new Intent(this, SimpleActivity.class));
                break;
            case R.id.multi_host:
                startActivity(new Intent(this, MultiHostActivity.class));
                break;
            case R.id.interceptor:
                startActivity(new Intent(this, InterceptorActivity.class));
                break;
        }
    }
}
