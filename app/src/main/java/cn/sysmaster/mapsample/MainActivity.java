package cn.sysmaster.mapsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.model.LatLng;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import cn.sysmaster.mapsample.model.ResponseModel;
import cn.sysmaster.mapsample.widget.MapLayout;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @author sysmaster
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private MapLayout mMapLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapLayout = findViewById(R.id.map_layout);
        mMapLayout.setOnMapStatusChanageFinishListener(new MapLayout.OnMapStatusChanageFinishListener() {
            @Override
            public void onFinish(LatLng latLng) {
                loadData(latLng);
            }
        });
        mMapLayout.setOnMarkerClickListener(new MapLayout.OnMarkerClickListener() {
            @Override
            public void onClick(String uuid, LatLng latLng) {
                getUnitDataInfo(uuid);
            }
        });
    }


    /**
     * 加载数据
     */
    private void loadData(LatLng latLng) {
        Map<String, Object> bodyMap = new HashMap<>(4);
        bodyMap.put("gps_latitude", latLng.latitude + "");
        bodyMap.put("gps_longitude", latLng.longitude + "");
        bodyMap.put("latitude", latLng.latitude + "");
        bodyMap.put("longitude", latLng.longitude + "");

        Map<String, Object> headerMap = new HashMap<>(13);
        headerMap.put("access_token", "");
        headerMap.put("api", "YBusiness.getSimpleBusinessListByPosition");
        headerMap.put("applet", "android");
        headerMap.put("brand", "xiaomi");
        headerMap.put("client_v", "2.151");
        headerMap.put("dpi", "720,1800");
        headerMap.put("model", "redmi 4a");
        headerMap.put("msg_id", "");
        headerMap.put("session_id", "");
        headerMap.put("service", "sharedCharging");
        headerMap.put("session_authorization", "bbgkdagbhdldjkjhiehj");
        headerMap.put("type", "");
        headerMap.put("userAgent", "jiedian/ client_v/2.151 (Redmi 4A; Android; Android OS ; 6.0.1; zh) ApacheHttpClient/4.0");

        Map<String, Object> map = new HashMap<>(2);
        map.put("header", new JSONObject(headerMap));
        map.put("body", new JSONObject(bodyMap));
        OkHttpUtils
                .put()
                .url("https://api.ankerjiedian.com/index.php")
                .requestBody(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JSON.toJSONString(map)))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        ResponseModel model = JSON.parseObject(response, ResponseModel.class);
                        mMapLayout.setMarkerDaces(model.body.business);
                    }
                });
    }


    /**
     * 获取详情
     *
     * @param uuid id
     */
    private void getUnitDataInfo(String uuid) {
        Map<String, Object> bodyMap = new HashMap<>(4);
        bodyMap.put("special_return_text", "1");
        bodyMap.put("uuid", uuid);

        Map<String, Object> headerMap = new HashMap<>(13);
        headerMap.put("access_token", "");
        headerMap.put("api", "YBusiness.getBusiness");
        headerMap.put("applet", "android");
        headerMap.put("brand", "xiaomi");
        headerMap.put("client_v", "2.151");
        headerMap.put("dpi", "720,1800");
        headerMap.put("model", "redmi 4a");
        headerMap.put("msg_id", "");
        headerMap.put("session_id", "");
        headerMap.put("service", "sharedCharging");
        headerMap.put("session_authorization", "bbgkdagbhdldjkjhiehj");
        headerMap.put("type", "");
        headerMap.put("userAgent", "jiedian/ client_v/2.151 (Redmi 4A; Android; Android OS ; 6.0.1; zh) ApacheHttpClient/4.0");

        Map<String, Object> map = new HashMap<>(2);
        map.put("header", new JSONObject(headerMap));
        map.put("body", new JSONObject(bodyMap));
        OkHttpUtils
                .put()
                .url("https://api.ankerjiedian.com/index.php")
                .requestBody(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JSON.toJSONString(map)))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        ResponseModel model = JSON.parseObject(response, ResponseModel.class);
                        mMapLayout.setMarkerDaces(model.body.business);
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        mMapLayout.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapLayout.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapLayout.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapLayout.onDestroy();
    }

}
