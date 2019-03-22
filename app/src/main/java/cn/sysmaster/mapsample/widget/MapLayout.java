package cn.sysmaster.mapsample.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

import cn.sysmaster.mapsample.R;
import cn.sysmaster.mapsample.model.ResponseModel;
import cn.sysmaster.mapsample.util.DensityUtils;

/**
 * @author dabo
 * @date 2019/3/20
 * @describe 封装地图初始化信息
 */
public class MapLayout extends FrameLayout implements SensorEventListener {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient mLocClient;

    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    // 是否首次定位
    boolean isFirstLoc = true;
    private MyLocationData locData;
    private float mCurrentDirection = 0;
    private Double lastX = 0.0;
    private SensorManager mSensorManager;


    /**
     * 当前点击的marker
     */
    private Marker mCurrClickMarker;
    /**
     * 当前点击marker的图标
     */
    private BitmapDescriptor mCurrClickBitmap;
    /**
     * 地图状态变化完成监听
     */
    private OnMapStatusChanageFinishListener mOnMapStatusChanageFinishListener;

    private OnMarkerClickListener mMarkerClickListener;


    public MapLayout(@NonNull Context context) {
        this(context, null);
    }

    public MapLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        //获取传感器管理服务
        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);

        initMap();

        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.icon_move_location);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        addView(imageView, layoutParams);
    }

    /**
     * 初始化地图
     */
    private void initMap() {
        mMapView = new MapView(getContext());
        mBaiduMap = mMapView.getMap();
        // 不显示放大缩小按钮
        mMapView.showZoomControls(false);
        // 不显示比例尺
        mMapView.showScaleControl(false);
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        // 隐藏baidu地图logo
        View child = mMapView.getChildAt(1);
        if ((child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }

        UiSettings uiSettings = mBaiduMap.getUiSettings();
        // 禁用旋转
        uiSettings.setRotateGesturesEnabled(false);
        // 禁用俯视
        uiSettings.setOverlookingGesturesEnabled(false);
        // 禁用指南针
        uiSettings.setCompassEnabled(false);

        // 修改为自定义marker
        BitmapDescriptor marker = BitmapDescriptorFactory
                .fromResource(R.drawable.gps_point);
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL,
                // 是否开启方向
                true,
                marker,
                // 精度圈颜色
                Color.TRANSPARENT,
                // 精度圈边框颜色
                Color.TRANSPARENT));
        // 地图拖动状态监听
        mBaiduMap.setOnMapStatusChangeListener(mOnMapStatusChangeListener);
        // marker点击监听
        mBaiduMap.setOnMarkerClickListener(mOnMarkerClickListener);
        // 地图触摸监听
        mBaiduMap.setOnMapTouchListener(mOnMapTouchListener);

        // 设置定位相关
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(getContext());
        mLocClient.registerLocationListener(mBDAbstractLocationListener);
        LocationClientOption option = new LocationClientOption();
        // 打开gps
        option.setOpenGps(true);
        // 设置坐标类型
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();

        addView(mMapView);
    }

    /**
     * 地图触摸监听
     */
    private BaiduMap.OnMapTouchListener mOnMapTouchListener = new BaiduMap.OnMapTouchListener() {
        @Override
        public void onTouch(MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                // 触摸还原marker图标
                if (mCurrClickMarker != null && mCurrClickBitmap != null) {
                    mCurrClickMarker.setIcon(mCurrClickBitmap);
                }
            }
        }
    };


    /**
     * 地图状态监听
     */
    private BaiduMap.OnMapStatusChangeListener mOnMapStatusChangeListener = new BaiduMap.OnMapStatusChangeListener() {
        @Override
        public void onMapStatusChangeStart(MapStatus status) {
        }

        @Override
        public void onMapStatusChangeStart(MapStatus status, int reason) {
        }

        @Override
        public void onMapStatusChange(MapStatus status) {
        }

        @Override
        public void onMapStatusChangeFinish(MapStatus status) {
            // 地图中心点坐标
            LatLng latLng = status.target;
            // 当前坐标
            LatLng currLatLng = new LatLng(mCurrentLat, mCurrentLon);
            // 计算两点之间距离
            double distance = DistanceUtil.getDistance(currLatLng, latLng);
            if (distance == 0.0d || distance > 500d) {
                if (null != mOnMapStatusChanageFinishListener) {
                    mOnMapStatusChanageFinishListener.onFinish(latLng);
                }
            }
            mCurrentLat = latLng.latitude;
            mCurrentLon = latLng.longitude;
        }
    };

    /**
     * marker点击监听
     */
    private BaiduMap.OnMarkerClickListener mOnMarkerClickListener = new BaiduMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            mCurrClickMarker = marker;
            mCurrClickBitmap = marker.getIcon();
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_map_marker_selected);
            marker.setIcon(bitmapDescriptor);
            String uuid = marker.getExtraInfo().getString("uuid");
            // 当前marker坐标
            LatLng latLng = marker.getPosition();
            if (null != mMarkerClickListener) {
                mMarkerClickListener.onClick(uuid, latLng);
            }
            return false;
        }
    };

    /**
     * 百度定位监听
     */
    private BDAbstractLocationListener mBDAbstractLocationListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(16.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
    };


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onPause() {
        mMapView.onPause();
    }

    protected void onResume() {
        mMapView.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    protected void onStop() {
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
    }

    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
    }

    /**
     * 设置marker数据
     *
     * @param business marker标记点数据集
     */
    public void setMarkerDaces(List<ResponseModel.Body.Business> business) {
        if (null != business) {
            LatLng point;
            final Bundle extra = new Bundle();
            // 获取可移动范围所有的marker
            LatLngBounds latLngBounds = mBaiduMap.getMapStatusLimit();
            List<Marker> markers = mBaiduMap.getMarkersInBounds(latLngBounds);
            for (final ResponseModel.Body.Business data : business) {
                boolean isExits = false;
                if (markers != null && markers.size() > 0) {
                    for (Marker marker : markers) {
                        LatLng latLng = marker.getPosition();
                        Log.i("", data.latitude + "----" + latLng.longitude + "===========" + latLng.toString());
                        if (latLng.latitude == Double.valueOf(data.latitude) && latLng.longitude == Double.parseDouble(data.longitude)) {
                            isExits = true;
                        }
                    }
                }
                if (isExits) {
                    return;
                }
                //定义Maker坐标点
                point = new LatLng(Double.valueOf(data.latitude), Double.parseDouble(data.longitude));
                final LatLng finalPoint = point;
                int bitmapW = DensityUtils.dip2px(getContext(), Integer.parseInt(data.icon.unselected_width));
                int bitmapH = DensityUtils.dip2px(getContext(), Integer.parseInt(data.icon.unselected_height));
                try {
                    Glide.with(getContext())
                            .asBitmap()
                            .load(data.icon.unselected)
                            .override(bitmapW, bitmapH)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    //构建Marker图标
                                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
                                            .fromBitmap(resource);
                                    //构建MarkerOption，用于在地图上添加Marker
                                    MarkerOptions option = new MarkerOptions()
                                            .animateType(MarkerOptions.MarkerAnimateType.grow)
                                            .position(finalPoint)
                                            .icon(bitmapDescriptor);
                                    extra.putString("uuid", data.uuid);
                                    option.extraInfo(extra);
                                    //在地图上添加Marker，并显示
                                    mBaiduMap.addOverlay(option);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 地图状态变化完成监听
     */
    public interface OnMapStatusChanageFinishListener {
        /**
         * 变化完成
         *
         * @param latLng 经纬度
         */
        void onFinish(LatLng latLng);
    }

    public interface OnMarkerClickListener {
        void onClick(String uuid, LatLng latLng);
    }

    public void setOnMapStatusChanageFinishListener(OnMapStatusChanageFinishListener onMapStatusChanageFinishListener) {
        mOnMapStatusChanageFinishListener = onMapStatusChanageFinishListener;
    }

    public void setOnMarkerClickListener(OnMarkerClickListener onMarkerClickListener) {
        mMarkerClickListener = onMarkerClickListener;
    }
}
