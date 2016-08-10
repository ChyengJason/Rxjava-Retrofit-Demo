package com.jscheng.retrofitapplication;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.jscheng.retrofitapplication.API.ZhuanLanApi;
import com.jscheng.retrofitapplication.API.ZhuanlanApi2;
import com.jscheng.retrofitapplication.API.ZhuanlanApi3;
import com.jscheng.retrofitapplication.Model.ZhuanLanAuthor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String API_URL = "https://zhuanlan.zhihu.com";
    public final static int TOAST_MSG = 0;
    public final static int AUTHOR_MSG = 1;
    private Button btn;
    private TextView text;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case TOAST_MSG:
                    Toast.makeText(MainActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case AUTHOR_MSG:
                    text.setText((String)msg.obj);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void init(){
        btn = (Button)findViewById(R.id.btn);
        text = (TextView)findViewById(R.id.text);
        btn.setOnClickListener(this);
        retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn:
                Message msg = new Message();
                msg.what = TOAST_MSG;
                msg.obj = "send!";
                handler.sendMessage(msg);
                getInternetInfo3();
                break;
            default:
                break;
        }
    }

    // 使用retrofit2 的方式
    private void getInternetInfo(){
        ZhuanLanApi api = retrofit.create(ZhuanLanApi.class);
        Call<ZhuanLanAuthor> call = api.getAuthor("qinchao");
        call.enqueue(new Callback<ZhuanLanAuthor>() {
            @Override
            public void onResponse(Call<ZhuanLanAuthor> call, Response<ZhuanLanAuthor> response) {
                ZhuanLanAuthor author = response.body();
                Message msg = new Message();
                msg.obj = author.getName();
                msg.what = AUTHOR_MSG;
                handler.sendMessage(msg);
            }
            @Override
            public void onFailure(Call<ZhuanLanAuthor> call, Throwable t) {

            }
        });
//        call.execute();//同步方式
//        要调用同步请求，用execute；异步请求则是调用enqueue
    }


    //使用 retrofit1.9 的方式，2之后不在这么用，所以会报错
    private void getInternetInfo2(){
        ZhuanlanApi2 api = retrofit.create(ZhuanlanApi2.class);
        api.getAuthor("qinchao", new Callback<ZhuanLanAuthor>() {
            @Override
            public void onResponse(Call<ZhuanLanAuthor> call, Response<ZhuanLanAuthor> response) {
                ZhuanLanAuthor author = response.body();
                Message msg = new Message();
                msg.obj = author.getName();
                msg.what = AUTHOR_MSG;
                handler.sendMessage(msg);
            }
            @Override
            public void onFailure(Call<ZhuanLanAuthor> call, Throwable t) {

            }
        });
    }

    //使用rxjava retrofit结合的方式,加入doOnNext对获取的数据进行操作
    public void getInternetInfo3(){
        retrofit = new Retrofit.Builder().baseUrl(API_URL)
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                //将Json转化
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                //转为Rxjava的Observable
                .build();

        ZhuanlanApi3 api = retrofit.create(ZhuanlanApi3.class);

        api.getAuthor("qinchao")
                .doOnNext(new Action1<ZhuanLanAuthor>() {
                    @Override
                    public void call(ZhuanLanAuthor zhuanLanAuthor) {
                        doSomeThing(zhuanLanAuthor);
                    }
                }) //doOnNext()的执行在onNext()前，对数据进行相关处理
                .subscribeOn(Schedulers.io())// 指定 subscribe() 发生在 IO 线程,我就是死在这里
                .observeOn(AndroidSchedulers.mainThread())// 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer<ZhuanLanAuthor>() {
                    @Override
                    public void onCompleted() {
                        Log.e("tag","complete");
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.e("tag","error"+e.getMessage());
                    }
                    @Override
                    public void onNext(ZhuanLanAuthor author) {
//                        Message msg = new Message();
//                        msg.obj = author.getName();
//                        msg.what = AUTHOR_MSG;
//                        handler.sendMessage(msg);
//                        Log.e("tag",author.getName());
                        text.setText(author.getName());
                    }
                });
    }

    private void doSomeThing(ZhuanLanAuthor zhuanLanAuthor) {

    }
}
