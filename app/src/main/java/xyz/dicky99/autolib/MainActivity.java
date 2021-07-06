package xyz.dicky99.autolib;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.*;

public class MainActivity extends AppCompatActivity {

    public static String baseUrl="http://pan.dicky99.xyz:8080";
    private EditText stuno,openid,seatid,email;
    private Button bind,unbind,mail,test_id,choose;
    private Spinner sp_start_hour, sp_end_hour, sp_start_minute, sp_end_minute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setListeners();
        try {
            validateVersion();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);

        //得到SharedPreferences.Editor对象，并保存数据到该对象中
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("openid", openid.getText().toString().trim());
        editor.putString("stuno", stuno.getText().toString().trim());
        editor.putString("email", email.getText().toString().trim());
        editor.putString("seatid", seatid.getText().toString().trim());
        //保存key-value对到文件中
        editor.apply();
        super.onStop();
    }

    public void initView(){
        stuno = findViewById(R.id.et_stuno);
        openid = findViewById(R.id.et_openid);
        seatid = findViewById(R.id.et_seatid);
        email = findViewById(R.id.et_email);
        bind = findViewById(R.id.btn_bind);
        unbind = findViewById(R.id.btn_unbind);
        mail = findViewById(R.id.btn_mail);
        choose = findViewById(R.id.btn_choose);
        test_id = findViewById(R.id.btn_test_id);
        SharedPreferences sharedPreferences = this.getSharedPreferences("info", MODE_PRIVATE);
        email.setText(sharedPreferences.getString("email",""));
        openid.setText(sharedPreferences.getString("openid",""));
        stuno.setText(sharedPreferences.getString("stuno",""));
        seatid.setText(sharedPreferences.getString("seatid",""));
        sp_start_hour = findViewById(R.id.sp_start_hour);
        sp_end_hour = findViewById(R.id.sp_end_hour);
        sp_start_minute = findViewById(R.id.sp_start_minute);
        sp_end_minute = findViewById(R.id.sp_end_minute);
        //设置spinner
        List<Integer> list_hour = new ArrayList<>(), list_minute = new ArrayList<>();
        for(int i=6; i<=22; i++){list_hour.add(i);}
        list_minute.add(0);list_minute.add(30);
        //hour_spinner
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, list_hour);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_start_hour.setAdapter(adapter);
        sp_end_hour.setAdapter(adapter);
        sp_end_hour.setSelection(16);
        //minute_spinner
        ArrayAdapter<Integer> adapter2 = new ArrayAdapter<>(this, R.layout.simple_spinner_item, list_minute);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_start_minute.setAdapter(adapter2);
        sp_start_minute.setSelection(1);
        sp_end_minute.setAdapter(adapter2);
        seatid.setEnabled(false);
    }

    public void setListeners(){
        OnClick onClick = new OnClick();
        bind.setOnClickListener(onClick);
        unbind.setOnClickListener(onClick);
        mail.setOnClickListener(onClick);
        test_id.setOnClickListener(onClick);
        choose.setOnClickListener(onClick);
    }

    public class OnClick implements View.OnClickListener{
        @Override
        public void onClick(View v) throws NumberFormatException {
            switch (v.getId()){
                case R.id.btn_bind: {
                    Toast.makeText(MainActivity.this, "正在绑定中", Toast.LENGTH_SHORT).show();
                    String url = baseUrl + "/user/bind";
                    OkHttpClient okHttpClient = new OkHttpClient();
                    @SuppressLint("DefaultLocale") final String json = String.format("{\"stuno\":\"%s\",\"openid\":\"%s\",\"begin\":%d,\"end\":%d,\"seatid\":%d,\"email\":\"%s\"}",
                            stuno.getText().toString(), openid.getText().toString(), (((Integer) sp_start_hour.getSelectedItem()))* 60  + ((Integer) sp_start_minute.getSelectedItem()),
                            (((Integer) sp_end_hour.getSelectedItem()) )* 60+ ((Integer) sp_end_minute.getSelectedItem()), Integer.valueOf(seatid.getText().toString()), email.getText().toString());
                    final RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

                    // 创建请求参数
                    Request request = new Request.Builder().url(url).post(body).build();
                    Call call = okHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result = response.body().string();
                            ;
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    });
                    break;
                }
                case R.id.btn_unbind: {
                    String url = baseUrl + "/user/unbind";
                    OkHttpClient okHttpClient = new OkHttpClient();
                    @SuppressLint("DefaultLocale") final String json = String.format("{\"stuno\":\"%s\",\"openid\":\"%s\"}",
                            stuno.getText().toString(), openid.getText().toString());
                    final RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

                    // 创建请求参数
                    Request request = new Request.Builder().url(url).post(body).build();
                    Call call = okHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result = response.body().string();
                            ;
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    });
                    break;
                }
                case R.id.btn_mail:{
                    String url = baseUrl + "/mail";
                    OkHttpClient okHttpClient = new OkHttpClient();
                    @SuppressLint("DefaultLocale") final String json = String.format("{\"email\":\"%s\"}",
                            email.getText().toString());
                    final RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

                    // 创建请求参数
                    Request request = new Request.Builder().url(url).post(body).build();
                    Call call = okHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result = response.body().string();
                            ;
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    });
                    break;
                }
                case R.id.btn_test_id:{
                    //判断时间
                    if (new Date().getHours() < 6){
                        Toast.makeText(MainActivity.this, "当前时间无法测试，请手动开vpn并点击下面的选座测试", Toast.LENGTH_SHORT).show();
                    }else {
                        String url = baseUrl + "/user/test?openid=" + openid.getText().toString();
                        OkHttpClient okHttpClient = new OkHttpClient();

                        // 创建请求参数
                        Request request = new Request.Builder().url(url).build();
                        Call call = okHttpClient.newCall(request);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Looper.prepare();
                                Toast.makeText(MainActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String result = response.body().string();
                                ;
                                Looper.prepare();
                                Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_LONG).show();
                                Looper.loop();
                            }
                        });
                    }
                    
                    break;
                }

                case R.id.btn_choose:{
                    Intent intent = new Intent(MainActivity.this, WebActivity.class);
                    intent.putExtra("openid",openid.getText().toString());
                    startActivityForResult(intent,1);
                    break;
                }

                default:
                    break;
            }
        }
    }

    public void validateVersion() throws PackageManager.NameNotFoundException {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
        final String version = packInfo.versionName;
        String url = baseUrl + "/app/latestversion";
        OkHttpClient okHttpClient = new OkHttpClient();

        // 创建请求参数
        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                Toast.makeText(MainActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                final HashMap res = gson.fromJson(result,HashMap.class);
                if (!version.equals(res.get("version").toString())){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("检测到新版本"+res.get("version"));
                    dialog.setMessage("更新内容:\n"+res.get("description"));
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uri = Uri.parse(Objects.requireNonNull(res.get("site")).toString());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    });
                    dialog.setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    Looper.prepare();
                    dialog.show();
                    Looper.loop();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1){
            if (resultCode==RESULT_OK){
                seatid.setText(data.getStringExtra("seatid"));
            }
        }
    }

}