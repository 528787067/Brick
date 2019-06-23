package com.x8.brick;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.x8.brick.asset.AssetActivity;
import com.x8.brick.converter.ConverterActivity;
import com.x8.brick.executor.ExecutorActivity;
import com.x8.brick.gson.GsonActivity;
import com.x8.brick.interceptor.InterceptorActivity;
import com.x8.brick.rxjava2.RxJava2Activity;
import com.x8.brick.simple.SimpleActivity;
import com.x8.brick.multihost.MultiHostActivity;
import com.x8.brick.task.TaskActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        findViewById(R.id.simple).setOnClickListener(this);
        findViewById(R.id.multi_host).setOnClickListener(this);
        findViewById(R.id.interceptor).setOnClickListener(this);
        findViewById(R.id.gson).setOnClickListener(this);
        findViewById(R.id.converter).setOnClickListener(this);
        findViewById(R.id.rxjava2).setOnClickListener(this);
        findViewById(R.id.task).setOnClickListener(this);
        findViewById(R.id.asset).setOnClickListener(this);
        findViewById(R.id.executor).setOnClickListener(this);
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
            case R.id.gson:
                startActivity(new Intent(this, GsonActivity.class));
                break;
            case R.id.converter:
                startActivity(new Intent(this, ConverterActivity.class));
                break;
            case R.id.rxjava2:
                startActivity(new Intent(this, RxJava2Activity.class));
                break;
            case R.id.task:
                startActivity(new Intent(this, TaskActivity.class));
                break;
            case R.id.asset:
                startActivity(new Intent(this, AssetActivity.class));
                break;
            case R.id.executor:
                startActivity(new Intent(this, ExecutorActivity.class));
                break;
        }
    }
}
