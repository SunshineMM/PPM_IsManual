package com.example.npttest.fragment;


import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.npttest.App;
import com.example.npttest.R;
import com.example.npttest.activity.QueryCarnum;
import com.example.npttest.activity.QueryRecord;
import com.example.npttest.activity.ReleaseRemarks;
import com.example.npttest.activity.SetCommonCity;
import com.example.npttest.activity.SetPassageWay;
import com.example.npttest.constant.Constant;
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
import util.UpdateAppUtils;

/**
 * Created by liuji on 2017/7/28.
 */

public class Fragment3 extends Fragment {
    @Bind(R.id.fg3_query_record)
    LinearLayout fg3QueryRecord;
    @Bind(R.id.fg3_query_carnum)
    LinearLayout fg3QueryCarnum;
    @Bind(R.id.fg3_release_remarks)
    LinearLayout fg3ReleaseRemarks;
    @Bind(R.id.fg3_setComCity)
    LinearLayout fg3SetComCity;
    @Bind(R.id.fg3_vehicle_inventory)
    LinearLayout fg3VehicleInventory;
    @Bind(R.id.fg3_violation_record)
    LinearLayout fg3ViolationRecord;
    @Bind(R.id.fg3_setPrinter)
    LinearLayout fg3SetPrinter;
    @Bind(R.id.fg3_update)
    LinearLayout fg3Update;
    @Bind(R.id.fg3_newsv_tv)
    TextView fg3NewsvTv;
    @Bind(R.id.fg3_newsv_img)
    ImageView fg3NewsvImg;
    @Bind(R.id.fg3_release_way)
    LinearLayout fg3ReleaseWay;
    private BluetoothAdapter bluetoothAdapter;
    private int dsv;
    private String updateurl;
    private boolean update = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment3, null);
        ButterKnife.bind(this, v);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (App.serverurl != null) {
            get_update();
        }
        return v;
    }


    @OnClick({R.id.fg3_query_carnum, R.id.fg3_update, R.id.fg3_setComCity, R.id.fg3_release_remarks,
            R.id.fg3_setPrinter, R.id.fg3_vehicle_inventory, R.id.fg3_violation_record, R.id.fg3_query_record,
            R.id.fg3_release_way})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fg3_query_carnum:
                Intent intent = new Intent(getActivity(), QueryCarnum.class);
                startActivity(intent);
                break;
            case R.id.fg3_update:
                if (update) {
                    UpdateAppUtils.from(getActivity())
                            .checkBy(UpdateAppUtils.CHECK_BY_VERSION_CODE) //更新检测方式，默认为VersionCode
                            .serverVersionCode(dsv)
                            .serverVersionName(String.valueOf(((double) dsv) / 100))
                            .apkPath("http://" + updateurl)
                            .downloadBy(UpdateAppUtils.DOWNLOAD_BY_APP) //下载方式：app下载、手机浏览器下载。默认app下载
                            .isForce(false) //是否强制更新，默认false 强制更新情况下用户不同意更新则不能使用app
                            .update();
                } else {
                    Toasty.info(getContext(), "已经是最新版本", Toast.LENGTH_SHORT, true).show();
                }
                break;
            case R.id.fg3_setComCity:
                //设置常用城市
                startActivity(new Intent(getActivity(), SetCommonCity.class));
                break;
            case R.id.fg3_release_remarks:
                Intent relintent = new Intent(getActivity(), ReleaseRemarks.class);
                relintent.putExtra("relBoo", false);
                startActivity(relintent);
                break;
            case R.id.fg3_setPrinter:
               /* Boolean aBoolean = checkBluetooth();
                Log.e("TAG", "点击fg3_setPrinter" + aBoolean);
                if (aBoolean) {
                    Log.e("TAG", checkBluetooth() + "");
                    startActivity(new Intent(getActivity(), AddBlueTooth.class));
                }*/
                Toast.makeText(getActivity(), "稍后开放，请耐心等待更新", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fg3_vehicle_inventory:
                Toast.makeText(getActivity(), "稍后开放，请耐心等待更新", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fg3_violation_record:
                Toast.makeText(getActivity(), "稍后开放，请耐心等待更新", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fg3_query_record:
                startActivity(new Intent(getActivity(), QueryRecord.class));
                break;
            case R.id.fg3_release_way:
                startActivity(new Intent(getActivity(), SetPassageWay.class));
                break;
        }
    }

    /**
     * 判断是否需要更新
     */

    private void get_update() {
        String jsons = "{\"cmd\":\"13\",\"type\":\"" + Constant.TYPE + "\",\"code\":\"" + Constant.CODE + "\",\"dsv\":\"" + Constant.DSV + "\",\"task\":\"10\",\"sign\":\"abcd\"}";
        Log.e("TAG", jsons);
        OkHttpUtils.postString().url(App.serverurl)
                .content(jsons)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("TAG", response);
                try {
                    JSONObject rpjson = new JSONObject(response);
                    String reasonjson = rpjson.getString("reason");
                    JSONObject resultjson = rpjson.getJSONObject("result");
                    JSONObject datajsonObject = resultjson.getJSONObject("data");
                    // Log.e("TAG", datajsonObject.toString());
                    int rs = datajsonObject.getInt("rs");
                    dsv = datajsonObject.getInt("dsv");
                    updateurl = datajsonObject.getString("url");
                    Log.e("TAG", dsv + "");
                    if (rs == 0) {
                        update = false;
                        fg3NewsvImg.setVisibility(View.GONE);
                        fg3NewsvTv.setVisibility(View.VISIBLE);
                    } else if (rs == 1) {
                        update = true;
                        fg3NewsvImg.setVisibility(View.VISIBLE);
                        fg3NewsvTv.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    /**
     * 判断是否开启蓝牙
     *
     * @return
     */
    private boolean checkBluetooth() {
        if (bluetoothAdapter == null) {
            return false;
        } else if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);
            return false;
            //return bluetoothAdapter.enable();
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
