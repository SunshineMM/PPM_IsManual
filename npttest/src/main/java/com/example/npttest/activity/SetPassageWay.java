package com.example.npttest.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.npttest.App;
import com.example.npttest.R;
import com.example.npttest.constant.Constant;
import com.example.npttest.util.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.MediaType;

public class SetPassageWay extends NoStatusbarActivity {

    @Bind(R.id.set_passage_way_return)
    ImageView setPassageWayReturn;
    @Bind(R.id.set_passage_way_rb1)
    RadioButton setPassageWayRb1;
    @Bind(R.id.set_passage_way_rb2)
    RadioButton setPassageWayRb2;
    @Bind(R.id.set_passage_way_rg)
    RadioGroup setPassageWayRg;
    private boolean releaseWayBoo;
    private int releaseWayInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_passage_way);
        ButterKnife.bind(this);
        setPassageWayRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.set_passage_way_rb1:
                        //自动放行
                        if (App.serverurl!=null){
                            autoRelease();
                        }
                        break;
                    case R.id.set_passage_way_rb2:
                        //确认放行
                        if (App.serverurl!=null){
                            confirmRelease();
                        }
                        break;
                }
            }
        });
        releaseWayInt= (int) SPUtils.get(SetPassageWay.this,Constant.SAVE_RELEASE_WAY_INT,-1);
        if (releaseWayInt==1){
            setPassageWayRb1.setChecked(true);
        }else if (releaseWayInt==0){
            setPassageWayRb2.setChecked(true);
        }
        Log.e("TAG","当前选中的："+releaseWayInt);
    }

    @OnClick(R.id.set_passage_way_return)
    public void onViewClicked() {
        finish();
    }

    /**
     * 自动放行
     */
    private void autoRelease(){

        final String jsonContent="{\"cmd\":\"259\",\"type\":\""+ Constant.TYPE+"\",\"code\":\""+Constant.CODE+"\",\"dsv\":\""+Constant.DSV+"\",\"tempCard\":\"1\",\"sign\":\"abcd\"}";

        Log.e("TAG","设置为自动放行："+jsonContent);
        OkHttpUtils.postString().url(App.serverurl)
                .content(jsonContent)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toasty.error(SetPassageWay.this,"请检查网络", Toast.LENGTH_SHORT,true).show();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("TAG","设置为自动放行返回结果："+response);
                try {
                    JSONObject rejsonObject=new JSONObject(response);
                    int code=rejsonObject.getInt("code");
                    if (code==100){
                        JSONObject result=rejsonObject.getJSONObject("result");
                        JSONObject data=result.getJSONObject("data");
                        int crs=data.getInt("crs");
                        if (crs==0){
                            Log.e("TAG","设置自动放行成功");
                            SPUtils.put(SetPassageWay.this,Constant.SAVE_RELEASE_WAY_BOO,true);
                            SPUtils.put(SetPassageWay.this,Constant.SAVE_RELEASE_WAY_INT,1);
                        }else {
                            Log.e("TAG","设置自动放行失败，crs："+crs);
                        }
                    }else {
                        Toast.makeText(SetPassageWay.this, "操作失败，错误码："+code, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 确认放行
     */
    private void confirmRelease(){

        final String jsonContent="{\"cmd\":\"259\",\"type\":\""+ Constant.TYPE+"\",\"code\":\""+Constant.CODE+"\",\"dsv\":\""+Constant.DSV+"\",\"tempCard\":\"0\",\"sign\":\"abcd\"}";
        Log.e("TAG","设置为确定放行："+jsonContent);
        OkHttpUtils.postString().url(App.serverurl)
                .content(jsonContent)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toasty.error(SetPassageWay.this,"请检查网络", Toast.LENGTH_SHORT,true).show();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("TAG","设置为确定放行返回结果："+response);
                try {
                    JSONObject rejsonObject=new JSONObject(response);
                    int code=rejsonObject.getInt("code");
                    if (code==100){
                        JSONObject result=rejsonObject.getJSONObject("result");
                        JSONObject data=result.getJSONObject("data");
                        int crs=data.getInt("crs");
                        if (crs==0){
                            Log.e("TAG","设置确认放行成功");
                            SPUtils.put(SetPassageWay.this,Constant.SAVE_RELEASE_WAY_BOO,false);
                            SPUtils.put(SetPassageWay.this,Constant.SAVE_RELEASE_WAY_INT,0);
                        }else {
                            Log.e("TAG","设置确认放行失败，crs："+crs);
                        }
                    }else {
                        Toast.makeText(SetPassageWay.this, "操作失败，错误码："+code, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}